package zzuli.zw.service;

import cn.hutool.crypto.digest.MD5;
import zzuli.zw.dao.UserDao;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.main.annotation.Injection;
import zzuli.zw.main.annotation.Transaction;
import zzuli.zw.pojo.User;
import zzuli.zw.service.interfaces.UserService;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 15日 16:39
 * @Version: 1.0
 */
@Bean("userService")
@Transaction
public class UserServiceImpl implements UserService {
    @Injection(name = "userDao")
    private UserDao userDao;
    @Override
    public User login(User user) {
        User byAccount = userDao.findPassAndIdByAccount(user.getAccount());
        if (byAccount == null)return null;
        if (user.getPassword().equals(byAccount.getPassword())){
            byAccount.setPassword(null);
            return byAccount;
        }
        return null;
    }
    @Override
    @Transaction
    public User findDetailUserInfo(int userId) {
        return userDao.findDetailUserById(userId);
    }

    @Override
    public int updateUserInfoByAccount(User user) {
        if (user == null)return -1;
        return userDao.updateUserInfoByAccount(user);
    }

    @Override
    public int updateUserStatus(int id, int status) {
        return userDao.updateStatusById(id,status);
    }

    @Override
    public int updateUserHead(String imagePath, int id) {
        if (imagePath == null)return -1;
        return userDao.updateUserHeaderById(imagePath,id);
    }

    @Override
    public int findUserStatus(String account) {
        return userDao.findStatusByAccount(account);
    }


}
