package zzuli.zw.request;

import zzuli.zw.main.broadcast.Broadcast;
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
 * @ClassName UpdateUserRequest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/2/12 20:47
 * @Version 1.0
 */
@Request(request = /*MessageType.REQUEST_UPDATE_USER_INFO*/"")
public class UpdateUserRequest extends BaseRequest {
    private UserService userService = AopUtils.aop(UserServiceImpl.class, UserService.class);
    private Broadcast commonRequest = new Broadcast();
    @Override
    public void doRequest(RequestParameter request, ResponseParameter response) throws IOException {
        User user = null;
        /*int i = userService.updateUserInfo(user);
        User detailUserInfo = userService.findDetailUserInfo(user.getUsername());*/
        JsonResult<User> jsonResult = new JsonResult<>();
        //jsonResult.setStatus(RequestType.REQUEST_UPDATE_USER_INFO);
        //jsonResult.setData(detailUserInfo);
        int i = 0;
        if (i == -1){
            jsonResult.setMsg("更新失败，请稍后重试");
        }else{
            jsonResult.setMsg("更新成功!");
            //commonRequest.notifyOther(user.getUsername(),1);
        }
        //response.write(jsonResult);
    }
}
