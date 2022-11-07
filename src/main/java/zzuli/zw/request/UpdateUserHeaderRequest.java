package zzuli.zw.request;

import cn.hutool.core.codec.Base64;
import zzuli.zw.main.broadcast.Broadcast;
import zzuli.zw.main.annotation.Request;
import zzuli.zw.main.baseRequest.BaseRequest;
import zzuli.zw.domain.*;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.service.UserService;
import zzuli.zw.service.UserServiceImpl;
import zzuli.zw.main.aop.AopUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @ClassName UpdateUserHeaderRequest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/2/12 20:48
 * @Version 1.0
 */
@Request(request = "")
public class UpdateUserHeaderRequest extends BaseRequest {
    private UserService userService = AopUtils.aop(UserServiceImpl.class, UserService.class);
    private Broadcast commonRequest = new Broadcast();
    @Override
    public void doRequest(RequestParameter request, ResponseParameter response) throws IOException {
        String msg = request.getUrl();
        //User data = MapToObject.MapToUser(request.getRequestData());
        User data = null;
        byte[] decode = Base64.decode(msg);
        String parentPath = FilePath.HEAD_IMAGE_PREFIX;
        String childrenPath = "\\" + data.getUsername();
        File defaultFile = new File(parentPath, childrenPath);
        defaultFile.mkdirs();
        String filePath = defaultFile + "\\" + data.getUsername() + FilePath.PNG_SUFFIX;
        FileOutputStream outputStream = new FileOutputStream(filePath);
        outputStream.write(decode);
        outputStream.flush();
        outputStream.close();
        //int i = userService.updateUserHeader(data.getUsername(), filePath);
        int i = 0;
        if (i >= 1){
            JsonResult<User> jsonResult = new JsonResult<>();
            //jsonResult.setStatus(RequestType.REQUEST_UPDATE_USER_HEADER);
            jsonResult.setMsg(filePath);
            //response.write(jsonResult);
            commonRequest.notifyOtherWhenHeaderChange(data.getUsername(), filePath);
        }else{
            JsonResult<User> jsonResult = new JsonResult<>();
            //jsonResult.setStatus(RequestType.REQUEST_UPDATE_USER_HEADER);
            jsonResult.setMsg("error");
            //response.write(jsonResult);
        }
    }
}
