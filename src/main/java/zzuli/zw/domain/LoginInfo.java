package zzuli.zw.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 索半斤
 * @description 登录后的用户信息
 * @date 2022/1/17
 * @className LoginInfo
 */
public class LoginInfo implements Serializable {
    private int id;
    private String loginIp;
    private String loginPort;
    private Date loginTime;
    private int online;
    private User loginUser;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getLoginPort() {
        return loginPort;
    }

    public void setLoginPort(String loginPort) {
        this.loginPort = loginPort;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public User getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
    }

}
