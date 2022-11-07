package zzuli.zw.config;

import java.io.Serializable;

/**
 * @ClassName RequestType
 * @Description 请求类型
 * @Author 索半斤
 * @Date 2021/1/9 9:01
 * @Version 2.0
 */
public class Router implements Serializable {
    public static final int BEFORE_LOGIN = 101; //登录前请求
    public static final int LOGIN = 102;       //登录请求
    public static final int UPDATE_FRIEND_STATUS = 201;  //更新好友状态
    public static final int DELETE_FRIEND = 301;         //删除好友
    public static final int CLOSE_SOCKET = 401;         //关闭连接
    public static final int FIND_COMMON_PIC = 501;   //查找图片
}
