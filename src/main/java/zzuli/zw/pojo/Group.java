package zzuli.zw.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName Group
 * @Description: 群组信息表，代表了一个群组的基本信息。
 * @Author 索半斤
 * @Datetime 2022年 11月 02日 21:22
 * @Version: 1.0
 */
public class Group implements Serializable {
    private int id;
    private User owner;   //群组的创建者
    private String groupAvatar;   //群组头像
    private GroupGroup groupGroup;   //群组所属分组

    private String groupName;  //群名称

    private String groupIntro;  //群简介
    private List<Member> memberList;   //群组所属成员

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", owner=" + owner +
                ", groupAvatar='" + groupAvatar + '\'' +
                ", groupGroup=" + groupGroup +
                ", groupName='" + groupName + '\'' +
                ", groupIntro='" + groupIntro + '\'' +
                ", memberList=" + memberList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id && Objects.equals(groupAvatar, group.groupAvatar) && Objects.equals(groupName, group.groupName) && Objects.equals(groupIntro, group.groupIntro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, groupAvatar, groupName, groupIntro);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupIntro() {
        return groupIntro;
    }

    public void setGroupIntro(String groupIntro) {
        this.groupIntro = groupIntro;
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getGroupAvatar() {
        return groupAvatar;
    }

    public void setGroupAvatar(String groupAvatar) {
        this.groupAvatar = groupAvatar;
    }

    public GroupGroup getGroupGroup() {
        return groupGroup;
    }

    public void setGroupGroup(GroupGroup groupGroup) {
        this.groupGroup = groupGroup;
    }
}
