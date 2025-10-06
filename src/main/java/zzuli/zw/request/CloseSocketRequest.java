package zzuli.zw.request;

import zzuli.zw.config.Router;
import zzuli.zw.main.annotation.*;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.pojo.User;
import zzuli.zw.pojo.model.StatusType;
import zzuli.zw.service.interfaces.UserService;

/**
 * @ClassName CloseSocketRequest
 * @Description 客户端关闭请求
 * @Author 索半斤
 * @Date 2021/2/12 16:58
 * @Version 1.0
 */
@Bean("closeSocketRequest")
public class CloseSocketRequest{
    @RequestMapping(Router.CLOSE_SOCKET)
    public void exit(RequestParameter request){
        request.closeConnection();

    }
}
