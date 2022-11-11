package zzuli.zw.main.broadcast;

import zzuli.zw.main.model.protocol.ResponseMessage;
import java.util.List;

public interface Broadcast {
    /**
     * @Author 索半斤
     * @Description 跟用用户id广播给该用户在线好友
     * @Date 2022/2/7 16:42
     * @Param [responseMessage, userId]
     * @return void
     **/
    void broadcast(ResponseMessage responseMessage, Integer userId);
    /**
     * @Author 索半斤
     * @Description 为特定用户广播消息
     * @Date 2022/2/7 17:29
     * @Param [responseMessage, id]
     * @return void
     **/
    void broadcast(ResponseMessage responseMessage,List<Integer> id);

    /**
     * @Author 索半斤
     * @Description 广播给所有在线用户
     * @Date 2022/2/7 0:14
     * @Param [responseMessage]
     * @return void
     **/
    void broadcast(ResponseMessage responseMessage);
}
