package zzuli.zw.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName JsonResult
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/8 13:59
 * @Version 1.0
 */
public class JsonResult<T> implements Serializable {
    private int status;
    private String msg;
    private int count;
    private T data;
    private List<T> list;
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "JsonResult{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", count=" + count +
                ", data=" + data +
                ", list=" + list +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
