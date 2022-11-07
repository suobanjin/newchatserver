package zzuli.zw.main.factory;

import zzuli.zw.main.interfaces.HandlerMethodArgumentResolver;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 索半斤
 * @description 存放各种参数解析器
 * @date 2022/1/26
 * @className ArgumentResolvers
 */
public class ArgumentResolvers<K> extends ArrayBlockingQueue<HandlerMethodArgumentResolver> {
    private static  Map<Class,HandlerMethodArgumentResolver> argumentResolverMap
            = new ConcurrentHashMap<>();
    public ArgumentResolvers(int capacity) {
        super(capacity);
    }

    public ArgumentResolvers(int capacity, boolean fair) {
        super(capacity, fair);
    }


    public ArgumentResolvers(int capacity, boolean fair, Collection c) {
        super(capacity, fair, c);
    }

    enum Singleton{
        ArgumentResolvers;
        private  ArgumentResolvers<HandlerMethodArgumentResolver> argumentResolvers;
        Singleton(){ argumentResolvers = new ArgumentResolvers<>(16,true);}
        public ArgumentResolvers<HandlerMethodArgumentResolver> getInstance(){return argumentResolvers;}
    }

    public static ArgumentResolvers<HandlerMethodArgumentResolver> getInstance(){
        return Singleton.ArgumentResolvers.getInstance();
    }

    public synchronized static HandlerMethodArgumentResolver getArgumentResolverCache(Class clazz){
        if (argumentResolverMap.size() == 0)return null;
        return argumentResolverMap.get(clazz);
    }

    public synchronized static ArgumentResolvers addResolver(HandlerMethodArgumentResolver resolver) {
        getInstance().add(resolver);
        return getInstance();
    }
    public synchronized static void addArgumentResolverCache(Class clazz, HandlerMethodArgumentResolver resolver){
        argumentResolverMap.put(clazz, resolver);
    }
}
