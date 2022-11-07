package zzuli.zw.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName Friend
 * @Description: 该类表示好友，我们通过继承User类来完成设计，和User做简单的区分
 * @Author 索半斤
 * @Datetime 2022年 11月 02日 18:06
 * @Version: 1.0
 */
public class Friend extends User implements Serializable {
    private String remark; //好友备注
    private Date addDate;  //好友添加时间

    public Friend() {
    }

    @Override
    public String toString() {
        return "Friend{" + super.toString()+
                "remark='" + remark + '\'' +
                ", addDate=" + addDate +
                '}';
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }
}
