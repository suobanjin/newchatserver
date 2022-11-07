package zzuli.zw.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName GroupGroup
 * @Description: 群分组，按照手机端qq来说，群分组是后端自动完成的，假如说你既有自己创建的也有加入的，那么
 * 后端会默认将你的群组分成两组并将群存入合适的群组内，同时手机端实际上是不支持自己创建分组的，但是电脑端就可以。
 * @Author 索半斤
 * @Datetime 2022年 11月 02日 21:22
 * @Version: 1.0
 */
public class GroupGroup implements Serializable {
    private int id;
    private String groupGroupName;  //分组名称
    private User owner;  //分组创建者

    List<Group> groupList;  //分组包含的群组


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupGroup that = (GroupGroup) o;
        return id == that.id && Objects.equals(groupGroupName, that.groupGroupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, groupGroupName);
    }

    @Override
    public String toString() {
        return "GroupGroup{" +
                "id=" + id +
                ", groupGroupName='" + groupGroupName + '\'' +
                ", owner=" + owner +
                ", groupList=" + groupList +
                '}';
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupGroupName() {
        return groupGroupName;
    }

    public void setGroupGroupName(String groupGroupName) {
        this.groupGroupName = groupGroupName;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
