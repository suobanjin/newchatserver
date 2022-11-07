package zzuli.zw.domain;

import java.io.Serializable;

/**
 * @author 索半斤
 * @description 角色类
 * @date 2022/1/16
 * @className Role
 */
public class Role implements Serializable {
    private int id;
    private String roleName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
