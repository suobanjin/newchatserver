package zzuli.zw.request;

import zzuli.zw.main.broadcast.Broadcast;
import zzuli.zw.main.annotation.Request;
import zzuli.zw.main.baseRequest.BaseRequest;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.domain.User;
import zzuli.zw.service.FriendService;
import zzuli.zw.service.FriendServiceImpl;
import zzuli.zw.main.aop.AopUtils;

import java.io.IOException;

/**
 * @ClassName UpdateStatusRequest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/2/12 20:33
 * @Version 1.0
 */
@Request(request = /*MessageType.REQUEST_UPDATE_STATUS*/"")
public class UpdateStatusRequest extends BaseRequest {
    private FriendService friendService = AopUtils.aop(FriendServiceImpl.class, FriendService.class);
    private Broadcast commonRequest = new Broadcast();
    @Override
    public void doRequest(RequestParameter request, ResponseParameter response) throws IOException {
        User user = null;
        String username = user.getUsername();
        int userStatus = user.getStatus();
        friendService.updateStatus(username, userStatus);
        //commonRequest.notifyOther(username,1);
    }
}
