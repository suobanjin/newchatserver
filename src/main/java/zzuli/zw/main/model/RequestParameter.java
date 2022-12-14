package zzuli.zw.main.model;

import cn.hutool.core.util.RandomUtil;
import zzuli.zw.config.Router;
import zzuli.zw.domain.User;
import zzuli.zw.main.broadcast.Broadcast;
import zzuli.zw.main.broadcast.Broadcasts;
import zzuli.zw.main.connection.RequestServerThread;
import zzuli.zw.main.factory.HeartBeatContainer;
import zzuli.zw.main.factory.SocketContainer;
import zzuli.zw.main.factory.ThreadContainer;
import zzuli.zw.main.interfaces.Session;
import zzuli.zw.main.factory.SessionContainer;
import zzuli.zw.main.ioc.ServerContext;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.utils.SocketUtils;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RequestParameter
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/2/12 12:24
 * @Version 1.0
 */
public class RequestParameter implements Serializable {
    private ResponseMessage result;
    private String url;
    private Socket requestSocket;
    private int request;
    private Map<Object,Object> requestData;
    private int status;
    private List<Map<Object,Object>> listRequestData;
    private Session session;
    private String ip;
    private String host;
    private int port;
    private RequestServerThread RequestThread;
    private Broadcast broadcast;

    private int from;

    private int to;

    private int requestType;

    private String protocolVersion;

    private static final int TIMEOUT = 30000;

    private Thread heartListenerThread;

    private ServerContext serverContext;

    @Override
    public String toString() {
        return "RequestParameter{" +
                "result=" + result +
                ", url='" + url + '\'' +
                ", requestSocket=" + requestSocket +
                ", request=" + request +
                ", requestData=" + requestData +
                ", status=" + status +
                ", listRequestData=" + listRequestData +
                ", session=" + session +
                ", ip='" + ip + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", RequestThread=" + RequestThread +
                '}';
    }


    public ServerContext getServerContext() {
        return serverContext;
    }

    public void setServerContext(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    public Broadcast getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(Broadcast broadcast) {
        this.broadcast = broadcast;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public RequestServerThread getRequestThread() {
        return RequestThread;
    }

    public void setRequestThread(RequestServerThread requestThread) {
        RequestThread = requestThread;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ResponseMessage getResult() {
        return result;
    }

    public void setResult(ResponseMessage result) {
        this.result = result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Socket getRequestSocket() {
        return requestSocket;
    }

    public void setRequestSocket(Socket requestSocket) {
        this.requestSocket = requestSocket;
    }

    public int getRequest() {
        return request;
    }

    public void setRequest(int request) {
        this.request = request;
    }

    public Map<Object, Object> getRequestData() {
        return requestData;
    }

    public void setRequestData(Map<Object, Object> requestData) {
        this.requestData = requestData;
    }

    public List<Map<Object, Object>> getListRequestData() {
        return listRequestData;
    }

    public void setListRequestData(List<Map<Object, Object>> listRequestData) {
        this.listRequestData = listRequestData;
    }

    public Session getSession() {
        String sessionId = getResult().getSessionId();
        if (sessionId == null){
            sessionId = RandomUtil.randomString(26);
            this.session = new UserSession(sessionId);
            SessionContainer.addSession(sessionId,this.session);
            getResult().setSessionId(sessionId);
            return this.session;
        }
        this.session = SessionContainer.getSession(sessionId);
        return this.session;
    }

    public Object getParameter(String parameterName){
        if (parameterName == null || parameterName.length() == 0)return null;
        return getRequestData().get(parameterName);
    }

    public String getResultContent(){
        if (getResult() == null)return null;
        return getResult().getContent();
    }

    public Broadcast broadcast() {
        if (this.broadcast == null){
            this.broadcast = (Broadcast) this.serverContext.getBean(Broadcasts.class);
        }
        return this.broadcast;
    }
    public boolean keepAlive(){
        if (getRequestThread() == null || requestSocket == null)return false;
        if (this.session == null) return false;
        List<Object> attributes = session.getAttributes();
        for (Object attribute : attributes) {
            if (attribute instanceof User){
                if (ThreadContainer.getThread(attribute) != null &&
                        SocketContainer.getSocket(attribute) != null)return false;
                ThreadContainer.addThread(((User) attribute).getId(),getRequestThread());
                SocketContainer.addSocket(((User) attribute).getId(),requestSocket);
                return true;
            }
        }
        return false;
    }
    public boolean keepAlive(int userId){
        if (getRequestThread() == null || requestSocket == null)return false;
        if (ThreadContainer.getThread(userId) != null &&
                SocketContainer.getSocket(userId) != null)return false;
        ThreadContainer.addThread(userId,getRequestThread());
        SocketContainer.addSocket(userId,requestSocket);
        return true;
    }
    public boolean closeConnection(){
        if (getRequestThread() == null)return false;
        if (session != null){
            List<Object> attributes = session.getAttributes();
            if (attributes.size() == 0)return false;
            for (Object attribute : attributes) {
                if (attribute instanceof User){
                    ThreadContainer.removeThread(((User) attribute).getId());
                    getRequestThread().close();
                    SocketContainer.getInstance().remove(((User) attribute).getId());
                    SessionContainer.getInstance().remove(session.getId());
                    return true;
                }
            }

        }
        return false;
    }

    public boolean closeConnection(int userId){
        if (getRequestThread() == null)return false;
        if (session != null){
            SessionContainer.getInstance().remove(session.getId());
        }
        ThreadContainer.removeThread(userId);
        getRequestThread().close();
        SocketContainer.getInstance().remove(userId);
        return true;
    }

    public boolean closeSocket(Socket socket){
        if (socket.isClosed())return true;
        SocketUtils.closeSocket(socket);
        getRequestThread().close();
        return true;
    }

    public boolean closeSocket(){
        if (this.requestSocket == null)return false;
        if (requestSocket.isClosed())return true;
        SocketUtils.closeSocket(this.requestSocket);
        getRequestThread().close();
        return true;
    }
    public void startHeartListener(int userId,
                                   ResponseParameter response){
        if (this.heartListenerThread != null)throw new RuntimeException("已经开启心跳检测......");
        this.heartListenerThread = new Thread(() -> {
            while (HeartBeatContainer.getLastDate(this.requestSocket) != null){
                if (System.currentTimeMillis() - HeartBeatContainer.getLastDate(requestSocket).getTime() > TIMEOUT){
                    ResponseMessage commonResponseMessage = new ResponseMessage();
                    commonResponseMessage.setRequest(Router.UPDATE_FRIEND_STATUS);
                    commonResponseMessage.setCode(ResponseCode.SUCCESS);
                    commonResponseMessage.setFrom(userId);
                    commonResponseMessage.setKeepAlive(true);
                    commonResponseMessage.setSendTime(new Date().getTime());
                    commonResponseMessage.setContentLength(0);
                    this.broadcast().broadcast(commonResponseMessage,userId);
                    this.closeConnection(userId);
                }else {
                    try {
                        ResponseMessage responseMessage = new ResponseMessage();
                        responseMessage.setRequest(Router.HEART_LISTENER);
                        responseMessage.setCode(ResponseCode.SUCCESS);
                        responseMessage.setSendTime(new Date().getTime());
                        responseMessage.setSessionId(this.getSession().getId());
                        responseMessage.setContentLength(0);
                        responseMessage.setKeepAlive(true);
                        response.write(responseMessage);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            HeartBeatContainer.removeHeartBeat(this.getRequestSocket());
        });
        this.heartListenerThread.start();
    }

    public void stopHeartListener(){
        HeartBeatContainer.addHeartBeat(this.requestSocket,null);
    }

    public void updateHeartListener(){
        HeartBeatContainer.addHeartBeat(this.requestSocket,new Date());
    }
}
