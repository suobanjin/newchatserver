package zzuli.zw.pojo.model;

import java.io.Serializable;

/**
 * @ClassName ResponseModel
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 27日 15:39
 * @Version: 1.0
 */
public class ResponseModel<T> implements Serializable {
    private T data;
    private String content;
    private int info;

    @Override
    public String toString() {
        return "ResponseModel{" +
                "data=" + data +
                ", content='" + content + '\'' +
                ", code=" + info +
                '}';
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getInfo() {
        return info;
    }

    public void setInfo(int info) {
        this.info = info;
    }
}
