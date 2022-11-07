package zzuli.zw.request;

import zzuli.zw.config.Router;
import zzuli.zw.main.broadcast.Broadcast;
import zzuli.zw.main.model.ResponseMessage;
import zzuli.zw.main.annotation.ParameterName;
import zzuli.zw.main.annotation.Request;
import zzuli.zw.main.annotation.RequestMapping;
import zzuli.zw.domain.*;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.factory.ThreadContainer;
import zzuli.zw.service.FriendService;
import zzuli.zw.service.FriendServiceImpl;
import zzuli.zw.main.aop.AopUtils;

/**
 * @ClassName CloseSocketRequest
 * @Description 客户端关闭请求
 * @Author 索半斤
 * @Date 2021/2/12 16:58
 * @Version 1.0
 */
@Request
public class CloseSocketRequest{
    private FriendService friendService = AopUtils.aop(FriendServiceImpl.class);
    private Broadcast commonRequest = new Broadcast();


    @RequestMapping(Router.CLOSE_SOCKET)
    public void exit(RequestParameter request,@ParameterName("user") User user){
        //friendService.updateStatus(user.getUsername(), StatusType.OFFLINE);
        ThreadContainer.removeThread(user.getId());
        commonRequest.broadcast(new ResponseMessage());
        request.getRequestThread().close();
    }
    /*@Override
    public void doRequest(RequestParameter request, ResponseParameter response) throws IOException {
        Map<Object, Object> requestData = request.getRequestData();
        //User user = MapToObject.MapToUser(requestData);
        User user = null;
        assert user != null;
        friendService.updateStatus(user.getUsername(), StatusType.OFFLINE);
        ThreadContainer.removeThread(user.getId());
        commonRequest.notifyOther(user.getUsername());
        Socket responseSocket = response.getResponseSocket();
        SocketUtils.closeSocket(responseSocket);
    }*/
}
