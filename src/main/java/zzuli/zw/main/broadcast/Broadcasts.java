package zzuli.zw.main.broadcast;

import zzuli.zw.domain.JsonResult;
import zzuli.zw.domain.User;
import zzuli.zw.main.connection.RequestServerThread;
import zzuli.zw.main.factory.SocketContainer;
import zzuli.zw.main.factory.ThreadContainer;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.service.FriendService;
import zzuli.zw.service.FriendServiceImpl;
import zzuli.zw.main.aop.AopUtils;
import zzuli.zw.main.utils.ProtocolUtils;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * @ClassName Broadcast
 * @Description 向其他用户广播信息
 * @Author 索半斤
 * @Date 2021/2/12 19:57
 * @Version 1.0
 */
public class Broadcasts {
    FriendService friendService = AopUtils.aop(FriendServiceImpl.class);
    /**
     * @Author 索半斤
     * @Description 根据用户名给该用户名的用户所有在线好友广播消息
     * @Date 2022/2/4 22:54
     * @Param [responseMessage, username]
     * @return void
     **/
    public void broadcast(ResponseMessage responseMessage, String username){
        ThreadContainer<Object, Runnable> threadContainer = ThreadContainer.getInstance();
        List<Integer> list = friendService.findFriendIdsByUsername(username);
        if (list == null || list.size() == 0)return;
        for (Integer integer : list) {
            if (threadContainer.containsKey(integer)){
                try {
                    ((RequestServerThread)threadContainer.get(integer))
                            .handlerRequest(responseMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * @Author 索半斤
     * @Description 跟用用户id广播给该用户在线好友
     * @Date 2022/2/7 16:42
     * @Param [responseMessage, userId]
     * @return void
     **/
    public void broadcast(ResponseMessage responseMessage, Integer userId){
        ThreadContainer<Object, Runnable> threadContainer = ThreadContainer.getInstance();
        if (threadContainer.size() == 0)return;
        List<Integer> list = friendService.findFriendIdsByUserId(userId);
        if (list == null || list.size() == 0)return;
        for (Integer integer : list) {
            if (threadContainer.containsKey(integer)){
                try {
                    ((RequestServerThread)threadContainer.get(integer))
                            .handlerRequest(responseMessage);
                } catch (Exception e) {
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

    public void notifyOtherWhenHeaderChange(String username,String imagePath){
        ThreadContainer<Object, Runnable> instance = ThreadContainer.getInstance();
        instance.forEach((key,value) -> {
            JsonResult<User> jsonResult = new JsonResult<>();
            //jsonResult.setStatus(RequestType.REQUEST_UPDATE_CONTACT_IMAGE);
            User user = new User();
            user.setUsername(username);
            jsonResult.setMsg(imagePath);
            jsonResult.setData(user);
            Socket socket = SocketContainer.getSocket(key);
            try {
                //ProtocolUtils.send(jsonResult, socket);
                ProtocolUtils.send(null,socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    //int status = friendService.findStatus(username);
    //User user = userService.findDetailUserInfo(username);
    //user.setUsername(username);
    //user.setStatus(status);
     /*ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setRequest(-1);
            responseMessage.setContent(null);*/
            /*JsonResult<User> jsonResult = new JsonResult<>();
            jsonResult.setStatus(RequestType.REQUEST_UPDATE_CONTACT);
            Socket socket = SocketContainer.getSocket(key);*/
    //String remark = friendService.findFriendRemark(, username);
            /*if (remark != null) {
                if (remark.equals("remarkIsNull")) {
                    user.setRemark(null);
                }else{
                    user.setRemark(remark);
                }
                jsonResult.setData(user);
                try {
                    ProtocolUtils.send(jsonResult, socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
}
