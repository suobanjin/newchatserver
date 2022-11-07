package zzuli.zw.service;

import cn.hutool.core.codec.Base64;
import org.apache.commons.io.IOUtils;
import zzuli.zw.domain.User;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.main.annotation.Injection;
import zzuli.zw.mapper.UserMapper;
import zzuli.zw.mapper.UserMapperImpl;
import zzuli.zw.main.aop.AopUtils;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * @ClassName LoginServiceImpl
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/14 22:35
 * @Version 1.0
 */
@Bean("loginService")
public class LoginServiceImpl implements LoginService{
    @Injection(name = "userMapper")
    private UserMapper userMapper;
    @Injection(name = "userService")
    private UserService userService;
    @Override
    public String findHeadPictureByUsername(String username) {
        String userHeaderByUsername = this.userMapper.findUserHeaderByUsername(username);
        if (userHeaderByUsername == null || userHeaderByUsername.length() == 0)return null;
        try {
            byte[] bytes = IOUtils.toByteArray(new FileInputStream(userHeaderByUsername));
            String encode = Base64.encode(bytes);
            return encode;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean login(User user) {
        return userService.login(user.getUsername(),user.getPassword()) >= 1;
    }
}
