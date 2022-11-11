package zzuli.zw.dao;


import zzuli.zw.pojo.User;

/**
 * @ClassName UserDao
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/22 22:48
 * @Version 1.0
 */
public interface UserDao {
    /**
    * @Author 索半斤
    * @Description 通过用户的id查找用户信息
    * @Date 21:31 2022/11/7
    * @Param [userId]
    * @return zzuli.zw.pojo.User
    **/
    User findUserById(int userId);

    /**
    * @Author 索半斤
    * @Description 根据用户名查找到用户的密码，用来进行登录校验
    * @Date 21:44 2022/11/7
    * @Param [username]
    * @return zzuli.zw.pojo.User
    **/
    User findPasswordByUsername(String username);




}
