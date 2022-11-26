package zzuli.zw.service.interfaces;

import zzuli.zw.pojo.User;

public interface UserService {
    User login(User user);
    User findDetailUserInfo(int userId);
    int updateUserInfoByAccount(User user);
    int updateUserStatus(int id,int status);
    int updateUserHead(String imagePath,int id);
    int findUserStatus(String account);
}
