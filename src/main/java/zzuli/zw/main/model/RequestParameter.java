package zzuli.zw.main.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import zzuli.zw.main.broadcast.Broadcast;
import zzuli.zw.main.connection.NioConnection;
import zzuli.zw.main.interfaces.Session;
import zzuli.zw.main.ioc.ServerContext;
import zzuli.zw.main.manager.IMSessionManager;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.utils.SecureTokenUtil;
import zzuli.zw.pojo.User;
import zzuli.zw.pojo.model.ResponseModel;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RequestParameter
 * @Description 专为NIO模式优化的请求参数类
 * @Author 索半斤
 * @Date 2021/2/12 12:24
 * @Version 2.0
 */
@Slf4j
public class RequestParameter implements Serializable {
    private ResponseMessage result;
    private String url;
    private int request;
    private Map<Object,Object> requestData;
    private List<Map<Object,Object>> listRequestData;
    private String ip;
    private String host;
    private int port;
    private Broadcast broadcast;
    private int from;
    private int to;
    private int requestType;
    private String protocolVersion;
    private ServerContext serverContext;
    private boolean keepAlive = true;
    private long requestTime;
    private Session session;

    // NIO连接支持
    private NioConnection nioConnection;
    private String sessionId;
    private static final ObjectMapper mapper = new ObjectMapper();
    /**
     * 构造函数，初始化请求时间
     */
    public RequestParameter() {
        this.requestTime = System.currentTimeMillis();
    }

    /**
     * 静态工厂方法，从NIO连接和请求消息创建参数
     */
    public static RequestParameter fromNioConnection(NioConnection connection, ResponseMessage message) {
        RequestParameter parameter = new RequestParameter();
        // 设置基本信息
        parameter.setResult(message);
        // 设置请求信息
        parameter.setRequest(message.getRequest());
        // 设置请求类型
        parameter.setRequestType(message.getRequestType());
        // 设置请求路径
        parameter.setUrl(message.getUrl());
        // 协议版本
        parameter.setProtocolVersion(message.getVersion());
        // 是否保持长连接
        parameter.setKeepAlive(message.isKeepAlive());
        // From用户ID
        parameter.setFrom(message.getFrom());
        // To用户ID
        parameter.setTo(message.getTo());
        // 设置SessionId
        parameter.setSessionId(message.getSessionId());

        // 设置Session
        String sessionId = message.getSessionId();
        if (sessionId != null && !sessionId.trim().isEmpty()) {
            IMUserSession session = IMSessionManager.getUserSession(sessionId);
            if (session != null) {
                parameter.setSession(session);
                parameter.setSessionId(sessionId);
            }
        }
        // 设置网络信息
        if (connection != null) {
            parameter.setNioConnection(connection);
            InetSocketAddress remoteAddress = connection.getRemoteAddress();
            if (remoteAddress != null) {
                parameter.setIp(remoteAddress.getAddress().getHostAddress());
                parameter.setHost(remoteAddress.getHostName());
                parameter.setPort(remoteAddress.getPort());
            }
        }
        // 设置请求参数
        setRequestData(parameter);
        return parameter;
    }

    /**
     * 设置请求参数
     * @param requestParameter
     */
    private static void setRequestData(RequestParameter requestParameter){
        String content = requestParameter.getResult().getContent();
        if (StringUtils.isNotEmpty(content)){
            try {
                Map<Object, Object> map = mapper.readValue(content, Map.class);
                requestParameter.setRequestData(map);
            }catch (Exception e){
                // TODO 添加异常处理
                log.error("解析请求参数失败：{}",e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "RequestParameter{" +
                "result=" + result +
                ", url='" + url + "'" +
                ", request=" + request +
                ", requestData=" + requestData +
                ", ip='" + ip + "'" +
                ", host='" + host + "'" +
                ", port=" + port +
                ", sessionId='" + sessionId + "'" +
                ", from=" + from +
                ", to=" + to +
                ", requestType=" + requestType +
                ", protocolVersion='" + protocolVersion + "'" +
                ", keepAlive=" + keepAlive +
                '}';
    }


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    // 工具方法
    public Object getParameter(String parameterName) {
        if (parameterName == null || parameterName.length() == 0 || requestData == null) {
            return null;
        }
        return requestData.get(parameterName);
    }

    public void setSession(Session session){
        this.session = session;
    }

    /**
     * 获取Session，如果session不存在则创建一个返回
     * @return
     */
    public Session getSession() {
        if (this.session == null){
            String sessionId = SecureTokenUtil.generateSessionId();
            IMSessionManager.createUserSession(sessionId, null, nioConnection.getDeviceId(),nioConnection);
            getResult().setSessionId(sessionId);
            return this.session;
        }
        return this.session;
    }

    public void close(){
        this.nioConnection.close();
    }

    /**
     * 判断用户是否保持链接状态
     * @return
     */
    public boolean hasKeepAlive(User user){
        return IMSessionManager.isUserOnline(user.getId());
    }

    public String getResultContent() {
        if (result == null) {
            return null;
        }
        return result.getContent();
    }

    public Broadcast broadcast() {
        if (broadcast == null) {
            broadcast = (Broadcast) serverContext.getBeanBySuper(Broadcast.class);
        }
        return broadcast;
    }

    public boolean isValid() {
        return nioConnection != null && !nioConnection.isClosed() && result != null;
    }

    public long getRequestDuration() {
        return System.currentTimeMillis() - requestTime;
    }

    public ResponseMessage createResponseMessage(int code, Object content) {
        ResponseMessage response = new ResponseMessage();
        response.setRequest(request);
        response.setCode(code);
        response.setSessionId(sessionId);
        response.setFrom(from);
        response.setTo(to);
        response.setKeepAlive(keepAlive);
        response.setSendTime(new Date().getTime());
        response.setContentObject(content);
        return response;
    }

    public void sendResponse(ResponseMessage response) {
        if (nioConnection != null && !nioConnection.isClosed()) {
            try {
                // 使用ResponseParameter来发送响应
                ResponseParameter responseParameter = ResponseParameter.fromNioConnection(nioConnection);
                responseParameter.write(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendSuccessResponse(Object content) {
        ResponseMessage response = createResponseMessage(ResponseCode.SUCCESS, content);
        sendResponse(response);
    }

    public void sendErrorResponse(int code, String errorMsg) {
        ResponseModel<String> responseModel = new ResponseModel<>();
        responseModel.setData(errorMsg);
        responseModel.setInfo(code);
        ResponseMessage response = createResponseMessage(code, responseModel);
        sendResponse(response);
    }

    public boolean isUserOnline(User user){
        return IMSessionManager.isUserOnline(user.getId());
    }
}
