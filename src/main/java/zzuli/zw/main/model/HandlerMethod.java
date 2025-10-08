package zzuli.zw.main.model;

import java.lang.reflect.Method;

public class HandlerMethod {
    private final Object request;
    private final Method method;

    public HandlerMethod(Object controller, Method method) {
        this.request = controller;
        this.method = method;
    }

    public Object getRequest() {
        return request;
    }

    public Method getMethod() {
        return method;
    }
}
