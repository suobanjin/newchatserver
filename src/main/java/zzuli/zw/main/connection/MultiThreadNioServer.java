package zzuli.zw.main.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzuli.zw.main.ioc.ServerContext;

public class MultiThreadNioServer {

    private static final Logger logger = LoggerFactory.getLogger(MultiThreadNioServer.class);

    private final ServerSocketChannel serverChannel;
    private final Selector mainSelector;
    private final ServerContext serverContext;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private final int port;
    private final int maxConnections;
    private final int workerThreadCount;

    private final ExecutorService bossExecutor;
    private final ExecutorService workerExecutor;
    private final ExecutorService businessExecutor;
    private final Worker[] workers;

    private final AtomicInteger nextWorkerIndex = new AtomicInteger(0);
    private final AtomicInteger globalConnectionCount = new AtomicInteger(0);

    public MultiThreadNioServer(int port, ServerContext serverContext) throws IOException {
        this(port, serverContext, 10000, Runtime.getRuntime().availableProcessors());
    }

    public MultiThreadNioServer(int port, ServerContext serverContext, int maxConnections, int workerThreadCount) throws IOException {
        this.port = port;
        this.serverContext = serverContext;
        this.maxConnections = maxConnections;
        this.workerThreadCount = workerThreadCount;

        // 主线程
        this.bossExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, "BossThread"));

        // 工作线程
        this.workerExecutor = Executors.newFixedThreadPool(workerThreadCount);
        this.workers = new Worker[workerThreadCount];
        for (int i = 0; i < workerThreadCount; i++) {
            workers[i] = new Worker(i);
            workerExecutor.submit(workers[i]);
        }

        this.businessExecutor = new ThreadPoolExecutor(
                Math.max(2, workerThreadCount),
                Math.max(4, workerThreadCount * 2),
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactory() {
                    private final AtomicInteger threadNum = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "BusinessExecutor-" + threadNum.incrementAndGet());
                    }
                },
                new ThreadPoolExecutor.AbortPolicy()
        );

        // NIO 初始化
        this.serverChannel = ServerSocketChannel.open();
        this.mainSelector = Selector.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.register(mainSelector, SelectionKey.OP_ACCEPT);

        logger.info("MultiThreadNioServer initialized on port {}, maxConnections {}, workerCount {}",
                port, maxConnections, workerThreadCount);
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            logger.warn("Server already running");
            return;
        }
        logger.info("Starting server on port {}", port);
        bossExecutor.submit(this::runMainReactor);
    }

    public void stop() throws InterruptedException {
        if (!running.compareAndSet(true, false)) return;
        logger.info("Stopping server...");
        try {
            mainSelector.wakeup();
            serverChannel.close();
        } catch (IOException e) {
            logger.error("Error closing server channel", e);
        }

        bossExecutor.shutdown();
        for (Worker w : workers) {
            w.shutdown();
        }
        workerExecutor.shutdown();
        workerExecutor.awaitTermination(30, TimeUnit.SECONDS);

        businessExecutor.shutdown();
        businessExecutor.awaitTermination(30, TimeUnit.SECONDS);

        logger.info("Server stopped");
    }

    private void runMainReactor() {
        logger.info("Main Reactor started");
        while (running.get()) {
            try {
                if (mainSelector.select(1000) == 0) continue;
                Iterator<SelectionKey> iterator = mainSelector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) continue;
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    }
                }
            } catch (IOException e) {
                logger.error("Main Reactor error", e);
            }
        }
        logger.info("Main Reactor stopped");
    }

    private void handleAccept(SelectionKey key) {
        try {
            SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
            if (sc != null) {
                sc.configureBlocking(false);
                int idx = nextWorkerIndex.getAndIncrement() % workerThreadCount;
                Worker w = workers[idx];
                w.enqueueRegisterChannel(sc);
            }
        } catch (IOException e) {
            logger.error("Accept failed", e);
        }
    }

    private class Worker implements Runnable {
        private final int id;
        private final Selector selector;
        private final AtomicBoolean running = new AtomicBoolean(true);
        private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        private final NioConnectionManager connectionManager = new NioConnectionManager(maxConnections / workerThreadCount);
        private final NioProtocolHandler protocolHandler = new NioProtocolHandler();
        private final NioRequestDispatcher requestDispatcher = new NioRequestDispatcher(serverContext);

        Worker(int id) throws IOException {
            this.id = id;
            this.selector = Selector.open();
        }

        public void enqueueRegisterChannel(SocketChannel sc) {
            taskQueue.add(() -> registerChannel(sc));
            selector.wakeup();
        }

        private void registerChannel(SocketChannel sc) {
            try {
                sc.socket().setTcpNoDelay(true);
                sc.socket().setKeepAlive(true);
                SelectionKey key = sc.register(selector, SelectionKey.OP_READ);
                NioConnection conn = new NioConnection(sc, key);
                key.attach(conn);
                connectionManager.addConnection(conn);
                globalConnectionCount.incrementAndGet();
                logger.info("Worker {} new connection {}", id, sc.getRemoteAddress());
            } catch (IOException e) {
                logger.error("Worker {} registerChannel failed", id, e);
                try { sc.close(); } catch (IOException ex) {}
            }
        }

        private void processTaskQueue() {
            Runnable task;
            while ((task = taskQueue.poll()) != null) {
                try { task.run(); } catch (Exception e) { logger.error("Worker {} task error", id, e); }
            }
        }

        @Override
        public void run() {
            logger.info("Worker {} started", id);
            while (running.get()) {
                try {
                    processTaskQueue();
                    if (selector.select(1000) == 0) continue;
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();
                        if (!key.isValid()) continue;
                        if (key.isReadable()) handleRead(key);
                        else if (key.isWritable()) handleWrite(key);
                    }
                } catch (IOException e) {
                    logger.error("Worker {} selector error", id, e);
                }
            }
            logger.info("Worker {} stopped", id);
        }

        private void handleRead(SelectionKey key) {
            NioConnection conn = (NioConnection) key.attachment();
            if (conn == null) return;

            try {
                int bytesRead = conn.readFromChannel();
                if (bytesRead <= 0) {
                    closeConnection(key);
                    return;
                }

                try {
                    businessExecutor.submit(() -> {
                        try {
                            protocolHandler.handleReceivedData(conn, requestDispatcher);
                        } catch (Exception e) {
                            logger.error("Worker {} business error", id, e);
                            taskQueue.add(() -> closeConnection(key));
                            selector.wakeup();
                        }
                    });
                } catch (RejectedExecutionException rex) {
                    logger.warn("Business queue full, closing connection");
                    taskQueue.add(() -> closeConnection(key));
                    selector.wakeup();
                }
            } catch (IOException e) {
                logger.warn("Worker {} read error {}", id, e.getMessage());
                closeConnection(key);
            }
        }

        private void handleWrite(SelectionKey key) {
            NioConnection conn = (NioConnection) key.attachment();
            if (conn == null) return;

            try {
                conn.flushToChannel();
                if (conn.isWriteBufferEmpty()) {
                    key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                }
            } catch (IOException e) {
                logger.warn("Worker {} write error {}", id, e.getMessage());
                closeConnection(key);
            }
        }

        private void closeConnection(SelectionKey key) {
            NioConnection conn = (NioConnection) key.attachment();
            if (conn != null) {
                conn.close();
                connectionManager.removeConnection(conn);
                globalConnectionCount.decrementAndGet();
            }
            key.cancel();
        }

        public void shutdown() {
            running.set(false);
            selector.wakeup();
            taskQueue.add(() -> {
                connectionManager.closeAllConnections();
            });
        }
    }
}
