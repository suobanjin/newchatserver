package zzuli.zw.request;

import zzuli.zw.main.annotation.Request;
import zzuli.zw.main.baseRequest.BaseRequest;
import zzuli.zw.domain.*;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.service.FriendService;
import zzuli.zw.service.FriendServiceImpl;
import zzuli.zw.main.aop.AopUtils;
import java.io.IOException;

/**
 * @ClassName FindStatusRequest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/2/12 20:26
 * @Version 1.0
 */
@Request(request = /*MessageType.REQUEST_FIND_STATUS*/"")
public class FindStatusRequest extends BaseRequest {
    private FriendService friendService = AopUtils.aop(FriendServiceImpl.class, FriendService.class);
    @Override
    public void doRequest(RequestParameter request, ResponseParameter response) throws IOException {
        User user = null;
        assert user != null;
        int myStatus = friendService.findStatus(user.getUsername());
        user.setStatus(myStatus);
        JsonResult<User> jsonResult = new JsonResult<>();
        //jsonResult.setStatus(RequestType.REQUEST_FIND_STATUS);
        jsonResult.setData(user);
        //response.write(jsonResult);
    }
}
