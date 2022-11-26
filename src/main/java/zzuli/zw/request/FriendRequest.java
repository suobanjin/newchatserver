package zzuli.zw.request;

import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.annotation.*;
import zzuli.zw.config.Router;
import zzuli.zw.pojo.User;
import zzuli.zw.service.interfaces.FriendService;
import zzuli.zw.service.interfaces.UserService;

/**
 * @author 索半斤
 * @description 处理好友相关请求
 * @date 2022/1/22
 * @className FriendRequest
 */
@Request
@Bean("friendRequest")
public class FriendRequest {
    @Injection(name = "friendService")
    private FriendService friendService;
    /**
     * @Author 索半斤
     * @Description 查找好友
     * @Date 2022/1/24 21:38
     * @Param [request,response]
     * @return void
     **/
    @RequestMapping(Router.UPDATE_FRIEND_INFO)
    public ResponseMessage findFriends(@ParameterName("user") User user)  {
        String username = user.getUsername();
        if (username != null && username.length()  != 0){
            System.out.println(username);
        }
        //User objectFromString = ObjectMapperFactory.getObjectFromString(request.getResultContent(), User.class);
        return null;
    }

    @RequestMapping(Router.UPDATE_FRIEND_STATUS)
    public ResponseMessage updateFriendStatus(User user){
        String username = user.getUsername();
        return null;
    }

    /**
    * @Author 索半斤
    * @Description 点赞信息更新
    * @Date 17:11 2022/11/26
    * @Param [request, response, num, friendId]
    * @return zzuli.zw.main.model.protocol.ResponseMessage
    **/
    @RequestMapping(Router.UPDATE_FRIEND_LIKE)
    public ResponseMessage updateLike(RequestParameter request,
                                      ResponseParameter response,
                                      @ParameterName("num")int num,
                                      @ParameterName("friendId")int friendId){
        return null;
    }

}
