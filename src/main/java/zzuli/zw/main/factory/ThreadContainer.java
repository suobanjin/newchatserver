package zzuli.zw.main.factory;

import java.util.HashMap;

/**
 * @ClassName ThreadContainer
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/7 19:47
 * @Version 1.0
 */
public class ThreadContainer<K,V> extends HashMap<Object, Runnable> {
    enum Singleton{
        ThreadContainer;
        private ThreadContainer<Object, Runnable> threadThreadContainer;
        Singleton(){threadThreadContainer = new ThreadContainer<>();}
        public ThreadContainer<Object, Runnable> getInstance(){return threadThreadContainer;}
    }

    public static ThreadContainer<Object, Runnable> getInstance(){
        return Singleton.ThreadContainer.getInstance();
    }

    public static void addThread(Object threadName, Runnable thread){
        getInstance().put(threadName, thread);
    }

    public static Runnable getThread(Object threadName){
        return getInstance().get(threadName);
    }

    public static void removeThread(Object threadName){
        getInstance().remove(threadName);
    }
}
