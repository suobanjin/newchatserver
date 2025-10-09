package zzuli.zw.main.connection;

import zzuli.zw.main.ioc.ServerContext;
import zzuli.zw.main.manager.IMSessionManager;
import zzuli.zw.main.scheduler.IMHeartbeatScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import zzuli.zw.broadcast.UserBroadcast;

/**
 * NIO服务器实现
 * 基于Selector的多路复用模型，支持高并发连接
 */
public class NioServer {
    private static final Logger logger = LoggerFactory.getLogger(NioServer.class);
    
    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private final ServerContext serverContext;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final NioConnectionManager connectionManager;
    private final NioProtocolHandler protocolHandler;
    private final NioRequestDispatcher requestDispatcher;
    
    private final int port;
    private final int maxConnections;
    
    public NioServer(int port, ServerContext serverContext) throws IOException {
        this(port, serverContext, 10000); // 默认最大10000连接
    }
    
    public NioServer(int port, ServerContext serverContext, int maxConnections) throws IOException {
        this.port = port;
        this.serverContext = serverContext;
        this.maxConnections = maxConnections;
        
        // 初始化NIO组件
        this.serverChannel = ServerSocketChannel.open();
        this.selector = Selector.open();
        this.connectionManager = new NioConnectionManager(maxConnections);
        this.protocolHandler = new NioProtocolHandler();
        this.requestDispatcher = new NioRequestDispatcher(serverContext);
        
        // 配置服务器通道
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        logger.info("NIO服务器初始化完成，端口: {}, 最大连接数: {}", port, maxConnections);
    }
    
    /**
     * 启动NIO服务器
     */
    public void start() throws IOException {
        if (running.compareAndSet(false, true)) {
            logger.info("启动NIO服务器，端口: {}", port);
            
            // 启动心跳检查
            IMHeartbeatScheduler.start();
            
            // 注册连接管理器到广播服务
            UserBroadcast.registerConnectionManager("NioServer-" + port, connectionManager);
            
            // 启动主循环
            run();
        } else {
            logger.warn("NIO服务器已在运行中");
        }
    }
    
    /**
     * 停止NIO服务器
     */
    public void stop() {
        if (running.compareAndSet(true, false)) {
            logger.info("停止NIO服务器");
            
            try {
                // 停止心跳检查
                IMHeartbeatScheduler.stop();
                
                // 关闭所有连接
                connectionManager.closeAllConnections();
                
                // 关闭选择器
                if (selector.isOpen()) {
                    selector.close();
                }
                
                // 关闭服务器通道
                if (serverChannel.isOpen()) {
                    serverChannel.close();
                }
                
                logger.info("NIO服务器已停止");
            } catch (IOException e) {
                logger.error("停止NIO服务器时发生错误", e);
            }
        }
    }
    
    /**
     * 主事件循环
     */
    private void run() {
        logger.info("NIO服务器主循环开始");
        
        while (running.get()) {
            try {
                // 等待事件，超时时间1秒
                int readyChannels = selector.select(1000);
                
                if (readyChannels == 0) {
                    continue; // 没有事件，继续循环
                }
                
                // 处理就绪的通道
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();
                    
                    try {
                        if (key.isValid()) {
                            if (key.isAcceptable()) {
                                handleAccept(key);
                            } else if (key.isReadable()) {
                                handleRead(key);
                            } else if (key.isWritable()) {
                                handleWrite(key);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("处理通道事件时发生错误", e);
                        closeConnection(key);
                    }
                }
                
            } catch (IOException e) {
                logger.error("NIO服务器主循环发生错误", e);
                break;
            }
        }
        
        logger.info("NIO服务器主循环结束");
    }
    
    /**
     * 处理新连接
     */
    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        
        if (clientChannel == null) {
            return; // 没有新连接
        }
        
        // 检查连接数限制
        if (connectionManager.getConnectionCount() >= maxConnections) {
            logger.warn("连接数已达上限，拒绝新连接: {}", clientChannel.getRemoteAddress());
            clientChannel.close();
            return;
        }
        
        // 配置客户端通道
        clientChannel.configureBlocking(false);
        clientChannel.socket().setTcpNoDelay(true);
        clientChannel.socket().setKeepAlive(true);
        
        // 注册到选择器
        SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
        
        // 创建连接对象
        NioConnection connection = new NioConnection(clientChannel, clientKey);
        clientKey.attach(connection);
        
        // 添加到连接管理器
        connectionManager.addConnection(connection);
        
        logger.info("新连接建立: {} 当前连接数: {}", 
            clientChannel.getRemoteAddress(), connectionManager.getConnectionCount());
    }
    
    /**
     * 处理读事件
     */
    private void handleRead(SelectionKey key) throws IOException {
        NioConnection connection = (NioConnection) key.attachment();
        if (connection == null) {
            return;
        }
        
        try {
            // 读取数据到缓冲区
            int bytesRead = connection.readFromChannel();
            if (bytesRead < 0) {
                // 连接关闭
                closeConnection(key);
                return;
            }
            
            if (bytesRead == 0) {
                return; // 没有数据可读
            }
            
            // 处理接收到的数据
            protocolHandler.handleReceivedData(connection, requestDispatcher);
            
        } catch (IOException e) {
            logger.warn("读取数据时发生错误: {}", e.getMessage());
            closeConnection(key);
        }
    }
    
    /**
     * 处理写事件
     */
    private void handleWrite(SelectionKey key) throws IOException {
        NioConnection connection = (NioConnection) key.attachment();
        if (connection == null) {
            return;
        }
        
        try {
            // 刷新写缓冲区
            connection.flushToChannel();
            
            // 如果写缓冲区为空，取消写事件监听
            if (connection.isWriteBufferEmpty()) {
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            }
            
        } catch (IOException e) {
            logger.warn("写入数据时发生错误: {}", e.getMessage());
            closeConnection(key);
        }
    }
    
    /**
     * 关闭连接
     */
    private void closeConnection(SelectionKey key) {
        NioConnection connection = (NioConnection) key.attachment();
        if (connection != null) {
            connection.close();
            connectionManager.removeConnection(connection);
            
            // 如果有关联的Session，清理Session
            String sessionId = connection.getSessionId();
            if (sessionId != null) {
                IMSessionManager.disconnectSession(sessionId);
            }
            
            logger.info("连接已关闭: {} 当前连接数: {}", 
                connection.getRemoteAddress(), connectionManager.getConnectionCount());
        }
        
        key.cancel();
    }
    
    /**
     * 获取服务器状态
     */
    public ServerStatus getServerStatus() {
        return new ServerStatus(
            running.get(),
            connectionManager.getConnectionCount(),
            maxConnections,
            port
        );
    }
    
    /**
     * 服务器状态信息
     */
    public static class ServerStatus {
        private final boolean running;
        private final int currentConnections;
        private final int maxConnections;
        private final int port;
        
        public ServerStatus(boolean running, int currentConnections, int maxConnections, int port) {
            this.running = running;
            this.currentConnections = currentConnections;
            this.maxConnections = maxConnections;
            this.port = port;
        }
        
        public boolean isRunning() { return running; }
        public int getCurrentConnections() { return currentConnections; }
        public int getMaxConnections() { return maxConnections; }
        public int getPort() { return port; }
        
        @Override
        public String toString() {
            return String.format("NIO服务器状态 - 运行: %s, 连接: %d/%d, 端口: %d",
                running, currentConnections, maxConnections, port);
        }
    }
}

