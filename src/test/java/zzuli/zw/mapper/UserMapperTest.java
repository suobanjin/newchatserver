package zzuli.zw.mapper;

import org.junit.jupiter.api.Test;
import zzuli.zw.domain.User;
import zzuli.zw.main.aop.AopUtils;

/**
 * @ClassName UserMapperTest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/22 22:51
 * @Version 1.0
 */
public class UserMapperTest {
    UserMapper userMapper = new UserMapperImpl();
    @Test
    public void test01(){
       /* User userHeaderByUsername = userMapper.findUserHeaderByUsername("541813460446");
        System.out.println(userHeaderByUsername);*/
    }

    @Test
    public void test02(){
        /*User detailUserInfo = userMapper.findDetailUserInfo("541813460446");
        System.out.println(detailUserInfo);*/
        UserMapper userMapper = AopUtils.aop(UserMapperImpl.class,UserMapper.class);
        User initUserInfoById = userMapper.findInitUserInfoById(1);
        System.out.println(initUserInfoById);
    }
}
