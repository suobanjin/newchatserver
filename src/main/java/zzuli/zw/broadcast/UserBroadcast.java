package zzuli.zw.broadcast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.main.annotation.Injection;
import zzuli.zw.main.broadcast.Broadcast;
import zzuli.zw.main.connection.NioConnection;
import zzuli.zw.main.connection.NioConnectionManager;
import zzuli.zw.main.connection.NioServer;
import zzuli.zw.main.connection.RequestServerThread;
import zzuli.zw.main.factory.SocketContainer;
import zzuli.zw.main.factory.ThreadContainer;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.protocol.BinaryCodec;
import zzuli.zw.main.utils.ProtocolUtils;
import zzuli.zw.pojo.model.StatusType;
import zzuli.zw.service.interfaces.FriendService;
import zzuli.zw.service.interfaces.UserService;

import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户广播服务
 * 支持BIO和NIO双模式的消息广播
 */

/**
 * @ClassName UserBroadcast
 * @Description: 该类用来实现广播消息，需要实现Broadcast接口，并且只能有一个
 * @Author 索半斤
 * @Datetime 2022年 11月 09日 21:50
 * @Version: 1.0
 */
@Bean("userBroadcast")
public class UserBroadcast implements Broadcast {
    private static final Logger logger = LoggerFactory.getLogger(UserBroadcast.class);
    
    @Injection(name = "friendService")
    private FriendService friendService;
    @Injection(name = "userService")
    private UserService userService;
    
    // 存储NIO服务器的连接管理器
    private static final Map<String, NioConnectionManager> connectionManagers = new ConcurrentHashMap<>();
    
    /**
     * 注册NIO连接管理器
     */
    public static void registerConnectionManager(String serverId, NioConnectionManager manager) {
        connectionManagers.put(serverId, manager);
        logger.info("注册NIO连接管理器: {}", serverId);
    }
    
    /**
     * 获取连接管理器（优先获取NIO的，没有则返回null）
     */
    private NioConnectionManager getConnectionManager() {
        if (!connectionManagers.isEmpty()) {
            return connectionManagers.values().iterator().next();
        }
        return null;
    }
    /**
     * @Author 索半斤
     * @Description 跟用用户id广播给该用户在线好友
     * @Date 2022/2/7 16:42
     * @Param [responseMessage, userId]
     * @return void
     **/
    @Override
    public void broadcast(ResponseMessage responseMessage, Integer userId){
        // 优先使用NIO模式
        NioConnectionManager manager = getConnectionManager();
        if (manager != null) {
            broadcastNIO(responseMessage, userId);
            return;
        }
        
        // 如果没有NIO管理器，则使用BIO模式
        broadcastBIO(responseMessage, userId);
    }
    
    /**
     * NIO模式广播给用户好友
     */
    private void broadcastNIO(ResponseMessage responseMessage, Integer userId) {
        NioConnectionManager manager = getConnectionManager();
        if (manager == null || manager.getConnectionCount() == 0) return;
        
        List<Integer> friendIds = friendService.findFriendIds(userId);
        if (friendIds == null || friendIds.isEmpty()) return;
        
        try {
            byte[] frameData = BinaryCodec.encodeFrame(responseMessage);
            for (Integer friendId : friendIds) {
                // 转换用户ID为字符串格式（假设Session ID或设备ID格式是user_xxx）
                String deviceId = "user_" + friendId;
                List<NioConnection> connections = manager.getConnectionsByDeviceId(deviceId);
                
                for (NioConnection connection : connections) {
                    if (connection != null && !connection.isClosed()) {
                        connection.sendBytes(frameData);
                        break; // 给每个用户发送一次即可
                    }
                }
            }
        } catch (Exception e) {
            logger.error("NIO广播消息时发生错误", e);
        }
    }
    
    /**
     * BIO模式广播给用户好友
     */
    private void broadcastBIO(ResponseMessage responseMessage, Integer userId) {
        SocketContainer<Object, Socket> socketContainer = SocketContainer.getInstance();
        if (socketContainer.size() == 0) return;
        
        List<Integer> friendIds = friendService.findFriendIds(userId);
        if (friendIds == null || friendIds.isEmpty()) return;
        
        for (Integer friendId : friendIds) {
            Socket socket = SocketContainer.getSocket(friendId);
            if (socket != null && !socket.isClosed()) {
                try {
                    ProtocolUtils.send(responseMessage, socket);
                    break;
                } catch (Exception e) {
                    logger.error("BIO广播消息时发生错误", e);
                }
            }
        }
    }
    /**
    * @Author 索半斤
    * @Description //TODO
    * @Date 14:57 2022/11/16
    * @Param [responseMessage, userId]
    * @return void
    **/
    public void closeBroadcast(ResponseMessage responseMessage, Integer userId){
        userService.updateUserStatus(userId, StatusType.OFFLINE);
        
        // 优先使用NIO模式
        NioConnectionManager manager = getConnectionManager();
        if (manager != null) {
            closeBroadcastNIO(responseMessage, userId);
            return;
        }
        
        // 如果没有NIO管理器，则使用BIO模式
        closeBroadcastBIO(responseMessage, userId);
    }
    
    /**
     * NIO模式关闭广播
     */
    private void closeBroadcastNIO(ResponseMessage responseMessage, Integer userId) {
        NioConnectionManager manager = getConnectionManager();
        if (manager == null || manager.getConnectionCount() == 0) return;
        
        List<Integer> friendIds = friendService.findFriendIds(userId);
        if (friendIds == null || friendIds.isEmpty()) return;
        
        try {
            byte[] frameData = BinaryCodec.encodeFrame(responseMessage);
            for (Integer friendId : friendIds) {
                String deviceId = "user_" + friendId;
                List<NioConnection> connections = manager.getConnectionsByDeviceId(deviceId);
                
                for (NioConnection connection : connections) {
                    if (connection != null && !connection.isClosed()) {
                        connection.sendBytes(frameData);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("NIO关闭广播时发生错误", e);
        }
    }
    
    /**
     * BIO模式关闭广播
     */
    private void closeBroadcastBIO(ResponseMessage responseMessage, Integer userId) {
        SocketContainer<Object, Socket> socketContainer = SocketContainer.getInstance();
        if (socketContainer.size() == 0) return;
        
        List<Integer> friendIds = friendService.findFriendIds(userId);
        if (friendIds == null || friendIds.isEmpty()) return;
        
        for (Integer friendId : friendIds) {
            if (socketContainer.containsKey(friendId)) {
                try {
                    Socket socket = socketContainer.get(friendId);
                    if (socket != null && !socket.isClosed()) {
                        ProtocolUtils.send(responseMessage, socket);
                    }
                } catch (Exception e) {
                    logger.error("BIO关闭广播时发生错误", e);
                }
            }
        }
    }
    /**
     * @Author 索半斤
     * @Description 为特定用户广播消息
     * @Date 2022/2/7 17:29
     * @Param [responseMessage, id]
     * @return void
     **/
    @Override
    public void broadcast(ResponseMessage responseMessage, List<Integer> ids){
        if (ids == null || ids.isEmpty()) return;
        
        // 优先使用NIO模式
        NioConnectionManager manager = getConnectionManager();
        if (manager != null) {
            broadcastNIO(responseMessage, ids);
            return;
        }
        
        // 如果没有NIO管理器，则使用BIO模式
        broadcastBIO(responseMessage, ids);
    }
    
    /**
     * NIO模式广播给特定用户列表
     */
    private void broadcastNIO(ResponseMessage responseMessage, List<Integer> ids) {
        NioConnectionManager manager = getConnectionManager();
        if (manager == null || manager.getConnectionCount() == 0) return;
        
        try {
            byte[] frameData = BinaryCodec.encodeFrame(responseMessage);
            for (Integer userId : ids) {
                String deviceId = "user_" + userId;
                List<NioConnection> connections = manager.getConnectionsByDeviceId(deviceId);
                
                for (NioConnection connection : connections) {
                    if (connection != null && !connection.isClosed()) {
                        connection.sendBytes(frameData);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("NIO广播给特定用户列表时发生错误", e);
        }
    }
    
    /**
     * BIO模式广播给特定用户列表
     */
    private void broadcastBIO(ResponseMessage responseMessage, List<Integer> ids) {
        ThreadContainer<Object, Runnable> instance = ThreadContainer.getInstance();
        if (instance.size() == 0) return;
        
        for (Integer userId : ids) {
            if (instance.containsKey(userId)) {
                try {
                    ((RequestServerThread) instance.get(userId)).handlerRequest(responseMessage);
                } catch (Exception e) {
                    logger.error("BIO广播给特定用户列表时发生错误", e);
                }
            }
        }
    }

    /**
     * @Author 索半斤
     * @Description 广播给所有在线用户
     * @Date 2022/2/7 0:14
     * @Param [responseMessage]
     * @return void
     **/
    @Override
    public void broadcast(ResponseMessage responseMessage){
        // 优先使用NIO模式
        NioConnectionManager manager = getConnectionManager();
        if (manager != null) {
            broadcastAllNIO(responseMessage);
            return;
        }
        
        // 如果没有NIO管理器，则使用BIO模式
        broadcastAllBIO(responseMessage);
    }
    
    /**
     * NIO模式广播给所有在线用户
     */
    private void broadcastAllNIO(ResponseMessage responseMessage) {
        NioConnectionManager manager = getConnectionManager();
        if (manager == null || manager.getConnectionCount() == 0) return;
        
        try {
            byte[] frameData = BinaryCodec.encodeFrame(responseMessage);
            for (NioConnection connection : manager.getAllConnections()) {
                if (connection != null && !connection.isClosed()) {
                    connection.sendBytes(frameData);
                }
            }
        } catch (Exception e) {
            logger.error("NIO广播给所有用户时发生错误", e);
        }
    }
    
    /**
     * BIO模式广播给所有在线用户
     */
    private void broadcastAllBIO(ResponseMessage responseMessage) {
        ThreadContainer<Object, Runnable> threadContainer = ThreadContainer.getInstance();
        if (threadContainer.size() == 0) return;
        
        threadContainer.forEach((key, value) -> {
            try {
                ((RequestServerThread) value).handlerRequest(responseMessage);
            } catch (Exception e) {
                logger.error("BIO广播给所有用户时发生错误", e);
            }
        });
    }
}
