package zzuli.zw.main.manager;

import zzuli.zw.main.connection.NioConnection;
import zzuli.zw.main.factory.SessionContainer;
import zzuli.zw.main.factory.SocketContainer;
import zzuli.zw.main.factory.ThreadContainer;
import zzuli.zw.main.interfaces.Session;
import zzuli.zw.main.model.IMUserSession;
import zzuli.zw.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * IM系统Session管理器
 * 特点：
 * 1. 基于连接状态管理Session
 * 2. 支持多设备登录
 * 3. 心跳检测连接存活
 * 4. 用户状态同步
 */
public class IMSessionManager {
    private static final Logger logger = LoggerFactory.getLogger(IMSessionManager.class);
    
    // 心跳超时时间（毫秒）
    private static final long HEARTBEAT_TIMEOUT = 120 * 1000; // 2分钟
    
    /**
     * 创建用户Session
     */
    public static IMUserSession createUserSession(String sessionId, User user, String deviceId) {
        IMUserSession session = new IMUserSession(sessionId);
        session.setUser(user);
        session.setDeviceId(deviceId);
        session.updateHeartbeat();
        
        SessionContainer.addSession(sessionId, session);
        logger.info("创建用户Session: {} 用户: {} 设备: {}", sessionId, user.getAccount(), deviceId);
        
        return session;
    }
    public static IMUserSession createUserSession(String sessionId, User user, String deviceId, NioConnection connection) {
        IMUserSession session = new IMUserSession(sessionId);
        session.setUser(user);
        session.setDeviceId(deviceId);
        session.setSocketChannel(connection);
        session.updateHeartbeat();

        SessionContainer.addSession(sessionId, session);
        logger.info("创建用户Session: {} 用户: {} 设备: {}", sessionId, user.getAccount(), deviceId);

        return session;
    }
    
    /**
     * 获取用户Session
     */
    public static IMUserSession getUserSession(String sessionId) {
        Session session = SessionContainer.getSession(sessionId);
        if (session instanceof IMUserSession) {
            return (IMUserSession) session;
        }
        return null;
    }
    
    /**
     * 根据用户ID获取所有活跃Session
     */
    public static List<IMUserSession> getUserSessions(Integer userId) {
        List<IMUserSession> sessions = new ArrayList<>();
        SessionContainer<String, Session> container = SessionContainer.getInstance();
        
        for (Session session : container.values()) {
            if (session instanceof IMUserSession) {
                IMUserSession imSession = (IMUserSession) session;
                if (imSession.isUserSession(userId) && imSession.isActive()) {
                    sessions.add(imSession);
                }
            }
        }
        
        return sessions;
    }
    
    /**
     * 检查用户是否在线
     */
    public static boolean isUserOnline(Integer userId) {
        List<IMUserSession> sessions = getUserSessions(userId);
        return !sessions.isEmpty();
    }
    
    /**
     * 获取用户在线设备数量
     */
    public static int getUserOnlineDeviceCount(Integer userId) {
        return getUserSessions(userId).size();
    }
    
    /**
     * 更新Session心跳
     */
    public static boolean updateHeartbeat(String sessionId) {
        IMUserSession session = getUserSession(sessionId);
        if (session != null && session.isActive()) {
            session.updateHeartbeat();
            return true;
        }
        return false;
    }
    
    /**
     * 检查Session心跳是否超时
     */
    public static boolean isHeartbeatTimeout(String sessionId) {
        IMUserSession session = getUserSession(sessionId);
        if (session == null) {
            return true;
        }
        return session.isHeartbeatTimeout(HEARTBEAT_TIMEOUT);
    }
    
    /**
     * 断开用户所有连接
     */
    public static void disconnectUser(Integer userId) {
        List<IMUserSession> sessions = getUserSessions(userId);
        for (IMUserSession session : sessions) {
            disconnectSession(session.getId());
        }
        logger.info("断开用户所有连接: {}", userId);
    }
    
    /**
     * 断开指定Session
     */
    public static void disconnectSession(String sessionId) {
        IMUserSession session = getUserSession(sessionId);
        if (session != null) {
            // 标记Session为非活跃
            session.setActive(false);
            
            // 移除Socket连接
            Integer userId = session.getUserId();
            if (userId != null) {
                //SocketContainer.remove(userId);
                //ThreadContainer.removeThread(userId);
            }
            
            // 移除Session
            SessionContainer.remove(sessionId);
            
            logger.info("断开Session: {} 用户: {}", sessionId, session.getUserAccount());
        }
    }
    
    /**
     * 清理超时的Session
     */
    public static int cleanupTimeoutSessions()  {
        SessionContainer<String, Session> container = SessionContainer.getInstance();
        int count = 0;
        for (Map.Entry<String, Session> entry : container.entrySet()) {
            String sessionId = entry.getKey();
            Session session = entry.getValue();
            
            if (session instanceof IMUserSession) {
                IMUserSession imSession = (IMUserSession) session;
                if (imSession.isHeartbeatTimeout(HEARTBEAT_TIMEOUT)) {
                    disconnectSession(sessionId);
                    NioConnection socketChannel = imSession.getSocketChannel();
                    if (!Objects.isNull(socketChannel)){
                        socketChannel.close();
                    }
                    count++;
                }
            }
        }
        return count;
    }
    
    /**
     * 获取在线用户数量
     */
    public static int getOnlineUserCount() {
        int count = 0;
        SessionContainer<String, Session> container = SessionContainer.getInstance();
        
        for (Session session : container.values()) {
            if (session instanceof IMUserSession) {
                IMUserSession imSession = (IMUserSession) session;
                if (imSession.isActive() && !imSession.isHeartbeatTimeout(HEARTBEAT_TIMEOUT)) {
                    count++;
                }
            }
        }
        
        return count;
    }
    
    /**
     * 获取Session统计信息
     */
    public static SessionStats getSessionStats() {
        SessionContainer<String, Session> container = SessionContainer.getInstance();
        int totalSessions = container.size();
        int activeSessions = 0;
        int timeoutSessions = 0;
        
        for (Session session : container.values()) {
            if (session instanceof IMUserSession) {
                IMUserSession imSession = (IMUserSession) session;
                if (imSession.isActive()) {
                    if (imSession.isHeartbeatTimeout(HEARTBEAT_TIMEOUT)) {
                        timeoutSessions++;
                    } else {
                        activeSessions++;
                    }
                }
            }
        }
        
        return new SessionStats(totalSessions, activeSessions, timeoutSessions);
    }
    
    /**
     * 获取所有活跃Session
     */
    public static List<IMUserSession> getAllActiveSessions() {
        List<IMUserSession> activeSessions = new ArrayList<>();
        SessionContainer<String, Session> container = SessionContainer.getInstance();
        
        for (Session session : container.values()) {
            if (session instanceof IMUserSession) {
                IMUserSession imSession = (IMUserSession) session;
                if (imSession.isActive() && !imSession.isHeartbeatTimeout(HEARTBEAT_TIMEOUT)) {
                    activeSessions.add(imSession);
                }
            }
        }
        
        return activeSessions;
    }
    
    /**
     * Session统计信息
     */
    public static class SessionStats {
        private final int totalSessions;
        private final int activeSessions;
        private final int timeoutSessions;
        
        public SessionStats(int totalSessions, int activeSessions, int timeoutSessions) {
            this.totalSessions = totalSessions;
            this.activeSessions = activeSessions;
            this.timeoutSessions = timeoutSessions;
        }
        
        public int getTotalSessions() { return totalSessions; }
        public int getActiveSessions() { return activeSessions; }
        public int getTimeoutSessions() { return timeoutSessions; }
        
        @Override
        public String toString() {
            return String.format("Session统计 - 总数: %d, 活跃: %d, 超时: %d",
                totalSessions, activeSessions, timeoutSessions);
        }
    }
}

