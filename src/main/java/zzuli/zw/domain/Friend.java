package zzuli.zw.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName Friend
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/17 21:52
 * @Version 1.0
 */
public class Friend extends User implements Serializable {
    private int friendRelationId;  //好友关系id
    private int userId;            //用户id
    private int friendId;          //好友id
    private String remark;         //备注
    private FriendGroup friendGroup;  //好友分组信息

    @Override
    public String toString() {
        String append = super.toString().replace("User{","")
                                        .replace("}",",");
        return "Friend{"+ append +
                "friendRelationId=" + friendRelationId +
                ", userId=" + userId +
                ", friendId=" + friendId +
                ", remark='" + remark + '\'' +
                ", friendGroup=" + friendGroup +
                '}';
    }

    public int getFriendRelationId() {
        return friendRelationId;
    }

    public void setFriendRelationId(int friendRelationId) {
        this.friendRelationId = friendRelationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public FriendGroup getFriendGroup() {
        return friendGroup;
    }

    public void setFriendGroup(FriendGroup friendGroup) {
        this.friendGroup = friendGroup;
    }
}
