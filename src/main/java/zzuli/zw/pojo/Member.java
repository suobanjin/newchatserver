package zzuli.zw.pojo;

import java.io.Serializable;

/**
 * @ClassName Member
 * @Description: 群组成员实体类，该实体类设计实际上还是有一点疑问的，就是说关于群主是否应该
 * 属于群员，当然这个和表的设计也有很大的关系，当前的设计实际上还是不太完美的，需要改进。
 * @Author 索半斤
 * @Datetime 2022年 11月 03日 11:01
 * @Version: 1.0
 */
public class Member extends User implements Serializable {
    private String memberName;
    private int admin;  //是否为管理员

    private int owner; //是否为群主

    @Override
    public String toString() {
        return "Member{" +
                "memberName='" + memberName + '\'' +
                ", admin=" + admin +
                ", owner=" + owner +
                '}';
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }
}
