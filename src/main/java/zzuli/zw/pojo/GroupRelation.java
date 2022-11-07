package zzuli.zw.pojo;

import java.io.Serializable;

/**
 * @ClassName GroupRelation
 * @Description: 群组关系表
 * @Author 索半斤
 * @Datetime 2022年 11月 07日 17:31
 * @Version: 1.0
 */
public class GroupRelation implements Serializable {
    private int id;
    private Group group;
    private GroupGroup grouping;
    private User user;

    @Override
    public String toString() {
        return "GroupRelation{" +
                "id=" + id +
                ", group=" + group +
                ", grouping=" + grouping +
                ", user=" + user +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public GroupGroup getGrouping() {
        return grouping;
    }

    public void setGrouping(GroupGroup grouping) {
        this.grouping = grouping;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
