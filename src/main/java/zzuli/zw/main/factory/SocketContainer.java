package zzuli.zw.main.factory;

import zzuli.zw.main.connection.NioConnection;
import zzuli.zw.main.connection.NioRequestAdapter;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName SocketContainer
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/7 16:32
 * @Version 1.0
 */
public class SocketContainer<K,V> extends ConcurrentHashMap<Object, NioConnection> {
    enum Singleton{
        SocketContainer;
        private  SocketContainer<Object, NioConnection> socketContainer;
        Singleton(){socketContainer = new SocketContainer<>();}
        public SocketContainer<Object, NioConnection> getInstance(){return socketContainer;}
    }

    public static SocketContainer<Object, NioConnection> getInstance(){
        return Singleton.SocketContainer.getInstance();
    }

    public static synchronized void addSocket(Object socketName, NioConnection socket){
        getInstance().put(socketName, socket);
    }

    public static NioConnection getSocket(Object socketName){
        return getInstance().get(socketName);
    }
}
