package zzuli.zw.request;

import zzuli.zw.main.model.ResponseMessage;
import zzuli.zw.main.annotation.*;
import zzuli.zw.config.Router;
import zzuli.zw.domain.User;
import zzuli.zw.service.UserService;

/**
 * @author 索半斤
 * @description 处理好友相关请求
 * @date 2022/1/22
 * @className FriendRequest
 */
@Request
@Bean("friendRequest")
public class FriendRequest {
    //private UserService userService = AopUtils.aop(UserServiceImpl.class);
    @Injection(name = "userService")
    private UserService userService;
    /**
     * @Author 索半斤
     * @Description 查找好友
     * @Date 2022/1/24 21:38
     * @Param [request,response]
     * @return void
     **/
    @RequestMapping(Router.DELETE_FRIEND)
    public ResponseMessage findFriends(@ParameterName("user") User user)  {
        String username = user.getUsername();
        if (username != null && username.length() != 0){
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

}
