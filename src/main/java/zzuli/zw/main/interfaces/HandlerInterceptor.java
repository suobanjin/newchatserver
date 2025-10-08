package zzuli.zw.main.interfaces;

import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;

public interface HandlerInterceptor {
    boolean preHandle(RequestParameter request, ResponseParameter response, Object handler) throws Exception;
    default void postHandle(RequestParameter request, ResponseParameter response, Object handler) throws Exception {
    }
    default void afterCompletion(RequestParameter request, ResponseParameter response, Object handler, Exception ex) throws Exception {
    }
}
