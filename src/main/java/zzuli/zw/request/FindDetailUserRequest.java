package zzuli.zw.request;

import zzuli.zw.main.annotation.Request;
import zzuli.zw.main.baseRequest.BaseRequest;
import zzuli.zw.domain.*;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.service.UserService;
import zzuli.zw.service.UserServiceImpl;
import zzuli.zw.main.aop.AopUtils;
import java.io.IOException;

/**
 * @ClassName FindDetailUserRequest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/2/12 20:43
 * @Version 1.0
 */
@Request(request = /*MessageType.REQUEST_FIND_DETAIL_USER_INFO*/"")
public class FindDetailUserRequest extends BaseRequest {
    private UserService userService = AopUtils.aop(UserServiceImpl.class, UserService.class);
    @Override
    public void doRequest(RequestParameter request, ResponseParameter response) throws IOException {
        User user = null;
        //User detailUserInfo = userService.findDetailUserInfo(user.getUsername());
        JsonResult<User> jsonResult = new JsonResult<>();
        //jsonResult.setStatus(RequestType.REQUEST_FIND_DETAIL_USER_INFO);
        //jsonResult.setData(detailUserInfo);
       // response.write(jsonResult);
    }
}
