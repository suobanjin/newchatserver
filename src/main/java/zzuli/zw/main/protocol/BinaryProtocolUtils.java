package zzuli.zw.main.protocol;

import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.factory.SessionContainer;
import zzuli.zw.main.interfaces.Session;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 二进制协议工具类
 * 替代原有的 ProtocolUtils，保持 Session 机制兼容
 */
public class BinaryProtocolUtils {
    
    /**
     * 发送二进制帧（Socket 版本，兼容现有代码）
     */
    public static void send(ResponseMessage response, Socket socket) throws IOException {
        BinaryFrame frame = BinaryCodec.encodeResponse(response);
        ByteBuffer[] buffers = frame.encode();
        
        // 写入所有缓冲区
        for (ByteBuffer buffer : buffers) {
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            socket.getOutputStream().write(data);
        }
        socket.getOutputStream().flush();
    }
    
    /**
     * 接收二进制帧（Socket 版本，兼容现有代码）
     */
    public static ResponseMessage receive(Socket socket) throws IOException {
        // 读取 Magic + Length
        byte[] header = new byte[6]; // Magic(2) + Length(4)
        int bytesRead = socket.getInputStream().read(header);
        if (bytesRead != 6) {
            return null; // 数据不足
        }
        
        ByteBuffer buffer = ByteBuffer.wrap(header);
        buffer.order(java.nio.ByteOrder.BIG_ENDIAN);
        short magic = buffer.getShort();
        if (magic != BinaryFrame.MAGIC) {
            return null; // Magic 不匹配
        }
        
        int length = buffer.getInt();
        if (length <= 0 || length > 1024 * 1024) { // 限制最大 1MB
            return null; // 长度异常
        }
        
        // 读取剩余数据
        byte[] data = new byte[6 + length];
        System.arraycopy(header, 0, data, 0, 6);
        
        int remaining = length;
        int offset = 6;
        while (remaining > 0) {
            int read = socket.getInputStream().read(data, offset, remaining);
            if (read <= 0) {
                return null; // 连接断开
            }
            remaining -= read;
            offset += read;
        }
        
        // 解码帧
        ByteBuffer fullBuffer = ByteBuffer.wrap(data);
        List<BinaryFrame> frames = BinaryCodec.decode(fullBuffer);
        if (frames.isEmpty()) {
            return null;
        }
        
        BinaryFrame frame = frames.get(0);
        ResponseMessage response = BinaryCodec.decodeToResponse(frame);
        
        // 保持 Session 机制
        if (frame.getSessionId() != null && !frame.getSessionId().trim().isEmpty()) {
            Session session = SessionContainer.getSession(frame.getSessionId());
            if (session != null) {
                response.setSessionId(session.getId());
            }
        }
        
        return response;
    }
    
    /**
     * 发送二进制帧（NIO 版本）
     */
    public static void sendNio(ResponseMessage response, SocketChannel channel) throws IOException {
        BinaryFrame frame = BinaryCodec.encodeResponse(response);
        ByteBuffer[] buffers = frame.encode();
        
        for (ByteBuffer buffer : buffers) {
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
        }
    }
    
    /**
     * 从 ByteBuffer 接收二进制帧（NIO 版本）
     */
    public static List<ResponseMessage> receiveNio(ByteBuffer buffer) {
        List<BinaryFrame> frames = BinaryCodec.decode(buffer);
        List<ResponseMessage> responses = new ArrayList<>();
        
        for (BinaryFrame frame : frames) {
            try {
                ResponseMessage response = BinaryCodec.decodeToResponse(frame);
                
                // 保持 Session 机制
                if (frame.getSessionId() != null && !frame.getSessionId().trim().isEmpty()) {
                    Session session = SessionContainer.getSession(frame.getSessionId());
                    if (session != null) {
                        response.setSessionId(session.getId());
                    }
                }
                
                responses.add(response);
            } catch (Exception e) {
                // 解码失败，跳过此帧
                e.printStackTrace();
            }
        }
        
        return responses;
    }
    
    /**
     * 创建心跳帧
     */
    public static BinaryFrame createHeartbeat(String sessionId) {
        return BinaryCodec.createPingFrame(sessionId);
    }
    
    /**
     * 创建心跳响应帧
     */
    public static BinaryFrame createHeartbeatResponse(String sessionId) {
        return BinaryCodec.createPongFrame(sessionId);
    }
}

