package zzuli.zw.mapper;

import zzuli.zw.domain.User;

/**
 * @ClassName UserMapper
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/22 22:48
 * @Version 1.0
 */
public interface UserMapper {
    String findUserHeaderByUsername(String username);
    String findUserHeaderById(int id);
    int updateUserInfo(User user);
    int updateUserHeaderByUsername(String username,String headerPath);
    int updateUserHeaderById(int id,String headerPath);
    User findUserById(int id);
    User findUserByUsername(String username);
    int deleteUserById(int id);
    int deleteUserByUsername(String username);
    User findInitUserInfoById(int id);
    String findUserPasswordByUsername(String username);
    int updateUserInfoById(User user);
    int updateUserInfoByUsername(User user);
}
