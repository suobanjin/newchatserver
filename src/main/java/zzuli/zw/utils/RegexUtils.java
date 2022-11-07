package zzuli.zw.utils;

/**
 * @ClassName RegexUtils
 * @Description 正则校验工具类
 * @Author 索半斤
 * @Date 2021/1/6 11:32
 * @Version 1.0
 */
public class RegexUtils {
    private static final String REGEX_USERNAME = "^[1-9][0-9]{6,12}"; //正则匹配用户名，规则为不包含特殊特殊字符
    private static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,20}$";
    private static final String REGEX_MOBILE = "^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$";
    private static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    private static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
    private static final String REGEX_IP_ADDR = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";
    /**
     * @Date: 2021/1/6 11:35
     * @Author 索半斤
     * @Description: 正则匹配，匹配规则为
     * @MethodName: regexUsername
     */
    public static boolean regexUsername(String username){
        return username.matches(REGEX_USERNAME);
    }

    /**
     * @Date: 2021/1/6 17:28
     * @Author 索半斤
     * @Description: 正则校验密码
     * @MethodName: regexPassword
     */
    public static boolean regexPassword(String password){
        return password.matches(REGEX_PASSWORD);
    }

    /**
     * @Date: 2021/1/6 17:28
     * @Author 索半斤
     * @Description: 正则校验手机号
     * @MethodName: regexMobile
     */
    public static boolean regexMobile(String phoneNumber){
        return phoneNumber.matches(REGEX_MOBILE);
    }

    /**
     * @Date: 2021/1/6 17:28
     * @Author 索半斤
     * @Description: 正则校验邮箱
     * @MethodName: regexEmail
     */
    public static boolean regexEmail(String email){
        return email.matches(REGEX_EMAIL);
    }

    /**
     * @Date: 2021/1/6 17:29
     * @Author 索半斤
     * @Description: 正则校验url地址
     * @MethodName: regexUrl
     */
    public static boolean regexUrl(String url){
        return url.matches(REGEX_URL);
    }

    /**
     * @Date: 2021/1/6 17:29
     * @Author 索半斤
     * @Description: 正则校验Ip地址
     * @MethodName: regexIp
     */
    public static boolean regexIp(String ip){
        return ip.matches(REGEX_IP_ADDR);
    }
}
