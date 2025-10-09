package zzuli.zw.main.model;

import zzuli.zw.main.connection.NioConnection;
import zzuli.zw.main.interfaces.Session;
import zzuli.zw.main.manager.IMSessionManager;
import zzuli.zw.pojo.User;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IM系统专用的Session实现
 * 特点：
 * 1. 与TCP连接绑定，连接断开即失效
 * 2. 存储用户临时状态和好友信息
 * 3. 支持多设备登录管理
 * 4. 无时间过期机制，基于连接状态
 */
public class IMUserSession implements Session {
    private final Map<String, Object> attributes;
    private String id;
    private long creatorTime;
    private User user; // 当前登录用户
    private String deviceId; // 设备ID
    private boolean isActive; // 连接是否活跃
    private long lastHeartbeat; // 最后心跳时间
    private NioConnection connection;
    public IMUserSession(String id) {
        this.attributes = new ConcurrentHashMap<>();
        this.id = id;
        this.creatorTime = System.currentTimeMillis();
        this.isActive = true;
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public IMUserSession(String id, NioConnection connection) {
        this(id);
        this.connection = connection;
    }

    public void setSocketChannel(NioConnection connection) {
        this.connection = connection;
    }

    public NioConnection getSocketChannel() {
        return connection;
    }

    @Override
    public boolean setAttribute(String key, Object value) {
        if (key == null || !isActive) {
            return false;
        }
        return attributes.put(key, value) != null;
    }

    @Override
    public Object getAttribute(String key) {
        if (key == null || !isActive) {
            return null;
        }
        return attributes.get(key);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void removeAttribute(String key) {
        if (!attributes.containsKey(key)) return;
        attributes.remove(key);
    }

    @Override
    public long getCreatorTime() {
        return this.creatorTime;
    }

    @Override
    public List<Object> getAttributes() {
        if (attributes.isEmpty()) return null;
        List<Object> list = new ArrayList<>();
        list.addAll(attributes.values());
        return list;
    }

    @Override
    public List<String> getAttributeNames() {
        if (attributes.isEmpty()) return null;
        List<String> list = new ArrayList<>();
        list.addAll(attributes.keySet());
        return list;
    }

    /**
     * 设置当前登录用户
     */
    public void setUser(User user) {
        this.user = user;
        setAttribute("user", user);
    }

    /**
     * 获取当前登录用户
     */
    public User getUser() {
        if (user == null) {
            user = (User) getAttribute("user");
        }
        return user;
    }

    /**
     * 设置设备ID
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        setAttribute("deviceId", deviceId);
    }

    /**
     * 获取设备ID
     */
    public String getDeviceId() {
        if (deviceId == null) {
            deviceId = (String) getAttribute("deviceId");
        }
        return deviceId;
    }

    /**
     * 检查Session是否活跃（连接是否有效）
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * 设置Session活跃状态
     */
    public void setActive(boolean active) {
        this.isActive = active;
    }

    /**
     * 更新心跳时间
     */
    public void updateHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
        setAttribute("lastHeartbeat", lastHeartbeat);
    }

    /**
     * 获取最后心跳时间
     */
    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    /**
     * 检查心跳是否超时（超过指定时间未收到心跳）
     */
    public boolean isHeartbeatTimeout(long timeoutMs) {
        return System.currentTimeMillis() - lastHeartbeat > timeoutMs;
    }

    /**
     * 获取用户ID
     */
    public Integer getUserId() {
        User user = getUser();
        return user != null ? user.getId() : null;
    }

    /**
     * 获取用户账号
     */
    public String getUserAccount() {
        User user = getUser();
        return user != null ? user.getAccount() : null;
    }

    /**
     * 检查是否为指定用户的Session
     */
    public boolean isUserSession(Integer userId) {
        Integer sessionUserId = getUserId();
        return sessionUserId != null && sessionUserId.equals(userId);
    }

    /**
     * 清理Session数据
     */
    public void clear() {
        attributes.clear();
        this.user = null;
        this.deviceId = null;
        this.isActive = false;
        this.getSocketChannel().close();
    }

    /**
     * 获取Session信息摘要
     */
    public String getSessionInfo() {
        return String.format("Session[%s] User[%s] Device[%s] Active[%s] Heartbeat[%d]",
            id, getUserAccount(), deviceId, isActive, lastHeartbeat);
    }
}

