package zzuli.zw.dao;


import zzuli.zw.pojo.User;

import java.util.List;

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
    * @Description 通过用户的账号查找用户ID和密码，用于登录
    * @Date 21:31 2022/11/7
    * @Param [userId]
    * @return zzuli.zw.pojo.User
    **/
    User findPassAndIdByAccount(String account);

    /**
    * @Author 索半斤
    * @Description 查询详细的用户信息
    * @Date 19:06 2022/11/14
    * @Param [userId]
    * @return zzuli.zw.pojo.User
    **/
    User findDetailUserById(int userId);

    /**
    * @Author 索半斤
    * @Description 通过用户id查询好友的id
    * @Date 19:06 2022/11/14
    * @Param [userId]
    * @return java.util.List<java.lang.Integer>
    **/
    List<Integer> findFriendIdsById(int userId);


    /**
    * @Author 索半斤
    * @Description 更新用户在线状态
    * @Date 21:19 2022/11/14
    * @Param [status, id]
    * @return int
    **/
    int updateUserStatusById(int status,int id);

    /**
    * @Author 索半斤
    * @Description 根据用户id查询用户登录状态
    * @Date 21:19 2022/11/14
    * @Param [userId]
    * @return int
    **/
    int findStatusById(int userId);

    /**
    * @Author 索半斤
    * @Description 通过用户的账号查询用户的在线状态
    * @Date 22:02 2022/11/15
    * @Param [account]
    * @return int
    **/
    int findStatusByAccount(String account);
    /**
    * @Author 索半斤
    * @Description 更新用户信息
    * @Date 21:20 2022/11/14
    * @Param [user]
    * @return int
    **/
    int updateUserInfoById(User user);

    /**
    * @Author 索半斤
    * @Description 根据用户名更新用户信息
    * @Date 18:00 2022/11/15
    * @Param [user]
    * @return int
    **/
    int updateUserInfoByAccount(User user);

    /**
    * @Author 索半斤
    * @Description 根据用户id查询用户的头像
    * @Date 21:20 2022/11/14
    * @Param [userId]
    * @return java.lang.String
    **/
    String findUserHeaderById(int userId);

    /**
    * @Author 索半斤
    * @Description 更新用户的头像
    * @Date 21:21 2022/11/14
    * @Param [imagePath, userId]
    * @return int
    **/
    int updateUserHeaderById(String imagePath,int userId);

    /**
    * @Author 索半斤
    * @Description 修改用户的登录状态
    * @Date 19:23 2022/11/15
    * @Param [id, status]
    * @return int
    **/
    int updateStatusById(int id,int status);

    User findIndexUserInfoById(int userId);

}
