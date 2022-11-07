package zzuli.zw.main.factory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName RequestBeanContainer
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/2/12 15:51
 * @Version 1.0
 */
public class RequestBeanContainer<K,V> extends ConcurrentHashMap<String, Object> {
    enum Singleton{
        RequestBeanContainer;
        private  RequestBeanContainer<String, Object> requestBeanContainer;
        Singleton(){ requestBeanContainer = new RequestBeanContainer<>();}
        public RequestBeanContainer<String, Object> getInstance(){return requestBeanContainer;}
    }
    public static RequestBeanContainer<String, Object> getInstance(){
        return Singleton.RequestBeanContainer.getInstance();
    }

    public synchronized static void addRequest(String requestStatus, Object baseRequest){
        getInstance().put(requestStatus, baseRequest);
    }

    public static Object getRequest(String requestStatus){
        return getInstance().get(requestStatus);
    }
}
