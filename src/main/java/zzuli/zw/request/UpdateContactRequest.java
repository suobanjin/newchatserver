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
import java.util.Map;

/**
 * @ClassName UpdateContactRequest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/2/12 16:13
 * @Version 1.0
 */
@Request(request = /*MessageType.REQUEST_UPDATE_CONTACT*/"")
public class UpdateContactRequest extends BaseRequest {
    private FriendService friendService = AopUtils.aop(FriendServiceImpl.class, FriendService.class);
    @Override
    public void doRequest(RequestParameter request, ResponseParameter response) throws IOException {
        Map<Object, Object> requestData = request.getRequestData();
        User user = null;
        assert user != null;
        User info = friendService.findFriendInfoByUsername(user.getUsername());
        JsonResult<User> jsonResult = new JsonResult<>();
        //jsonResult.setStatus(RequestType.REQUEST_SUCCESS);
        jsonResult.setData(info);
        info.setHeaderPicture(null);
        //response.write(jsonResult);
    }
}
