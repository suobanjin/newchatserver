package zzuli.zw.main.model.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import zzuli.zw.main.factory.ObjectMapperFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 索半斤
 * @description 协议类
 * @date 2022/1/20
 * @className ResponseMessage
 */
public class ResponseMessage implements Serializable {
    private int code; //状态码
    private String content; //发送的内容，json格式
    private int contentLength; //发送的内容长度
    private String sessionId;  //sessionId
    private long sendTime;   //消息发送时间
    private int request;     //请求内容
    private Map<String,String> cookies;  //cookies
    private boolean keepAlive = true; //是否是长连接，默认为长连接
    private String version = "Server-1.0"; //协议版本

    private String url;

    private int from;  //发送者

    private int to;   //接收者

    private int requestType;  //请求类型

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "code=" + code +
                ", content='" + content + '\'' +
                ", contentLength=" + contentLength +
                ", sessionId='" + sessionId + '\'' +
                ", sendTime=" + sendTime +
                ", request=" + request +
                ", cookies=" + cookies +
                ", keepAlive=" + keepAlive +
                ", version='" + version + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", requestType=" + requestType +
                '}';
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setContentMap(String content) {
        this.content = content;
    }

    public void setContentMap(Map objectData){
        if (objectData == null || objectData.size() == 0)return;
        try {
            this.content = ObjectMapperFactory.getInstance().writeValueAsString(objectData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void setContentObject(Object o){
        ObjectMapper instance = ObjectMapperFactory.getInstance();
        try {
            this.setContentMap(instance.writeValueAsString(o));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public int getRequest() {
        return request;
    }

    public void setRequest(int request) {
        this.request = request;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String,String> cookies) {
        this.cookies = cookies;
    }
}
