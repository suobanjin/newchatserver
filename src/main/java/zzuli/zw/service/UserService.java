package zzuli.zw.service;

import zzuli.zw.domain.User;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/22 22:47
 * @Version 1.0
 */
public interface UserService {
    String findUserHeaderByUsername(String username);
    //User findDetailUserInfo(String username);
    /*int updateUserInfo(User user);
    int updateUserHeader(String username,String headerPath);*/
    int deleteUserByUserId(int userId);
    int login(String username,String password);
    User findUserInfoById(int userId);
}
