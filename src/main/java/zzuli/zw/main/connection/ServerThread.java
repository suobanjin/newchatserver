package zzuli.zw.main.connection;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import zzuli.zw.config.Router;
import zzuli.zw.domain.User;
import zzuli.zw.main.broadcast.Broadcast;
import zzuli.zw.main.factory.ObjectMapperFactory;
import zzuli.zw.main.factory.SessionContainer;
import zzuli.zw.main.factory.SocketContainer;
import zzuli.zw.main.factory.ThreadContainer;
import zzuli.zw.main.model.ResponseMessage;
import zzuli.zw.main.model.UserSession;
import zzuli.zw.service.*;
import zzuli.zw.main.aop.AopUtils;
import zzuli.zw.main.utils.ProtocolUtils;
import zzuli.zw.utils.RegexUtils;
import zzuli.zw.main.utils.SocketUtils;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * @ClassName ServerThread
 * @Description 服务端线程
 * @Author 索半斤
 * @Date 2021/1/28 11:35
 * @Version 1.0
 */
public class ServerThread implements Runnable {
    private Socket socket;
    private ServerSocket serverSocket;
    public ServerThread(Socket socket, ServerSocket serverSocket) {
        this.socket = socket;
        this.serverSocket = serverSocket;
    }
    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        try {
            ResponseMessage result = ProtocolUtils.receive(socket);
            int status = result.getRequest();
            if (status == Router.LOGIN) {
                User user = ObjectMapperFactory.getInstance().readValue(result.getContent(),User.class);
                ResponseMessage responseMessage = new ResponseMessage();
                if (user == null || user.getPassword() == null || !RegexUtils.regexUsername(user.getUsername())) {
                    //responseMessage.setRequest(RequestType.LOGIN_FAIL);
                    ProtocolUtils.send(responseMessage, socket);
                    SocketUtils.closeSocket(socket);
                } else {
                    //登录校验
                    LoginService loginService = AopUtils.aop(UserServiceImpl.class);
                    boolean isLogin = loginService.login(user);
                    if (isLogin) {
                        //登录成功将socket存入容器，id为用户id
                        SocketContainer.addSocket(user.getId(), socket);
                        //登录成功后创建session并存入Session容器中，id为随机生成
                        UserService userService = AopUtils.aop(UserServiceImpl.class);
                        User userInfo = userService.findUserInfoById(user.getId());
                        String sessionId = RandomUtil.randomString(26);
                        UserSession userSession = new UserSession(sessionId);
                        userSession.setAttribute("user",userInfo);
                        SessionContainer.addSession(sessionId,userSession);
                        //发送登录成功信息
                        responseMessage.setSessionId(sessionId);
                        //responseMessage.setRequest(RequestType.LOGIN_SUCCESS);
                        ProtocolUtils.send(responseMessage, socket);
                        //通过线程发送响应信息
                        RequestServerThread serverThread = new RequestServerThread(socket);
                        //通过通用响应请求通知其他用户上线信息
                        Broadcast commonRequest = new Broadcast();
                        //commonRequest.notifyOther(user.getUsername(),1);
                        //为用户开启独立线程
                        ThreadUtil.execute(serverThread);
                        //将用户通信线程加入容器进行管理
                        ThreadContainer.addThread(user.getId(), serverThread);
                    } else {
                        //用户信息校验失败，返回登录失败信息，并将socket关闭
                        //responseMessage.setRequest(RequestType.LOGIN_FAIL);
                        ProtocolUtils.send(responseMessage, socket);
                        SocketUtils.closeSocket(socket);
                    }
                }
            }else {
                ResponseMessage responseMessage = new ResponseMessage();
                //responseMessage.setRequest(RequestType.SERVER_ERROR);
                ProtocolUtils.send(responseMessage, socket);
                SocketUtils.closeSocket(socket);
            } /*else if (status == MessageType.REQUEST_UPDATE_CONTACT) {
                //User user = result.getData();
                User user = null;
                FriendService friendService = AopUtils.aop(FriendServiceImpl.class, FriendService.class);
                User info = friendService.findFriendInfoByUsername(user.getUsername());
                JsonResult<User> jsonResult = new JsonResult<>();
                jsonResult.setStatus(MessageType.REQUEST_SUCCESS);
                jsonResult.setData(info);
                info.setHeaderPicture(null);
                //ProtocolUtils.send(jsonResult, socket);
                SocketUtils.closeSocket(socket);
            } else if (status == MessageType.REQUEST_FIND_STATUS) {
                //User user = result.getData();
                User user = null;
                FriendService friendService = AopUtils.aop(FriendServiceImpl.class, FriendService.class);
                int myStatus = friendService.findStatus(user.getUsername());
                user.setStatus(myStatus);
                JsonResult<User> jsonResult = new JsonResult<>();
                jsonResult.setStatus(MessageType.REQUEST_FIND_STATUS);
                jsonResult.setData(user);
                //ProtocolUtils.send(jsonResult, socket);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            try {
                serverSocket.close();
                SocketContainer<Object, Socket> instance = SocketContainer.getInstance();
                instance.forEach((key,value) -> SocketUtils.closeSocket(value));
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }
}
