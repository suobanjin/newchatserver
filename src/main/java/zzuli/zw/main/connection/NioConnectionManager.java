package zzuli.zw.main.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * NIO连接管理器
 * 管理所有活跃的NIO连接
 */
public class NioConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(NioConnectionManager.class);
    
    private final ConcurrentHashMap<String, NioConnection> connections;
    private final AtomicInteger connectionCount;
    private final int maxConnections;
    
    public NioConnectionManager(int maxConnections) {
        this.connections = new ConcurrentHashMap<>();
        this.connectionCount = new AtomicInteger(0);
        this.maxConnections = maxConnections;
    }
    
    /**
     * 添加连接
     */
    public boolean addConnection(NioConnection connection) {
        if (connectionCount.get() >= maxConnections) {
            logger.warn("连接数已达上限: {}", maxConnections);
            return false;
        }
        
        String connectionId = generateConnectionId(connection);
        connections.put(connectionId, connection);
        connectionCount.incrementAndGet();
        
        logger.debug("添加连接: {} 当前连接数: {}", connectionId, connectionCount.get());
        return true;
    }
    
    /**
     * 移除连接
     */
    public void removeConnection(NioConnection connection) {
        String connectionId = findConnectionId(connection);
        if (connectionId != null) {
            connections.remove(connectionId);
            connectionCount.decrementAndGet();
            logger.debug("移除连接: {} 当前连接数: {}", connectionId, connectionCount.get());
        }
    }
    
    /**
     * 根据Session ID获取连接
     */
    public NioConnection getConnectionBySessionId(String sessionId) {
        for (NioConnection connection : connections.values()) {
            if (sessionId.equals(connection.getSessionId())) {
                return connection;
            }
        }
        return null;
    }
    
    /**
     * 根据设备ID获取连接
     */
    public List<NioConnection> getConnectionsByDeviceId(String deviceId) {
        List<NioConnection> deviceConnections = new ArrayList<>();
        for (NioConnection connection : connections.values()) {
            if (deviceId.equals(connection.getDeviceId())) {
                deviceConnections.add(connection);
            }
        }
        return deviceConnections;
    }
    
    /**
     * 获取所有连接
     */
    public Collection<NioConnection> getAllConnections() {
        return connections.values();
    }
    
    /**
     * 获取连接数量
     */
    public int getConnectionCount() {
        return connectionCount.get();
    }
    
    /**
     * 获取最大连接数
     */
    public int getMaxConnections() {
        return maxConnections;
    }
    
    /**
     * 检查是否还有可用连接槽
     */
    public boolean hasAvailableSlots() {
        return connectionCount.get() < maxConnections;
    }
    
    /**
     * 关闭所有连接
     */
    public void closeAllConnections() {
        logger.info("开始关闭所有连接，当前连接数: {}", connectionCount.get());
        
        for (NioConnection connection : connections.values()) {
            try {
                connection.close();
            } catch (Exception e) {
                logger.warn("关闭连接时发生错误", e);
            }
        }
        
        connections.clear();
        connectionCount.set(0);
        
        logger.info("所有连接已关闭");
    }
    
    /**
     * 清理超时连接
     */
    public int cleanupTimeoutConnections(long timeoutMs) {
        List<String> timeoutConnectionIds = new ArrayList<>();
        
        for (NioConnection connection : connections.values()) {
            if (connection.isHeartbeatTimeout(timeoutMs)) {
                String connectionId = findConnectionId(connection);
                if (connectionId != null) {
                    timeoutConnectionIds.add(connectionId);
                }
            }
        }
        
        // 关闭超时连接
        for (String connectionId : timeoutConnectionIds) {
            NioConnection connection = connections.remove(connectionId);
            if (connection != null) {
                connection.close();
                connectionCount.decrementAndGet();
                logger.debug("清理超时连接: {}", connectionId);
            }
        }
        
        if (!timeoutConnectionIds.isEmpty()) {
            logger.info("清理超时连接数量: {}", timeoutConnectionIds.size());
        }
        
        return timeoutConnectionIds.size();
    }
    
    /**
     * 获取连接统计信息
     */
    public ConnectionManagerStats getStats() {
        int totalConnections = connectionCount.get();
        int timeoutConnections = 0;
        long totalWriteQueueSize = 0;
        
        for (NioConnection connection : connections.values()) {
            if (connection.isHeartbeatTimeout(120000)) { // 2分钟超时
                timeoutConnections++;
            }
            totalWriteQueueSize += connection.getWriteQueueSize();
        }
        
        return new ConnectionManagerStats(
            totalConnections,
            timeoutConnections,
            totalWriteQueueSize,
            maxConnections
        );
    }
    
    /**
     * 生成连接ID
     */
    private String generateConnectionId(NioConnection connection) {
        return String.format("%s_%d", 
            connection.getRemoteAddress(), 
            connection.getConnectionTime());
    }
    
    /**
     * 查找连接ID
     */
    private String findConnectionId(NioConnection targetConnection) {
        for (String connectionId : connections.keySet()) {
            if (connections.get(connectionId) == targetConnection) {
                return connectionId;
            }
        }
        return null;
    }
    
    /**
     * 连接管理器统计信息
     */
    public static class ConnectionManagerStats {
        private final int totalConnections;
        private final int timeoutConnections;
        private final long totalWriteQueueSize;
        private final int maxConnections;
        
        public ConnectionManagerStats(int totalConnections, int timeoutConnections, 
                                   long totalWriteQueueSize, int maxConnections) {
            this.totalConnections = totalConnections;
            this.timeoutConnections = timeoutConnections;
            this.totalWriteQueueSize = totalWriteQueueSize;
            this.maxConnections = maxConnections;
        }
        
        public int getTotalConnections() { return totalConnections; }
        public int getTimeoutConnections() { return timeoutConnections; }
        public long getTotalWriteQueueSize() { return totalWriteQueueSize; }
        public int getMaxConnections() { return maxConnections; }
        public int getActiveConnections() { return totalConnections - timeoutConnections; }
        
        @Override
        public String toString() {
            return String.format("连接管理器统计 - 总数: %d, 活跃: %d, 超时: %d, 写队列: %d, 最大: %d",
                totalConnections, getActiveConnections(), timeoutConnections, totalWriteQueueSize, maxConnections);
        }
    }
}

