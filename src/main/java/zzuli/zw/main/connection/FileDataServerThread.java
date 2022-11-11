package zzuli.zw.main.connection;

import cn.hutool.core.thread.ThreadUtil;
import zzuli.zw.config.Router;
import zzuli.zw.domain.User;
import zzuli.zw.main.factory.ObjectMapperFactory;
import zzuli.zw.main.model.ResponseCode;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.service.*;
import zzuli.zw.main.aop.AopUtils;
import zzuli.zw.main.utils.ProtocolUtils;
import zzuli.zw.utils.RegexUtils;
import zzuli.zw.main.utils.SocketUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @ClassName FileDataServerThread
 * @Description 请求文件、图片等操作的线程
 * @Author 索半斤
 * @Date 2021/1/28 11:35
 * @Version 1.0
 */
public class FileDataServerThread implements Runnable {
    private Socket socket;
    private ServerSocket serverSocket;
    public FileDataServerThread(Socket socket, ServerSocket serverSocket) {
        this.socket = socket;
        this.serverSocket = serverSocket;
    }
    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        try {
            //打印客户端信息
            System.out.println(socket.getInetAddress().getHostAddress() + "在文件服务器端进行了.........connected");
            ResponseMessage result = ProtocolUtils.receive(socket);
            int status = result.getRequest();
            //登录前获取用户头像(客户端如果进行了缓存则无需进行获取)
            if (status == Router.BEFORE_LOGIN) {
                String content = result.getContent();
                User data = ObjectMapperFactory.getInstance().readValue(content,User.class);
                ResponseMessage responseMessage = new ResponseMessage();
                if (data == null || data.getUsername() == null || !RegexUtils.regexUsername(data.getUsername())) {
                    responseMessage.setRequest(status);
                    responseMessage.setCode(ResponseCode.FAIL);
                } else {
                    LoginService loginService = AopUtils.aop(LoginServiceImpl.class,LoginService.class);
                    String username = data.getUsername();
                    String userHeaderEncode = loginService.findHeadPictureByUsername(username);
                    responseMessage.setRequest(status);
                    responseMessage.setCode(ResponseCode.SUCCESS);
                    responseMessage.setContentMap(userHeaderEncode);
                }
                ProtocolUtils.send(responseMessage, socket);
                SocketUtils.closeSocket(socket);
              //根据图片地址获取图片
            }else if (status == Router.FIND_COMMON_PIC){
                String content = result.getContent();
                Runnable runnable = new HeaderImageRequestThread(socket, content);
                ThreadUtil.execute(runnable);
            } /*else if (status == MessageType.REQUEST_FRIEND_IMAGE) {
                //好友头像的地址
                String content = result.getContent();
                Runnable runnable = new HeaderImageRequestThread(socket, content);
                ThreadUtil.execute(runnable);
            }else if (status == MessageType.REQUEST_FIND_INFO_IMAGE){
                String message = result.getContent();
                Runnable runnable = new HeaderImageRequestThread(socket, message);
                ThreadUtil.execute(runnable);
            }*/ else {
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setRequest(status);
                ProtocolUtils.send(responseMessage, socket);
                SocketUtils.closeSocket(socket);
            }
        } catch (Exception e) {
            try {
                serverSocket.close();
            }catch (IOException e1){
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
