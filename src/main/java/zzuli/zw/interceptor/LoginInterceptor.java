package zzuli.zw.interceptor;

import zzuli.zw.main.model.ResponseCode;
import zzuli.zw.main.annotation.Interceptor;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.main.interfaces.HandlerInterceptor;
import zzuli.zw.main.interfaces.Session;

/**
 * @Author 索半斤
 * @Description 登录拦截器
 * @Date 2022/1/28 13:30
 */
@Interceptor(order = 1)
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(RequestParameter request, ResponseParameter response, Object handler) throws Exception {
        //System.out.println("登录拦截中.....");
        Session session = request.getSession();
        if (session.getAttribute("user") != null){
            return true;
        }else{
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setRequest(request.getRequest());
            responseMessage.setCode(ResponseCode.AUTHORITY_ERROR);
            response.write(responseMessage);
            request.closeSocket();
            return false;
        }
    }

    @Override
    public void postHandle(RequestParameter request, ResponseParameter response, Object handler) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(RequestParameter request, ResponseParameter response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        System.out.println(request.getRequest() +","+request.getSession()+", "+ex);
    }
}
