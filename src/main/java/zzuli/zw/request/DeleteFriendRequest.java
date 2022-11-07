package zzuli.zw.request;

import zzuli.zw.main.annotation.Request;
import zzuli.zw.main.baseRequest.BaseRequest;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.service.FriendService;
import zzuli.zw.service.FriendServiceImpl;
import zzuli.zw.main.aop.AopUtils;

import java.io.IOException;

/**
 * @ClassName DeleteFriendRequest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/2/12 20:55
 * @Version 1.0
 */
@Request(request = /*MessageType.REQUEST_DELETE_FRIEND*/"")
public class DeleteFriendRequest extends BaseRequest {
    private  FriendService friendService = AopUtils.aop(FriendServiceImpl.class, FriendService.class);
    @Override
    public void doRequest(RequestParameter request, ResponseParameter response) throws IOException {
        /*User data = MapToObject.MapToUser(request.getRequestData());
        Friend friend = data.getFriends().get(0);
        Relation myRelation = friendService.findRelation(data.getUsername(), friend.getUsername());
        Relation friendRelation = friendService.findRelation(friend.getUsername(), data.getUsername());
        int i = friendService.deleteFriendInfo(data.getUsername(), friend.getUsername());
        int j = friendService.deleteFriendInfo(friend.getUsername(), data.getUsername());
        if (i >= 1 && j >= 1){
            JsonResult<Relation> jsonResult = new JsonResult<>();
            jsonResult.setData(myRelation);
            jsonResult.setStatus(MessageType.REQUEST_DELETE_FRIEND);
            jsonResult.setMsg(friend.getUsername());
            response.write(jsonResult);
            Socket friendSocket = SocketContainer.getSocket(friend.getUsername());
            if (friendSocket != null) {
                jsonResult.setStatus(MessageType.RESPONSE_DELETE_FRIEND);
                jsonResult.setData(friendRelation);
                jsonResult.setMsg(data.getUsername());
                ProtocolUtils.send(jsonResult, friendSocket);
            }
        }else{
            JsonResult<User> jsonResult = new JsonResult<>();
            jsonResult.setStatus(MessageType.REQUEST_DELETE_FRIEND);
            jsonResult.setMsg("error");
            response.write(jsonResult);
        }*/
    }
}
