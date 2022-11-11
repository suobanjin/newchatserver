package zzuli.zw.dao;

import zzuli.zw.domain.User;

/**
 * @ClassName IndexDao
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/22 22:48
 * @Version 1.0
 */
public interface IndexDao {
    /**
    * @Author 索半斤
    * @Description 根据用户的id查询当前登录用户的
    * @Date 21:47 2022/11/7
    * @Param [userId]
    * @return zzuli.zw.domain.User
    **/
    User findIndexInfoByUserId(int userId);
}
