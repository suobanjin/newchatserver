package zzuli.zw.main.protocol;

import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.factory.ObjectMapperFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 二进制协议编解码器
 * 处理半包/粘包问题，保持与现有 Session 机制的兼容
 */
public class BinaryCodec {
    private static final ObjectMapper objectMapper = ObjectMapperFactory.getInstance();
    
    /**
     * 解码 ByteBuffer 中的完整帧
     */
    public static List<BinaryFrame> decode(ByteBuffer buffer) {
        List<BinaryFrame> frames = new ArrayList<>();
        
        while (buffer.hasRemaining()) {
            BinaryFrame frame = BinaryFrame.decode(buffer);
            if (frame == null) {
                break; // 数据不足或格式错误
            }
            frames.add(frame);
        }
        
        return frames;
    }
    
    /**
     * 将 ResponseMessage 编码为 BinaryFrame
     */
    public static BinaryFrame encodeResponse(ResponseMessage response) {
        try {
            // 将 ResponseMessage 序列化为 JSON
            String json = objectMapper.writeValueAsString(response);
            byte[] payload = json.getBytes("UTF-8");
            
            // 使用现有的 request 字段作为 type
            byte type = (byte) response.getRequest();
            
            return new BinaryFrame(type, response.getSessionId(), payload);
        } catch (Exception e) {
            throw new RuntimeException("编码失败", e);
        }
    }
    
    /**
     * 将 BinaryFrame 解码为 ResponseMessage
     */
    public static ResponseMessage decodeToResponse(BinaryFrame frame) {
        try {
            String json = new String(frame.getPayload(), "UTF-8");
            ResponseMessage response = objectMapper.readValue(json, ResponseMessage.class);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("解码失败", e);
        }
    }
    
    /**
     * 创建简单的二进制消息帧
     */
    public static BinaryFrame createFrame(byte type, String sessionId, String content) {
        return new BinaryFrame(type, sessionId, content.getBytes());
    }
    
    /**
     * 创建心跳帧
     */
    public static BinaryFrame createPingFrame(String sessionId) {
        return new BinaryFrame((byte) 5, sessionId, new byte[0]); // PING = 5
    }
    
    /**
     * 创建心跳响应帧
     */
    public static BinaryFrame createPongFrame(String sessionId) {
        return new BinaryFrame((byte) 6, sessionId, new byte[0]); // PONG = 6
    }
    
    /**
     * 将ResponseMessage编码为完整的字节数组（包含帧头和负载）
     * 用于NIO连接中的直接发送
     */
    public static byte[] encodeFrame(ResponseMessage response) {
        try {
            BinaryFrame frame = encodeResponse(response);
            ByteBuffer[] buffers = frame.encode();
            
            // 计算总长度
            int totalLength = 0;
            for (ByteBuffer buffer : buffers) {
                totalLength += buffer.remaining();
            }
            
            // 创建结果数组
            byte[] result = new byte[totalLength];
            int position = 0;
            
            // 复制所有字节
            for (ByteBuffer buffer : buffers) {
                int remaining = buffer.remaining();
                buffer.get(result, position, remaining);
                position += remaining;
            }
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("编码帧失败", e);
        }
    }
}

