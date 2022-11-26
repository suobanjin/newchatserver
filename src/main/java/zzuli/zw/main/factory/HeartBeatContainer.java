package zzuli.zw.main.factory;

import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName HeartBeatContainer
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 09日 18:10
 * @Version: 1.0
 */
public class HeartBeatContainer<K,V> extends ConcurrentHashMap<Socket, Date> {
    enum Singleton{
        HeartBeatContainer;
        private  HeartBeatContainer<Socket,Date> heartBeatContainer;
        Singleton(){heartBeatContainer = new HeartBeatContainer<>();}
        public HeartBeatContainer<Socket,Date> getInstance(){return heartBeatContainer;}
    }

    public static HeartBeatContainer<Socket,Date> getInstance(){
        return Singleton.HeartBeatContainer.getInstance();
    }

    public static synchronized void addHeartBeat(Socket requestSocket, Date sendDate){
        getInstance().put(requestSocket,sendDate);
    }

    public static  void removeHeartBeat(Socket requestSocket){
        getInstance().remove(requestSocket);
    }

    public static Date getLastDate(Socket requestSocket){
        return getInstance().get(requestSocket);
    }
}
