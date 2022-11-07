package zzuli.zw.domain;

import java.io.Serializable;

/**
 * @author 索半斤
 * @description 群组成员类
 * @date 2022/1/16
 * @className GroupUser
 */
public class GroupUser implements Serializable {
    private int id;
    private int groupId;
    private User user;
    private int userId;
    private Role role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
