package zzuli.zw.request;

import zzuli.zw.main.broadcast.Broadcast;
import zzuli.zw.main.annotation.Request;
import zzuli.zw.main.baseRequest.BaseRequest;
import zzuli.zw.domain.*;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.service.FriendService;
import zzuli.zw.service.FriendServiceImpl;
import zzuli.zw.main.aop.AopUtils;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName UpdateUserRemarkRequest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/2/12 20:53
 * @Version 1.0
 */
@Request(request = /*MessageType.REQUEST_UPDATE_USER_REMARK*/"")
public class UpdateUserRemarkRequest extends BaseRequest {
    private FriendService friendService = AopUtils.aop(FriendServiceImpl.class, FriendService.class);
    private Broadcast commonRequest = new Broadcast();
    @Override
    public void doRequest(RequestParameter request, ResponseParameter response) throws IOException {
        User resultData = null;
        List<Friend> friends = resultData.getFriends();
        Friend friend = friends.get(0);
        friendService.updateFriendRemark(friend.getId(), friend.getRemark());
        //commonRequest.notifyOther(resultData.getUsername(),1);
    }
}
