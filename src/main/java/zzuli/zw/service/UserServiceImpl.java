package zzuli.zw.service;

import cn.hutool.crypto.SecureUtil;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.main.annotation.Injection;
import zzuli.zw.main.annotation.Transaction;
import zzuli.zw.domain.User;
import zzuli.zw.mapper.*;

/**
 * @ClassName UserServiceImpl
 * @Description 用户服务
 * @Author 索半斤
 * @Date 2021/1/22 22:47
 * @Version 1.0
 */
@Bean("userService")
@Transaction
public class UserServiceImpl implements UserService{
    @Injection(name = "userMapper")
    private UserMapper userMapper;
    @Injection(name = "messageMapper")
    private MessageMapper messageMapper;
    @Injection(name = "friendMapper")
    private FriendMapper friendMapper;
    @Injection(name = "friendGroupMapper")
    private FriendGroupMapper friendGroupMapper;
    @Injection(name = "loginMapper")
    private LoginInfoMapper loginInfoMapper;
    @Injection(name = "groupInfoMapper")
    private GroupInfoMapper groupInfoMapper;
    @Override
    public String findUserHeaderByUsername(String username) {
        return userMapper.findUserHeaderByUsername(username);
    }

    /**
     * @Author 索半斤
     * @Description 根据用户id删除用户信息
     * @Date 2022/1/20 11:02
     * @Param [userId]
     * @return int
     **/
    @Override
    @Transaction
    public int deleteUserByUserId(int userId) {
        User userById = userMapper.findUserById(userId);
        if (userById == null)return -1;
        int i = loginInfoMapper.deleteLoginInfoByUserId(userId);
        int i1 = friendMapper.deleteFriendRelationByUserId(userId);
        int i2 = friendGroupMapper.deleteGroupByUserId(userId);
        int i3 = messageMapper.deleteMessagesByUserId(userId);
        int i4 = groupInfoMapper.deleteUserInGroupByUserId(userId);
        int i5 = userMapper.deleteUserById(userId);
        return i+i1+i2+i3+i4+i5;
    }

    /**
     * @Author 索半斤
     * @Description 登录校验
     * @Date 2022/1/20 11:03
     * @Param [username, password]
     * @return int
     **/
    @Override
    public int login(String username, String password) {
        System.out.println(userMapper == null);
        String userPasswordByUsername = userMapper.findUserPasswordByUsername(username);
        if (userPasswordByUsername == null)return -1;
        String md5 = SecureUtil.md5(password);
        if (!md5.equals(userPasswordByUsername))return -1;
        return 1;
    }

    @Override
    public User findUserInfoById(int userId) {
        if (userId > Integer.MAX_VALUE || userId < Integer.MIN_VALUE)return null;
        return userMapper.findUserById(userId);
    }

   /* @Override
    public User findDetailUserInfo(String username) {
        return userMapper.findDetailUserInfo(username);
    }

    @Override
    @Transaction
    public int updateUserInfo(User user) {
        return userMapper.updateUserInfo(user);
    }

    @Override
    @Transaction
    public int updateUserHeader(String username, String headerPath) {
        return userMapper.updateUserHeader(username, headerPath);
    }*/
}
