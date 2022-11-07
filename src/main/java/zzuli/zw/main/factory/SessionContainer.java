package zzuli.zw.main.factory;

import zzuli.zw.main.interfaces.Session;
import java.util.concurrent.ConcurrentHashMap;

public class SessionContainer<K,V> extends ConcurrentHashMap<String, Session> {
    enum Singleton{
        SessionContainer;
        private SessionContainer<String, Session> sessionSessionContainer;
        Singleton(){sessionSessionContainer = new SessionContainer<>();}
        public SessionContainer<String, Session> getInstance(){return sessionSessionContainer;}
    }
    public static SessionContainer<String, Session> getInstance(){
        return Singleton.SessionContainer.getInstance();
    }
    public static synchronized void addSession(String id, Session session){
        getInstance().put(id,session);
    }
    public static Session getSession(String sessionId){
        return getInstance().get(sessionId);
    }
    public static synchronized void remove(String sessionId){
        if (sessionId == null || sessionId.length() == 0)return;
        if (!getInstance().contains(sessionId))return;
        getInstance().remove(sessionId);
    }
}
