package zzuli.zw.request;
import zzuli.zw.config.Router;
import zzuli.zw.main.annotation.*;
import zzuli.zw.main.model.ResponseCode;
import zzuli.zw.domain.User;
import zzuli.zw.main.interfaces.Session;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.service.UserService;
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
@Bean("loginRequest")
public class LoginRequest {
    @Injection(name = "userService")
    private UserService userService;
    @RequestMapping(Router.LOGIN)
    public  void login(RequestParameter request,
                       ResponseParameter response,
                       @ParameterName("user")User user,
                       @ParameterName("age") int age) throws IOException {
        ResponseMessage responseMessage = new ResponseMessage();
        if (user == null || user.getPassword() == null || !RegexUtils.regexUsername(user.getUsername())) {
            responseMessage.setRequest(Router.LOGIN);
            responseMessage.setCode(ResponseCode.FAIL);
            response.write(responseMessage);
            request.closeConnection();
        } else {
            //登录校验
            int isLogin = userService.login(user.getUsername(),user.getPassword());
            if (isLogin >= 3) {
                //登录成功后
                User userInfo = userService.findUserInfoById(user.getId());
                Session session = request.getSession();
                session.setAttribute("user", userInfo);
                //发送登录成功信息
                responseMessage.setSessionId(session.getId());
                responseMessage.setRequest(request.getRequest());
                responseMessage.setCode(ResponseCode.SUCCESS);
                response.write(responseMessage);
                //通过通用响应请求通知其他用户上线信息

                /*User responseUser = new User();
                responseUser.setUsername(user.getUsername());
                Map map = new HashMap();
                map.put("user",responseUser);
                commonResponseMessage.setContentMap(map);*/
                //登录成功,保持长连接状态
                request.keepAlive();
                //登录成功之后开启心跳检测，保证客户端和服务器端的通信状态
                request.startHeartListener(user.getId(),response);
            } else {
                //用户信息校验失败，返回登录失败信息，并将socket关闭
                responseMessage.setRequest(request.getRequest());
                responseMessage.setSendTime(new Date().getTime());
                responseMessage.setCode(ResponseCode.FAIL);
                response.write(responseMessage);
                request.closeConnection();
            }
        }
    }
    /*//登录成功将socket存入容器，id为用户id
    SocketContainer.addSocket(user.getId(), socket);*/
    //将用户通信线程加入容器进行管理
    //ThreadContainer.addThread(user.getId(), request.getRequestThread());
}
