package zzuli.zw.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName Group
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 02日 9:31
 * @Version: 1.0
 */
public class FriendGroup implements Serializable {
    private int id; //id
    private String groupingName; //分组名称
    private int groupingType;    //分组类型
    private int userId;          //分组所属用户id
    private User user;           //分组所属用户

}
