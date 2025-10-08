package zzuli.zw.request;

import zzuli.zw.broadcast.UserBroadcast;
import zzuli.zw.config.Router;
import zzuli.zw.main.annotation.*;
import zzuli.zw.main.manager.IMSessionManager;
import zzuli.zw.main.model.ResponseCode;
import zzuli.zw.main.interfaces.Session;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.pojo.PhotoWall;
import zzuli.zw.pojo.User;
import zzuli.zw.pojo.model.ResponseModel;
import zzuli.zw.pojo.model.StatusType;
import zzuli.zw.service.interfaces.IndexService;
import zzuli.zw.service.interfaces.PhotoWallService;
import zzuli.zw.service.interfaces.UserService;
import zzuli.zw.utils.RegexUtils;

import java.io.IOException;
import java.util.Date;

/**
 * @author 索半斤
 * @description 用来处理登录请求
 * @date 2022/2/1
 * @className LoginRequest
 */
@Request
//@Bean("loginRequest")
public class LoginRequest {
    @Injection(name = "userService")
    private UserService userService;
    @Injection
    @Qualifier("indexService")
    private IndexService indexService;
    @Injection
    private UserBroadcast broadcast;
    @Injection(name = "photoWallService")
    private PhotoWallService photoWallService;

    @RequestMapping(request = Router.FIND_USER_STATUS)
    public void checkStatus(@ParameterName("user") User user,
                            RequestParameter request,
                            ResponseParameter response) throws IOException {
        //这里先记一个bug，就是当请求找不到的时候，没有默认返回值,明天再改
        int status = userService.findUserStatus(user.getAccount());
        if (!request.isUserOnline(user)) {
            status = StatusType.OFFLINE;
            userService.updateUserStatus(user.getId(), status);
        }
        ResponseMessage responseMessage = new ResponseMessage();
        User responseUser = new User();
        responseUser.setStatus(status);
        responseUser.setAccount(user.getAccount());
        responseMessage.setContentObject(responseUser);
        try {
            response.write(responseMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(Router.LOGIN)
    public void login(RequestParameter request,
                      ResponseParameter response,
                      @ParameterName("user") User user) throws IOException {
        ResponseMessage responseMessage = new ResponseMessage();
        if (user == null || user.getPassword() == null || !RegexUtils.regexUsername(user.getAccount())) {
            responseMessage.setRequest(Router.LOGIN);
            responseMessage.setCode(ResponseCode.FAIL);
            response.write(responseMessage);
            request.closeConnection();
        } else {
            int status = userService.findUserStatus(user.getAccount());
            if (status != StatusType.OFFLINE) {
                responseMessage.setCode(ResponseCode.FAIL);
                responseMessage.setContent("该用户已经登录过了!");
                response.write(responseMessage);
                return;
            }
            //登录校验
            User isLogin = userService.login(user);
            if (isLogin != null) {
                //修改用户的登录状态
                userService.updateUserStatus(isLogin.getId(), StatusType.ONLINE);
                //将信息存入session
                Session session = request.getSession();
                session.setAttribute("user", isLogin);
                //发送登录成功信息
                isLogin = indexService.findIndexInfo(isLogin.getId());
                responseMessage.setSessionId(session.getId());
                responseMessage.setRequest(request.getRequest());
                responseMessage.setCode(ResponseCode.SUCCESS);
                responseMessage.setContentObject(isLogin);
                response.write(responseMessage);

                //向好友发送上线提醒消息
                ResponseMessage broadcastResponse = new ResponseMessage();
                broadcastResponse.setRequest(Router.UPDATE_FRIEND_STATUS);
                User responseUser = new User();
                responseUser.setAccount(isLogin.getAccount());
                responseUser.setId(isLogin.getId());
                responseUser.setStatus(StatusType.ONLINE);
                ResponseModel<User> responseModel = new ResponseModel<>();
                responseModel.setData(responseUser);
                responseModel.setInfo(0);
                broadcastResponse.setContentObject(responseModel);
                broadcastResponse.setCode(ResponseCode.SUCCESS);
                broadcast.broadcast(broadcastResponse, isLogin.getId());
                System.out.println(broadcast);
                //登录成功之后开启心跳检测，保证客户端和服务器端的通信状态
                request.startHeartListener(isLogin, response);
            } else {
                //用户信息校验失败，返回登录失败信息，并将socket关闭
                responseMessage.setRequest(request.getRequest());
                responseMessage.setSendTime(new Date().getTime());
                responseMessage.setContent("用户名或者密码错误,请稍后重试...");
                responseMessage.setCode(ResponseCode.FAIL);
                response.write(responseMessage);
                request.closeSocket();
            }
        }
    }
}
