package zzuli.zw.main.connection;

import lombok.extern.slf4j.Slf4j;
import zzuli.zw.main.ioc.ServerContext;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.main.protocol.BinaryFrame;
import zzuli.zw.main.protocol.BinaryCodec;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.manager.IMSessionManager;
import zzuli.zw.main.model.IMUserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * NIO协议处理器
 * 处理二进制协议的编解码和消息分发
 */
@Slf4j
public class NioProtocolHandler {

    private final NioRequestAdapter requestAdapter;
    private final ServerContext serverContext;
    private final NioRequestDispatcher dispatcher;
    public NioProtocolHandler(ServerContext serverContext) {
        this.serverContext = serverContext;
        this.requestAdapter = new NioRequestAdapter(serverContext);
        this.dispatcher = new NioRequestDispatcher(serverContext);
    }

    private void handleMessageFrame(RequestParameter requestParameter, ResponseParameter responseParameter) {
        try {
            // 交由通用适配器处理（包含拦截器链 + 分发）
            requestAdapter.process(requestParameter, responseParameter);

        } catch (Exception e) {
            log.error("处理消息帧时发生错误", e);
            try {
                responseParameter.writeError(500, "处理消息失败");
            }catch (IOException ex){
                log.error("发送错误响应时发生错误", ex);
            }
            // sendErrorResponse(connection, "处理消息失败");
        }
    }
    /**
     * 处理接收到的数据
     */
    public void handleReceivedData(NioConnection connection, NioRequestDispatcher dispatcher) {
        try {
            ByteBuffer readBuffer = connection.getReadBuffer();
            
            // 解码二进制帧
            List<BinaryFrame> frames = BinaryCodec.decode(readBuffer);
            
            for (BinaryFrame frame : frames) {
                handleFrame(connection, frame, dispatcher);
            }
            
        } catch (Exception e) {
            log.error("处理接收数据时发生错误: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理单个二进制帧
     */
    private void handleFrame(NioConnection connection, BinaryFrame frame, NioRequestDispatcher dispatcher) {
        try {
            // 将帧转换为ResponseMessage
            ResponseMessage response = BinaryCodec.decodeToResponse(frame);
            // 设置连接信息
            connection.setSessionId(frame.getSessionId());
            // 更新连接心跳
            connection.updateHeartbeat();
            // 创建请求参数
            RequestParameter requestParameter = createRequestParameter(connection, response);
            // 创建响应参数
            ResponseParameter responseParameter = createResponseParameter(requestParameter);
            // 处理不同类型的消息，消息会通过拦截器，通过登录认证拦截器判断是否是登录用户
            switch (frame.getType()) {
                case 1: // AUTH - 认证
                    handleAuthFrame(requestParameter, responseParameter);
                    break;
                case 2: // MESSAGE - 消息
                    handleMessageFrame(requestParameter, responseParameter);
                    break;
                case 3: // File - 文件
                    handleSyncFrame(requestParameter, responseParameter);
                    break;
                case 4: // PING - 心跳
                    handlePingFrame(connection, frame);
                    break;
                case 5: // PONG - 心跳响应
                    handlePongFrame(connection, frame);
                    break;
                default:
                    log.warn("未知的消息类型: {}", frame.getType());
                    sendErrorResponse(connection, "未知的消息类型");
            }
            
        } catch (Exception e) {
            log.error("处理帧时发生错误: {}", e.getMessage(), e);
            sendErrorResponse(connection, "处理消息时发生错误");
        }
    }
    
    /**
     * 处理认证帧
     */
    private void handleAuthFrame(RequestParameter requestParameter,ResponseParameter responseParameter) {
        try {
            // 分发请求
            dispatcher.dispatchRequest(requestParameter, responseParameter);
        } catch (Exception e) {
            log.error("处理认证帧时发生错误", e);
            try {
                responseParameter.writeError(500, "认证失败");
            }catch (IOException ex){
                log.error("发送错误响应时发生错误", ex);
            }
            // sendErrorResponse(this.connection, "认证失败");
        }
    }

    /**
     * 处理文件
     */
    private void handleSyncFrame(RequestParameter requestParameter, ResponseParameter responseParameter) {
        try {
            dispatcher.dispatchRequest(requestParameter, responseParameter);
        } catch (Exception e) {
            log.error("处理同步帧时发生错误", e);
            try {
                responseParameter.writeError(500, "同步失败");
            }catch (IOException ex){
                log.error("发送错误响应时发生错误", ex);
            }
            //sendErrorResponse(connection, "同步失败");
        }
    }
    
    /**
     * 处理心跳帧
     */
    private void handlePingFrame(NioConnection connection, BinaryFrame frame) {
        try {
            // 更新心跳时间
            connection.updateHeartbeat();
            
            // 发送PONG响应
            BinaryFrame pongFrame = BinaryCodec.createPongFrame(frame.getSessionId());
            connection.sendFrame(pongFrame);
            
            log.debug("处理心跳请求: {}", connection.getConnectionInfo());
            
        } catch (Exception e) {
            log.error("处理心跳帧时发生错误", e);
        }
    }
    
    /**
     * 处理心跳响应帧
     */
    private void handlePongFrame(NioConnection connection, BinaryFrame frame) {
        try {
            // 更新心跳时间
            connection.updateHeartbeat();
            
            log.debug("收到心跳响应: {}", connection.getConnectionInfo());
            
        } catch (Exception e) {
            log.error("处理心跳响应帧时发生错误", e);
        }
    }
    /**
     * 创建请求参数
     */
    private RequestParameter createRequestParameter(NioConnection connection, ResponseMessage responseMessage) {
        // 使用静态工厂方法创建请求参数
        RequestParameter requestParameter = RequestParameter.fromNioConnection(connection, responseMessage);
        // 设置服务器上下文
        requestParameter.setServerContext(this.serverContext);
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
     * 处理错误帧
     */
//    private void handleErrorFrame(NioConnection connection, BinaryFrame frame, NioRequestDispatcher dispatcher) {
//        try {
//            ResponseMessage response = BinaryCodec.decodeToResponse(frame);
//            dispatcher.dispatchRequest(connection, response);
//
//        } catch (Exception e) {
//            log.error("处理错误帧时发生错误", e);
//        }
//    }
    
    /**
     * 验证Session
     */
    private boolean validateSession(NioConnection connection, String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return false;
        }
        
        // 检查Session是否存在且有效
        IMUserSession session = IMSessionManager.getUserSession(sessionId);
        if (session == null || !session.isActive()) {
            return false;
        }
        
        // 更新连接信息
        connection.setSessionId(sessionId);
        connection.setDeviceId(session.getDeviceId());
        
        return true;
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(NioConnection connection, String errorMessage) {
        try {
            // 创建错误帧
            BinaryFrame errorFrame = BinaryCodec.createFrame((byte) 7, connection.getSessionId(), 
                "{\"error\":\"" + errorMessage + "\"}");
            
            connection.sendFrame(errorFrame);
            
        } catch (Exception e) {
            log.error("发送错误响应时发生错误", e);
        }
    }
    
    /**
     * 发送成功响应
     */
    public void sendSuccessResponse(NioConnection connection, String sessionId, String content) {
        try {
            BinaryFrame successFrame = BinaryCodec.createFrame((byte) 2, sessionId, content);
            connection.sendFrame(successFrame);
            
        } catch (Exception e) {
            log.error("发送成功响应时发生错误", e);
        }
    }
    
    /**
     * 发送心跳包
     */
    public void sendHeartbeat(NioConnection connection) {
        try {
            BinaryFrame pingFrame = BinaryCodec.createPingFrame(connection.getSessionId());
            connection.sendFrame(pingFrame);
            
        } catch (Exception e) {
            log.error("发送心跳包时发生错误", e);
        }
    }
    
    /**
     * 发送消息给指定连接
     */
    public void sendMessageToConnection(NioConnection connection, ResponseMessage message) {
        try {
            BinaryFrame frame = BinaryCodec.encodeResponse(message);
            connection.sendFrame(frame);
            
        } catch (Exception e) {
            log.error("发送消息到连接时发生错误", e);
        }
    }
}

