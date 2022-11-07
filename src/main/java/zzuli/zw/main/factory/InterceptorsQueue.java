package zzuli.zw.main.factory;

import zzuli.zw.main.interfaces.HandlerInterceptor;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class InterceptorsQueue<K> extends ArrayBlockingQueue<HandlerInterceptor> {
    public InterceptorsQueue(int capacity) {
        super(capacity);
    }
    enum Singleton{
        InterceptorsQueue;
        private  InterceptorsQueue<HandlerInterceptor> interceptorsQueue;
        Singleton(){ interceptorsQueue = new InterceptorsQueue<>(16);}
        public InterceptorsQueue<HandlerInterceptor> getInstance(){return interceptorsQueue;}
    }
    public static InterceptorsQueue<HandlerInterceptor> getInstance(){
        return Singleton.InterceptorsQueue.getInstance();
    }
    public  synchronized boolean addBean(HandlerInterceptor o) {
        return getInstance().add(o);
    }
    public static HandlerInterceptor peekBean(){
        return getInstance().peek();
    }

    public  synchronized  boolean addBeans(Queue<HandlerInterceptor> queue){
        return getInstance().addAll(queue);
    }
}
