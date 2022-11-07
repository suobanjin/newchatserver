package zzuli.zw.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName LoginInfo
 * @Description: 用户登陆后则生成登录信息，我们可以根据登录信息查找登录记录
 * @Author 索半斤
 * @Datetime 2022年 11月 02日 23:20
 * @Version: 1.0
 */
public class LoginInfo implements Serializable {
    private int id;
    private String loginIp;
    private Date loginTime;
    private int online;
    private User loginUser;

    @Override
    public String toString() {
        return "LoginInfo{" +
                "id=" + id +
                ", loginIp='" + loginIp + '\'' +
                ", loginTime=" + loginTime +
                ", online=" + online +
                ", loginUser=" + loginUser +
                '}';
    }

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
