package zzuli.zw.main.connection;

import lombok.extern.slf4j.Slf4j;
import zzuli.zw.main.protocol.BinaryFrame;
import zzuli.zw.main.protocol.BinaryCodec;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * NIO连接对象
 * 管理单个客户端连接的状态、读写缓冲区、Session等
 */
@Slf4j
public class NioConnection {
    private final SocketChannel channel;
    private final SelectionKey selectionKey;
    private final ByteBuffer readBuffer;
    private final ConcurrentLinkedQueue<ByteBuffer> writeQueue;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    
    private String sessionId;
    private String deviceId;
    private long lastHeartbeat;
    private long connectionTime;
    private InetSocketAddress remoteAddress;
    
    // 缓冲区大小配置
    private static final int READ_BUFFER_SIZE = 64 * 1024; // 64KB
    private static final int MAX_WRITE_QUEUE_SIZE = 100; // 最大写队列大小
    
    public NioConnection(SocketChannel channel, SelectionKey selectionKey) {
        this.channel = channel;
        this.selectionKey = selectionKey;
        this.readBuffer = ByteBuffer.allocateDirect(READ_BUFFER_SIZE);
        this.writeQueue = new ConcurrentLinkedQueue<>();
        this.connectionTime = System.currentTimeMillis();
        this.lastHeartbeat = connectionTime;
        
        try {
            this.remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
        } catch (IOException e) {
            this.remoteAddress = null;
        }
    }
    
    /**
     * 从通道读取数据
     */
    public int readFromChannel() throws IOException {
        if (closed.get()) {
            return -1;
        }
        
        int bytesRead = channel.read(readBuffer);
        if (bytesRead > 0) {
            updateHeartbeat();
        }
        return bytesRead;
    }
    
    /**
     * 将写队列中的数据写入通道
     */
    public void flushToChannel() throws IOException {
        if (closed.get()) {
            return;
        }
        
        ByteBuffer buffer;
        while ((buffer = writeQueue.peek()) != null) {
            int bytesWritten = channel.write(buffer);
            if (bytesWritten == 0) {
                break; // 无法写入更多数据
            }
            
            if (!buffer.hasRemaining()) {
                writeQueue.poll(); // 缓冲区已完全写入，移除
            }
        }
    }
    
    /**
     * 发送二进制帧
     */
    public void sendFrame(BinaryFrame frame) throws IOException {
        if (closed.get()) {
            throw new IOException("连接已关闭");
        }
        
        ByteBuffer[] buffers = frame.encode();
        for (ByteBuffer buffer : buffers) {
            if (writeQueue.size() >= MAX_WRITE_QUEUE_SIZE) {
                throw new IOException("写队列已满，连接可能过慢");
            }
            writeQueue.offer(buffer.duplicate());
        }
        
        // 注册写事件
        selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
        selectionKey.selector().wakeup();
    }
    
    /**
     * 发送字节数组
     */
    public void sendBytes(byte[] data) throws IOException {
        if (closed.get()) {
            throw new IOException("连接已关闭");
        }
        
        if (writeQueue.size() >= MAX_WRITE_QUEUE_SIZE) {
            throw new IOException("写队列已满，连接可能过慢");
        }
        
        writeQueue.offer(ByteBuffer.wrap(data));
        
        // 注册写事件
        selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
        selectionKey.selector().wakeup();
    }
    
    /**
     * 获取读缓冲区
     */
    public ByteBuffer getReadBuffer() {
        return readBuffer;
    }
    
    /**
     * 检查写缓冲区是否为空
     */
    public boolean isWriteBufferEmpty() {
        return writeQueue.isEmpty();
    }
    
    /**
     * 获取写队列大小
     */
    public int getWriteQueueSize() {
        return writeQueue.size();
    }
    
    /**
     * 更新心跳时间
     */
    public void updateHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }
    
    /**
     * 检查心跳是否超时
     */
    public boolean isHeartbeatTimeout(long timeoutMs) {
        return System.currentTimeMillis() - lastHeartbeat > timeoutMs;
    }
    
    /**
     * 设置Session ID
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    /**
     * 获取Session ID
     */
    public String getSessionId() {
        return sessionId;
    }
    
    /**
     * 设置设备ID
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    /**
     * 获取设备ID
     */
    public String getDeviceId() {
        return deviceId;
    }
    
    /**
     * 获取远程地址
     */
    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }
    
    /**
     * 获取连接时间
     */
    public long getConnectionTime() {
        return connectionTime;
    }
    
    /**
     * 获取最后心跳时间
     */
    public long getLastHeartbeat() {
        return lastHeartbeat;
    }
    
    /**
     * 检查连接是否已关闭
     */
    public boolean isClosed() {
        return closed.get() || !channel.isOpen();
    }
    
    /**
     * 关闭连接
     */
    public void close() {
        if (closed.compareAndSet(false, true)) {
            try {
                // 清空写队列
                writeQueue.clear();
                // 关闭通道
                if (channel.isOpen()) {
                    channel.close();
                }
                // 取消选择键
                if (selectionKey.isValid()) {
                    selectionKey.cancel();
                }
                
            } catch (IOException e) {
                log.error("Error closing connection", e);
                // 忽略关闭时的异常
            }
        }
    }
    
    /**
     * 获取连接信息
     */
    public String getConnectionInfo() {
        return String.format("Connection[%s] Session[%s] Device[%s] Heartbeat[%d] Queue[%d]",
            remoteAddress, sessionId, deviceId, lastHeartbeat, writeQueue.size());
    }
    
    /**
     * 获取连接统计信息
     */
    public ConnectionStats getStats() {
        return new ConnectionStats(
            connectionTime,
            lastHeartbeat,
            writeQueue.size(),
            readBuffer.position(),
            readBuffer.limit()
        );
    }
    
    /**
     * 连接统计信息
     */
    public static class ConnectionStats {
        private final long connectionTime;
        private final long lastHeartbeat;
        private final int writeQueueSize;
        private final int readBufferPosition;
        private final int readBufferLimit;
        
        public ConnectionStats(long connectionTime, long lastHeartbeat, int writeQueueSize, 
                            int readBufferPosition, int readBufferLimit) {
            this.connectionTime = connectionTime;
            this.lastHeartbeat = lastHeartbeat;
            this.writeQueueSize = writeQueueSize;
            this.readBufferPosition = readBufferPosition;
            this.readBufferLimit = readBufferLimit;
        }
        
        public long getConnectionTime() { return connectionTime; }
        public long getLastHeartbeat() { return lastHeartbeat; }
        public int getWriteQueueSize() { return writeQueueSize; }
        public int getReadBufferPosition() { return readBufferPosition; }
        public int getReadBufferLimit() { return readBufferLimit; }
        
        @Override
        public String toString() {
            return String.format("连接统计 - 连接时间: %d, 心跳: %d, 写队列: %d, 读缓冲: %d/%d",
                connectionTime, lastHeartbeat, writeQueueSize, readBufferPosition, readBufferLimit);
        }
    }
}

