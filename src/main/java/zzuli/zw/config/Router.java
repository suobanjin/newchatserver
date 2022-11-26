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
    public static final int HEART_LISTENER = 103;   //心跳包
    public static final int FIND_USER_STATUS = 105; //查询用户的在线状态
    public static final int INIT_INDEX_INFO = 106; //初始化首页信息
    public static final int UPDATE_USER_STATUS = 107; //更新当前用户的登录状态
    public static final int FIND_DETAIL_USER_INFO = 108; //查询用户的详细信息
    public static final int UPDATE_USER_HEADER = 109; //更新用户的头像
    public static final int UPDATE_USER_INFO = 110; //更新用户信息
    public static final int UPLOAD_USER_PHOTO = 111; //用户上传图片
    public static final int UPDATE_FRIEND_INFO = 201;   //当好友更新状态时，更新用户的最新状态（在线状态、用户名、昵称、个性签名等内容）
    public static final int UPDATE_FRIEND_REMARK = 202; //修改用户的好友的备注
    public static final int DELETE_FRIEND = 204; //按照好友ID删除好友，设定为双向删除
    public static final int UPDATE_FRIEND_IMAGE = 205; //更新好友头像
    public static final int FIND_DETAIL_FRIEND_INFO = 206; //
    public static final int UPDATE_FRIEND_STATUS = 207;  //更新好友状态
    public static final int UPDATE_FRIEND_LIKE = 208; //更新点赞数量
    public static final int UPDATE_GROUP_INFO = 301; //更新群组的信息
    public static final int CLOSE_SOCKET = 401;         //关闭连接
    public static final int FIND_COMMON_PIC = 501;   //查找图片
    public static final int  ILLEGAL_REQUEST = 601; //非法请求



}
