package zzuli.zw.main.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import zzuli.zw.main.connection.NioConnection;
import zzuli.zw.main.factory.ObjectMapperFactory;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.utils.ProtocolUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;

/**
 * @ClassName ResponseParameter
 * @Description TODO
 * @Author
 * @Date 2021/2/12 19:58
 * @Version 1.0
 */
public class ResponseParameter {
    private Socket responseSocket;
    private ResponseMessage jsonResult;
    private int code;
    private int responseStatus;


    @Override
    public String toString() {
        return "ResponseParameter{" +
                "responseSocket=" + responseSocket +
                ", jsonResult=" + jsonResult +
                ", code=" + code +
                ", responseStatus=" + responseStatus +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void write() throws IOException {
        ProtocolUtils.send(jsonResult, responseSocket);
    }
    public OutputStream getOutputStream() throws IOException {
        return responseSocket.getOutputStream();
    }
    public void write(ResponseMessage data) throws IOException {
        this.jsonResult = data;
        data.setSendTime(new Date().getTime());
        data.setVersion("Server1.0");
        if (data.getContent() != null){
            data.setContentLength(data.getContent().length());
        }
        
        // 检查是否为NIO连接
        if (nioConnection != null && !nioConnection.isClosed()) {
            // 使用NIO协议发送
            try {
                zzuli.zw.main.protocol.BinaryFrame frame = zzuli.zw.main.protocol.BinaryCodec.encodeResponse(data);
                nioConnection.sendFrame(frame);
                System.out.println("NIO send ---> " + data.getContent());
            } catch (Exception e) {
                throw new IOException("NIO发送失败", e);
            }
        } else if (responseSocket != null && !responseSocket.isClosed()) {
            // 使用传统BIO发送
            ObjectMapper objectMapper = ObjectMapperFactory.getInstance();
            String string = objectMapper.writeValueAsString(data);
            System.out.println("BIO send ---> " + string);
            OutputStream outputStream = responseSocket.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(outputStream)
            );
            bufferedWriter.write(string+"\n");
            bufferedWriter.newLine();
            bufferedWriter.write("EOF\n");
            bufferedWriter.flush();
        } else {
            throw new IOException("没有可用的连接发送数据");
        }
    }
    public Socket getResponseSocket() {
        return responseSocket;
    }

    public void setResponseSocket(Socket responseSocket) {
        this.responseSocket = responseSocket;
    }
    
    // NIO连接支持
    private NioConnection nioConnection;
    
    public NioConnection getNioConnection() {
        return nioConnection;
    }
    
    public void setNioConnection(NioConnection nioConnection) {
        this.nioConnection = nioConnection;
    }

    public ResponseMessage getJsonResult() {
        return jsonResult;
    }

    public void setJsonResult(ResponseMessage jsonResult) {
        this.jsonResult = jsonResult;
    }


    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
        jsonResult.setRequest(responseStatus);
    }
}
