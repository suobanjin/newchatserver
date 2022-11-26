package zzuli.zw.mapper;

import org.junit.jupiter.api.Test;
import zzuli.zw.dao.UserDao;
import zzuli.zw.dao.daoImpl.UserDaoImpl;

import java.util.List;

/**
 * @ClassName ChatInfoMapperTest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/31 11:18
 * @Version 1.0
 */
public class ChatInfoMapperTest {
    //ChatInfoMapper chatInfoMapper = new ChatInfoMapperImpl();
    UserDao userDao = new UserDaoImpl();
    @Test
    public void test01(){
        System.out.println("--->"+userDao.findDetailUserById(1));
    }
}
