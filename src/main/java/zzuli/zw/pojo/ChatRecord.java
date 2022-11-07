package zzuli.zw.pojo;

import java.io.Serializable;

/**
 * @ClassName ChatRecord
 * @Description: 消息记录，一般来说消息不需要永久存储在数据库中，我们只存储短时间内的群消息或者离线消息等
 * @Author 索半斤
 * @Datetime 2022年 11月 03日 20:01
 * @Version: 1.0
 */
public class ChatRecord implements Serializable {
    private int id;
    private User from;
    private User to;
    private Group group;
    private int read;
    private Message message;

    @Override
    public String toString() {
        return "ChatRecord{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", group=" + group +
                ", read=" + read +
                ", message=" + message +
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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
