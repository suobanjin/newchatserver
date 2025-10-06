package zzuli.zw.main.model;

import cn.hutool.core.util.RandomUtil;
import zzuli.zw.config.Router;
import zzuli.zw.main.broadcast.Broadcast;
import zzuli.zw.main.connection.NioConnection;
import zzuli.zw.main.connection.RequestServerThread;
import zzuli.zw.main.factory.HeartBeatContainer;
import zzuli.zw.main.factory.SocketContainer;
import zzuli.zw.main.factory.ThreadContainer;
import zzuli.zw.main.interfaces.Session;
import zzuli.zw.main.factory.SessionContainer;
import zzuli.zw.main.ioc.ServerContext;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.utils.SocketUtils;
import zzuli.zw.pojo.User;
import zzuli.zw.pojo.model.ResponseModel;
import zzuli.zw.pojo.model.StatusType;
import zzuli.zw.request.HeartListenerRequest;

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

    private static final int TIMEOUT = 50000;

    private Thread heartListenerThread;

    private ServerContext serverContext;

    private boolean keepAlive;

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

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public ServerContext getServerContext() {
        return serverContext;
    }

    public void setServerContext(ServerContext serverContext) {
        this.serverContext = serverContext;
    }
    
    // NIO连接支持
    private NioConnection nioConnection;
    
    public NioConnection getNioConnection() {
        return nioConnection;
    }
    
    public void setNioConnection(NioConnection nioConnection) {
        this.nioConnection = nioConnection;
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
        if (this.session == null){
            String sessionId = RandomUtil.randomString(26);
            this.session = new UserSession(sessionId);
            SessionContainer.addSession(sessionId,this.session);
            getResult().setSessionId(sessionId);
            return this.session;
        }
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
            this.broadcast = (Broadcast) this.serverContext.getBeanBySuper(Broadcast.class);
        }
        return this.broadcast;
    }

    public boolean hasKeepAlive(){
        if (getRequestThread() == null || requestSocket == null || this.session == null)return false;
        return true;
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
                    //从管理线程中移除，并关闭通信线程
                    ThreadContainer.removeThread(((User) attribute).getId());
                    getRequestThread().close();
                    //移除Socket
                    SocketContainer.getInstance().remove(((User) attribute).getId());
                    //移除Session
                    SessionContainer.getInstance().remove(session.getId());
                    //停止心跳检测
                    this.stopHeartListener();
                    //向其它用户发送广播
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setRequest(Router.UPDATE_FRIEND_STATUS);
                    User user = new User();
                    user.setAccount(((User)attribute).getAccount());
                    user.setId(((User) attribute).getId());
                    user.setStatus(StatusType.OFFLINE);
                    ResponseModel<User> responseModel = new ResponseModel<>();
                    responseModel.setData(user);
                    responseModel.setInfo(1);
                    responseMessage.setContentObject(responseModel);
                    this.getBroadcast().closeBroadcast(responseMessage,((User) attribute).getId());
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

    public boolean isHeartListenerStart(){
        if (this.heartListenerThread == null)return false;
        return true;
    }
    public boolean isClosed(){
        return this.requestSocket.isClosed();
    }

    public boolean isConnection(int userId){
        if (ThreadContainer.getThread(userId) == null || SocketContainer.getSocket(userId) ==  null)return false;
        for (Map.Entry<String, Session> entry : SessionContainer.getInstance().entrySet()) {
            if (((User)entry.getValue().getAttribute("user")).getId() == userId)return true;
        }
        return false;
    }
    public void startHeartListener(User user,
                                   ResponseParameter response){
        if (this.heartListenerThread != null)throw new RuntimeException("已经开启心跳检测......");
        this.heartListenerThread = new Thread(() -> {
            HeartBeatContainer.addHeartBeat(this.requestSocket,new Date());
            while (HeartBeatContainer.getLastDate(this.requestSocket) != null){
                if (System.currentTimeMillis() - HeartBeatContainer.getLastDate(requestSocket).getTime() > TIMEOUT){
                    System.out.println("heart");
                    ResponseMessage commonResponseMessage = new ResponseMessage();
                    commonResponseMessage.setRequest(Router.UPDATE_FRIEND_STATUS);
                    commonResponseMessage.setCode(ResponseCode.SUCCESS);
                    commonResponseMessage.setFrom(user.getId());
                    commonResponseMessage.setKeepAlive(true);
                    commonResponseMessage.setSendTime(new Date().getTime());
                    ResponseModel<User> responseModel = new ResponseModel<>();
                    User responseUser = new User();
                    responseUser.setId(user.getId());
                    responseUser.setAccount(user.getAccount());
                    responseUser.setStatus(StatusType.OFFLINE);
                    responseModel.setData(responseUser);
                    responseModel.setInfo(1);
                    commonResponseMessage.setContentObject(responseModel);
                    this.broadcast().closeBroadcast(commonResponseMessage,user.getId());
                    this.closeConnection(user.getId());
                    break;
                }
            }
        });
        this.heartListenerThread.start();
    }

    public void stopHeartListener(){
        if (HeartBeatContainer.getInstance().get(this.requestSocket) == null)return;
        HeartBeatContainer.addHeartBeat(this.requestSocket,null);
        HeartBeatContainer.removeHeartBeat(this.requestSocket);
    }

    public void updateHeartListener(){
        HeartBeatContainer.addHeartBeat(this.requestSocket,new Date());
    }
}
