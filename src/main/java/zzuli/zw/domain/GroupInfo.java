package zzuli.zw.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName GroupInfo
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/26 15:51
 * @Version 1.0
 */
public class GroupInfo implements Serializable {
    private int id;
    private String groupName;
    private User creator;
    private Date createDate;
    private String groupPicture;
    private String groupInfo;
    private List<GroupUser> groupUsers;

    public List<GroupUser> getGroupUsers() {
        return groupUsers;
    }

    public void setGroupUsers(List<GroupUser> groupUsers) {
        this.groupUsers = groupUsers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getGroupPicture() {
        return groupPicture;
    }

    public void setGroupPicture(String groupPicture) {
        this.groupPicture = groupPicture;
    }

    public String getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(String groupInfo) {
        this.groupInfo = groupInfo;
    }
}
