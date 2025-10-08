package zzuli.zw.main.model;

import zzuli.zw.main.interfaces.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

public class InterceptorChain {

    private final List<OrderedInterceptor> interceptors = new ArrayList<>();
    private int currentIndex = -1;

    public InterceptorChain(List<OrderedInterceptor> interceptors) {
        this.interceptors.addAll(interceptors);
    }

    public boolean applyPreHandle(RequestParameter request, ResponseParameter response, Object handler) throws Exception {
        for (int i = 0; i < interceptors.size(); i++) {
            HandlerInterceptor interceptor = interceptors.get(i).getInterceptor();
            if (!interceptor.preHandle(request, response, handler)) {
                triggerAfterCompletion(request, response, handler, null);
                return false;
            }
            currentIndex = i;
        }
        return true;
    }

    public void applyPostHandle(RequestParameter request, ResponseParameter response, Object handler, Object result) throws Exception {
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).getInterceptor().postHandle(request, response, handler);
        }
    }

    public void triggerAfterCompletion(RequestParameter request, ResponseParameter response, Object handler, Exception ex) throws Exception {
        for (int i = currentIndex; i >= 0; i--) {
            interceptors.get(i).getInterceptor().afterCompletion(request, response, handler, ex);
        }
    }
}

