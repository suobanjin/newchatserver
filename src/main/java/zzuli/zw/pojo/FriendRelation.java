package zzuli.zw.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FriendRelation
 * @Description: 好友关系实体类
 * @Author 索半斤
 * @Datetime 2022年 11月 02日 18:46
 * @Version: 1.0
 */
public class FriendRelation implements Serializable {
    private int id;
    private User user;
    private Friend friend;
    private FriendGroup friendGroup;
    private String friendRemark;
    private Date addDate;

    @Override
    public String toString() {
        return "FriendRelation{" +
                "id=" + id +
                ", user=" + user +
                ", friend=" + friend +
                ", friendGroup=" + friendGroup +
                ", friendRemark='" + friendRemark + '\'' +
                ", addDate=" + addDate +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public FriendGroup getFriendGroup() {
        return friendGroup;
    }

    public void setFriendGroup(FriendGroup friendGroup) {
        this.friendGroup = friendGroup;
    }

    public String getFriendRemark() {
        return friendRemark;
    }

    public void setFriendRemark(String friendRemark) {
        this.friendRemark = friendRemark;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }
}
