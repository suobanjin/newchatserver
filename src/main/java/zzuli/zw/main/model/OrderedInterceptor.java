package zzuli.zw.main.model;

import zzuli.zw.main.interfaces.HandlerInterceptor;

public class OrderedInterceptor {
    private final HandlerInterceptor interceptor;
    private final int order;
    private final String[] pathPatterns;

    public OrderedInterceptor(HandlerInterceptor interceptor, int order, String[] pathPatterns) {
        this.interceptor = interceptor;
        this.order = order;
        this.pathPatterns = pathPatterns;
    }

    public HandlerInterceptor getInterceptor() { return interceptor; }
    public int getOrder() { return order; }
    public String[] getPathPatterns() { return pathPatterns; }
}

