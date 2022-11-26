package zzuli.zw.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName Group
 * @Description: 好友分组实体类
 * @Author 索半斤
 * @Datetime 2022年 11月 02日 9:31
 * @Version: 1.0
 */
public class FriendGroup implements Serializable {
    private int id;               //id
    private String groupingName; //分组名称
    private int groupingType;    //分组类型
    private User user;           //分组所属用户
    List<Friend> friendList;    //分组所属的好友列表

    @Override
    public String toString() {
        return "FriendGroup{" +
                "id=" + id +
                ", groupingName='" + groupingName + '\'' +
                ", groupingType=" + groupingType +
                ", user=" + user +
                ", friendList=" + friendList +
                '}';
    }

    public List<Friend> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<Friend> friendList) {
        this.friendList = friendList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendGroup that = (FriendGroup) o;
        return id == that.id && Objects.equals(groupingName, that.groupingName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, groupingName);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupingName() {
        return groupingName;
    }

    public void setGroupingName(String groupingName) {
        this.groupingName = groupingName;
    }

    public int getGroupingType() {
        return groupingType;
    }

    public void setGroupingType(int groupingType) {
        this.groupingType = groupingType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
