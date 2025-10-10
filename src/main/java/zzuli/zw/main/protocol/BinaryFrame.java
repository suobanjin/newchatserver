package zzuli.zw.main.protocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 二进制帧协议
 * 格式: [Magic(2)][Length(4)][Type(1)][SessionId(32)][Payload(变长)]
 */
public class BinaryFrame {
    public static final short MAGIC = (short) 0xCAFE;
    public static final int SESSION_ID_LENGTH = 32;
    
    private final byte type;
    private final String sessionId;
    private final byte[] payload;
    
    public BinaryFrame(byte type, String sessionId, byte[] payload) {
        this.type = type;
        this.sessionId = sessionId;
        this.payload = payload;
    }
    
    /**
     * 编码为 ByteBuffer 数组
     */
    public ByteBuffer[] encode() {
        int payloadLen = payload != null ? payload.length : 0;
        int totalLen = 1 + SESSION_ID_LENGTH + payloadLen; // Type + SessionId + Payload
        
        ByteBuffer header = ByteBuffer.allocate(2 + 4 + 1 + SESSION_ID_LENGTH);
        header.order(ByteOrder.BIG_ENDIAN);
        header.putShort(MAGIC);
        header.putInt(totalLen);
        header.put(type);
        
        // 填充 SessionId 到 32 字节
        byte[] sessionBytes = new byte[SESSION_ID_LENGTH];
        if (sessionId != null) {
            byte[] original = sessionId.getBytes();
            System.arraycopy(original, 0, sessionBytes, 0, 
                Math.min(original.length, SESSION_ID_LENGTH));
        }
        header.put(sessionBytes);
        header.flip();
        
        if (payload != null && payload.length > 0) {
            return new ByteBuffer[]{header, ByteBuffer.wrap(payload)};
        } else {
            return new ByteBuffer[]{header};
        }
    }
    
    /**
     * 从 ByteBuffer 解码
     */
    public static BinaryFrame decode(ByteBuffer buffer) {
        if (buffer.remaining() < 2 + 4 + 1 + SESSION_ID_LENGTH) {
            return null; // 数据不足
        }
        
        buffer.mark();
        short magic = buffer.getShort();
        if (magic != MAGIC) {
            buffer.reset();
            return null; // Magic 不匹配
        }
        
        int length = buffer.getInt();
        if (buffer.remaining() < length) {
            buffer.reset();
            return null; // 数据不足
        }
        
        byte type = buffer.get();
        
        // 读取 SessionId
        byte[] sessionBytes = new byte[SESSION_ID_LENGTH];
        buffer.get(sessionBytes);
        String sessionId = new String(sessionBytes).trim();
        
        // 读取 Payload
        byte[] payload = new byte[length - 1 - SESSION_ID_LENGTH];
        if (payload.length > 0) {
            buffer.get(payload);
        }
        
        return new BinaryFrame(type, sessionId, payload);
    }
    
    // Getters
    public byte getType() { return type; }
    public String getSessionId() { return sessionId; }
    public byte[] getPayload() { return payload; }
}

