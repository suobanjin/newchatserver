package zzuli.zw.request;

import zzuli.zw.config.Router;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.main.annotation.ParameterName;
import zzuli.zw.main.annotation.Request;
import zzuli.zw.main.annotation.RequestMapping;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseCode;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.model.ResponseParameter;

/**
 * @ClassName HeartListenerRequest
 * @Description: 心跳检测请求
 * @Author 索半斤
 * @Datetime 2022年 11月 09日 22:50
 * @Version: 1.0
 */
@Bean("heartListenerRequest")
@Request
public class HeartListenerRequest {

    /**
    * @Author 索半斤
    * @Description 处理客户端发送的维护心跳请求
    * @Date 22:25 2022/11/10
    * @Param [request, response]
    * @return zzuli.zw.main.model.protocol.ResponseMessage
    **/
    @RequestMapping(request = Router.HEART_LISTENER)
    public ResponseMessage listen(RequestParameter request,
                                  ResponseParameter response,
                                  @ParameterName("info")int info){
       /* //由于心跳检测请求较为频繁，因此不必每次都经过拦截qi，但是为了防止未登录就发送心跳检测，所以先判断是否登录
        if (request.getSession().getAttribute("user") == null){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setRequest(request.getRequest());
            responseMessage.setCode(ResponseCode.AUTHORITY_ERROR);
            return responseMessage;
        }*/
        //更新心跳包发送的最后时间
        request.updateHeartListener();
        //回应心跳包请求
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setRequest(request.getRequest());
        responseMessage.setCode(ResponseCode.SUCCESS);
        return responseMessage;
    }
}
