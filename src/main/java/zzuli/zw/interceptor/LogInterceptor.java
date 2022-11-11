package zzuli.zw.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.main.annotation.Interceptor;
import zzuli.zw.main.interfaces.HandlerInterceptor;

/**
 * @author 索半斤
 * @description 日志拦截器
 * @date 2022/2/4
 * @className LogInterceptor
 */
@Interceptor(order = 0)
public class LogInterceptor implements HandlerInterceptor {
    private Logger logger = LoggerFactory.getLogger(LogInterceptor.class);
    @Override
    public boolean preHandle(RequestParameter request,
                             ResponseParameter response,
                             Object handler) throws Exception {
        System.out.println("日志拦截中......");
        logger.info("请求ip--->" + request.getIp() + "  " +
                "请求端口--->" + request.getPort()  + "  " +
                "请求--->" + request.getRequest());
        return true;
    }

    @Override
    public void postHandle(RequestParameter request, ResponseParameter response, Object handler) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(RequestParameter request, ResponseParameter response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
