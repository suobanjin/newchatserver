package zzuli.zw.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName SystemInfo
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 03日 10:57
 * @Version: 1.0
 */
public class SystemInfo implements Serializable {
    private int id;
    private String content;    //系统通告的信息
    private Date infoDate;     //系统信息发布时间
    private int expire;        //是否过期

    @Override
    public String toString() {
        return "SystemInfo{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", infoDate=" + infoDate +
                ", expire=" + expire +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getInfoDate() {
        return infoDate;
    }

    public void setInfoDate(Date infoDate) {
        this.infoDate = infoDate;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }
}
