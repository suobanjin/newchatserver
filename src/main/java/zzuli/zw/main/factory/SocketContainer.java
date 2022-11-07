package zzuli.zw.main.factory;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName SocketContainer
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/7 16:32
 * @Version 1.0
 */
public class SocketContainer<K,V> extends ConcurrentHashMap<Object, Socket> {
    enum Singleton{
        SocketContainer;
        private  SocketContainer<Object, Socket> socketContainer;
        Singleton(){socketContainer = new SocketContainer<>();}
        public SocketContainer<Object, Socket> getInstance(){return socketContainer;}
    }

    public static SocketContainer<Object, Socket> getInstance(){
        return Singleton.SocketContainer.getInstance();
    }

    public static synchronized void addSocket(Object socketName, Socket socket){
        getInstance().put(socketName, socket);
    }

    public static Socket getSocket(Object socketName){
        return getInstance().get(socketName);
    }
}
