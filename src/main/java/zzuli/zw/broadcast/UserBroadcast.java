package zzuli.zw.broadcast;

import zzuli.zw.main.annotation.Bean;
import zzuli.zw.main.annotation.Injection;
import zzuli.zw.main.broadcast.Broadcast;
import zzuli.zw.main.connection.RequestServerThread;
import zzuli.zw.main.factory.SocketContainer;
import zzuli.zw.main.factory.ThreadContainer;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.utils.ProtocolUtils;
import zzuli.zw.pojo.model.StatusType;
import zzuli.zw.service.interfaces.FriendService;
import zzuli.zw.service.interfaces.UserService;

import java.net.Socket;
import java.util.List;

/**
 * @ClassName UserBroadcast
 * @Description: 该类用来实现广播消息，需要实现Broadcast接口，并且只能有一个
 * @Author 索半斤
 * @Datetime 2022年 11月 09日 21:50
 * @Version: 1.0
 */
@Bean("userBroadcast")
public class UserBroadcast implements Broadcast {
    @Injection(name = "friendService")
    private FriendService friendService;
    @Injection(name = "userService")
    private UserService userService;
    /**
     * @Author 索半斤
     * @Description 跟用用户id广播给该用户在线好友
     * @Date 2022/2/7 16:42
     * @Param [responseMessage, userId]
     * @return void
     **/
    @Override
    public void broadcast(ResponseMessage responseMessage, Integer userId){
        SocketContainer<Object, Socket> socketContainer = SocketContainer.getInstance();
        if (socketContainer.size() == 0)return;
        List<Integer> list = friendService.findFriendIds(userId);
        if (list == null || list.size() == 0)return;
        for (Integer integer : list) {
            Socket socket = SocketContainer.getSocket(integer);
            if (socket != null && !socket.isClosed()){
                try {
                    ProtocolUtils.send(responseMessage, socketContainer.get(integer));
                    break;
                }catch (Exception e){
                    e.printStackTrace();
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
        SocketContainer<Object, Socket> socketContainer = SocketContainer.getInstance();
        if (socketContainer.size() == 0)return;
        List<Integer> list = friendService.findFriendIds(userId);
        if (list == null || list.size() == 0)return;
        for (Integer integer : list) {
            if (socketContainer.containsKey(integer)){
                try {
                    if (!socketContainer.get(integer).isClosed()) {
                        ProtocolUtils.send(responseMessage, socketContainer.get(integer));
                    }
                }catch (Exception e){
                    e.printStackTrace();
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
    public void broadcast(ResponseMessage responseMessage,List<Integer> id){
        if (id == null || id.size() == 0)return;
        ThreadContainer<Object, Runnable> instance = ThreadContainer.getInstance();
        if (instance.size() == 0)return;
        for (Integer friendId : id) {
            if (instance.containsKey(friendId)){
                try {
                    ((RequestServerThread) instance.get(friendId)).handlerRequest(responseMessage);
                }catch (Exception e){
                    e.printStackTrace();
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
        ThreadContainer<Object, Runnable> threadContainer = ThreadContainer.getInstance();
        if (threadContainer.size() == 0)return;
        threadContainer.forEach((key,value) -> {
            try {
                ((RequestServerThread)value).handlerRequest(responseMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
