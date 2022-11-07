package zzuli.zw.main.model;

import zzuli.zw.main.model.ResponseMessage;
import zzuli.zw.main.utils.ProtocolUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

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
        ProtocolUtils.send(jsonResult, responseSocket);
    }
    public Socket getResponseSocket() {
        return responseSocket;
    }

    public void setResponseSocket(Socket responseSocket) {
        this.responseSocket = responseSocket;
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
