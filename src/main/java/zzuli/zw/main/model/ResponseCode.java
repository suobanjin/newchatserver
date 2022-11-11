package zzuli.zw.main.model;

import java.io.Serializable;

/**
 * @author 索半斤
 * @description 返回状态码
 * @date 2022/2/2
 * @className ResponseCode
 */
public class ResponseCode implements Serializable {
    public static final int SERVER_ERROR = 500;  //服务器错误
    public static final int REQUEST_NOT_FOUND = 404; //请求未找到
    public static final int REQUEST_ERROR = 401; //请求格式错误
    public static final int AUTHORITY_ERROR = 403;  //认证失败
    public static final int PARAMETER_ERROR = 402;  //参数错误
    public static final int SUCCESS = 200;         //请求成功
    public static final int FAIL = 501;            //请求失败

    public static final int  ILLEGAL_REQUEST = 502; //非法请求

}
