package zzuli.zw.main.connection;

import zzuli.zw.main.ioc.ServerContext;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.connection.DispatcherRequest;
import zzuli.zw.main.manager.IMSessionManager;
import zzuli.zw.main.model.IMUserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * NIO请求分发器
 * 将NIO接收到的请求分发给现有的业务处理器
 */
public class NioRequestDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(NioRequestDispatcher.class);
    
    private final ServerContext serverContext;
    
    public NioRequestDispatcher(ServerContext serverContext) {
        this.serverContext = serverContext;
    }
    
    /**
     * 分发请求到业务处理器
     */
    public void dispatchRequest(NioConnection connection, ResponseMessage responseMessage) {
        try {
            // 创建请求参数
            RequestParameter requestParameter = createRequestParameter(connection, responseMessage);
            
            // 创建响应参数
            ResponseParameter responseParameter = createResponseParameter(requestParameter);
            
            // 使用现有的DispatcherRequest处理
            long startTime = System.currentTimeMillis();
            logger.debug("开始处理请求: {} from {}", requestParameter.getUrl(), requestParameter.getIp());
            
            DispatcherRequest dispatcherRequest = new DispatcherRequest();
            dispatcherRequest.doRequest(requestParameter, responseParameter);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("请求处理完成: {} 耗时: {}ms", requestParameter.getUrl(), duration);
            
        } catch (Exception e) {
            logger.error("分发请求时发生错误", e);
            sendErrorResponse(connection, "服务器内部错误: " + e.getMessage());
        }
    }
    
    /**
     * 创建请求参数
     */
    private RequestParameter createRequestParameter(NioConnection connection, ResponseMessage responseMessage) {
        // 使用静态工厂方法创建请求参数
        RequestParameter requestParameter = RequestParameter.fromNioConnection(connection, responseMessage);
        
        // 设置基本信息
        requestParameter.setResult(responseMessage);
        requestParameter.setRequest(responseMessage.getRequest());
        requestParameter.setUrl(responseMessage.getUrl());
        //requestParameter.setStatus(responseMessage.getCode());
        requestParameter.setRequestType(responseMessage.getRequestType());
        requestParameter.setProtocolVersion(responseMessage.getVersion());
        requestParameter.setKeepAlive(responseMessage.isKeepAlive());
        
        // 设置网络信息
        InetSocketAddress remoteAddress = connection.getRemoteAddress();
        if (remoteAddress != null) {
            requestParameter.setIp(remoteAddress.getAddress().getHostAddress());
            requestParameter.setHost(remoteAddress.getHostName());
            requestParameter.setPort(remoteAddress.getPort());
        }
        
        // 设置Session
        String sessionId = responseMessage.getSessionId();
        if (sessionId != null && !sessionId.trim().isEmpty()) {
            IMUserSession session = IMSessionManager.getUserSession(sessionId);
            if (session != null) {
                //requestParameter.setSession(session);
                requestParameter.setSessionId(sessionId);
            }
        }
        
        // 设置服务器上下文
        requestParameter.setServerContext(serverContext);
        
        return requestParameter;
    }
    
    /**
     * 创建响应参数
     */
    private ResponseParameter createResponseParameter(RequestParameter requestParameter) {
        // 使用静态工厂方法从请求参数创建响应参数
        return ResponseParameter.fromRequestParameter(requestParameter);
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(NioConnection connection, String errorMessage) {
        try {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setRequest(601); // ILLEGAL_REQUEST
            errorResponse.setContent(errorMessage);
            errorResponse.setSessionId(connection.getSessionId());
            errorResponse.setVersion("Server2.0");
            
            // 使用NIO协议发送
            ResponseParameter responseParameter = ResponseParameter.fromNioConnection(connection);
            responseParameter.writeError(500, errorMessage);
            
        } catch (Exception e) {
            logger.error("发送错误响应时发生错误", e);
        }
    }
    
    /**
     * 广播消息给多个连接
     */
    public void broadcastToConnections(java.util.List<NioConnection> connections, ResponseMessage message) {
        for (NioConnection connection : connections) {
            try {
                if (!connection.isClosed()) {
                    ResponseParameter responseParameter = ResponseParameter.fromNioConnection(connection);
                    responseParameter.write(message);
                }
            } catch (Exception e) {
                logger.warn("广播消息到连接时发生错误: {}", e.getMessage());
            }
        }
    }
    
    /**
     * 发送消息给指定用户的所有连接
     */
    public void sendToUser(Integer userId, ResponseMessage message) {
        try {
            java.util.List<IMUserSession> userSessions = IMSessionManager.getUserSessions(userId);
            for (IMUserSession session : userSessions) {
                // 这里需要根据Session找到对应的NioConnection
                // 实际实现中可能需要维护Session到Connection的映射
                logger.debug("发送消息给用户: {} Session: {}", userId, session.getId());
            }
        } catch (Exception e) {
            logger.error("发送消息给用户时发生错误", e);
        }
    }
}

