package zzuli.zw.pojo;

import java.io.Serializable;

/**
 * @ClassName FriendRequestRecord
 * @Description: 好友申请表
 * @Author 索半斤
 * @Datetime 2022年 11月 03日 18:41
 * @Version: 1.0
 */
public class FriendRequestRecord implements Serializable {
    private int id;  //
    private User from;  //申请来源
    private User to;    //向谁申请
    private String intro;  //介绍
    private int state;     //消息处理状态

    @Override
    public String toString() {
        return "FriendRequestRecord{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", intro='" + intro + '\'' +
                ", state=" + state +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
