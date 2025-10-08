package zzuli.zw.main.model;

import zzuli.zw.main.connection.NioConnection;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.protocol.BinaryCodec;
import zzuli.zw.main.protocol.BinaryFrame;

import java.io.IOException;
import java.util.Date;

/**
 * @ClassName ResponseParameter
 * @Description 专为NIO模式优化的响应参数类
 * @Author 索半斤
 * @Date 2021/2/12 19:58
 * @Version 2.0
 */
public class ResponseParameter {
    // NIO连接支持
    private NioConnection nioConnection;
    private ResponseMessage responseMessage;
    private int code;
    private int responseStatus;
    private String protocolVersion = "Server2.0";
    private long responseTime;

    /**
     * 构造函数，初始化响应时间
     */
    public ResponseParameter() {
        this.responseTime = System.currentTimeMillis();
    }

    /**
     * 静态工厂方法，从NIO连接创建响应参数
     */
    public static ResponseParameter fromNioConnection(NioConnection connection) {
        ResponseParameter parameter = new ResponseParameter();
        parameter.setNioConnection(connection);
        return parameter;
    }

    /**
     * 静态工厂方法，从请求参数创建响应参数
     */
    public static ResponseParameter fromRequestParameter(RequestParameter requestParameter) {
        ResponseParameter responseParameter = new ResponseParameter();
        responseParameter.setNioConnection(requestParameter.getNioConnection());
        responseParameter.setProtocolVersion(requestParameter.getProtocolVersion());
        
        // 创建响应消息并复制必要的会话信息
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setSessionId(requestParameter.getSessionId());
        responseMessage.setFrom(requestParameter.getFrom());
        responseMessage.setTo(requestParameter.getTo());
        responseMessage.setRequest(requestParameter.getRequest());
        responseMessage.setKeepAlive(requestParameter.isKeepAlive());
        
        responseParameter.setResponseMessage(responseMessage);
        return responseParameter;
    }

    @Override
    public String toString() {
        return "ResponseParameter{" +
                "nioConnection=" + (nioConnection != null ? nioConnection.getConnectionInfo() : "null") +
                ", responseMessage=" + responseMessage +
                ", code=" + code +
                ", responseStatus=" + responseStatus +
                ", protocolVersion='" + protocolVersion + "'" +
                ", responseTime=" + responseTime +
                '}';
    }

    /**
     * 发送响应消息
     */
    public void write() throws IOException {
        if (responseMessage == null) {
            throw new IOException("响应消息不能为空");
        }
        sendResponse(responseMessage);
    }

    /**
     * 发送指定的响应消息
     */
    public void write(ResponseMessage message) throws IOException {
        this.responseMessage = message;
        sendResponse(message);
    }

    /**
     * 创建并发送成功响应
     */
    public void writeSuccess(Object content) throws IOException {
        ResponseMessage response = createSuccessResponse(content);
        sendResponse(response);
    }

    /**
     * 创建并发送错误响应
     */
    public void writeError(int errorCode, String errorMsg) throws IOException {
        ResponseMessage response = createErrorResponse(errorCode, errorMsg);
        sendResponse(response);
    }

    /**
     * 发送响应的核心方法
     */
    private void sendResponse(ResponseMessage message) throws IOException {
        // 确保连接有效
        if (nioConnection == null || nioConnection.isClosed()) {
            throw new IOException("NIO连接不可用或已关闭");
        }

        // 设置响应元数据
        if (message.getSendTime() <= 0) {
            message.setSendTime(System.currentTimeMillis());
        }
        if (message.getVersion() == null) {
            message.setVersion(protocolVersion);
        }
        
        // 更新响应内容长度
        if (message.getContent() != null) {
            message.setContentLength(message.getContent().length());
        }

        try {
            // 使用NIO协议发送
            BinaryFrame frame = BinaryCodec.encodeResponse(message);
            nioConnection.sendFrame(frame);
        } catch (Exception e) {
            throw new IOException("NIO发送失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建成功响应消息
     */
    public ResponseMessage createSuccessResponse(Object content) {
        ResponseMessage response = new ResponseMessage();
        response.setCode(ResponseCode.SUCCESS);
        response.setContentObject(content);
        response.setVersion(protocolVersion);
        response.setSendTime(System.currentTimeMillis());
        
        // 复制请求中的会话信息
        if (responseMessage != null) {
            response.setSessionId(responseMessage.getSessionId());
            response.setFrom(responseMessage.getFrom());
            response.setTo(responseMessage.getTo());
            response.setRequest(responseMessage.getRequest());
            response.setKeepAlive(responseMessage.isKeepAlive());
        }
        
        return response;
    }

    /**
     * 创建错误响应消息
     */
    public ResponseMessage createErrorResponse(int errorCode, String errorMsg) {
        ResponseMessage response = new ResponseMessage();
        response.setCode(errorCode);
        response.setContent(errorMsg);
        response.setVersion(protocolVersion);
        response.setSendTime(System.currentTimeMillis());
        
        // 复制请求中的会话信息
        if (responseMessage != null) {
            response.setSessionId(responseMessage.getSessionId());
            response.setFrom(responseMessage.getFrom());
            response.setTo(responseMessage.getTo());
            response.setRequest(responseMessage.getRequest());
            response.setKeepAlive(false); // 错误响应后通常不需要保持连接
        }
        
        return response;
    }

    /**
     * 检查连接是否有效
     */
    public boolean isValid() {
        return nioConnection != null && !nioConnection.isClosed();
    }

    /**
     * 获取响应处理耗时
     */
    public long getResponseDuration() {
        return System.currentTimeMillis() - responseTime;
    }

    // Getters and Setters
    public NioConnection getNioConnection() {
        return nioConnection;
    }

    public void setNioConnection(NioConnection nioConnection) {
        this.nioConnection = nioConnection;
    }

    public ResponseMessage getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(ResponseMessage responseMessage) {
        this.responseMessage = responseMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
        if (responseMessage != null) {
            responseMessage.setCode(code);
        }
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
        if (responseMessage != null) {
            responseMessage.setRequest(responseStatus);
        }
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
}
