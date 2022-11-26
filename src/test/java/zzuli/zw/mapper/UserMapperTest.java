package zzuli.zw.mapper;

import cn.hutool.core.io.IoUtil;
import org.junit.jupiter.api.Test;
import zzuli.zw.dao.FileInfoDao;
import zzuli.zw.dao.UserDao;
import zzuli.zw.dao.daoImpl.FileInfoDaoImpl;
import zzuli.zw.dao.daoImpl.UserDaoImpl;
import zzuli.zw.main.factory.ObjectMapperFactory;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.pojo.FileInfo;
import zzuli.zw.pojo.User;
import zzuli.zw.main.utils.TxQueryRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @ClassName UserMapperTest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/22 22:51
 * @Version 1.0
 */
public class UserMapperTest {
    //UserMapper userMapper = new UserMapperImpl();
    private TxQueryRunner queryRunner = new TxQueryRunner();
    @Test
    public void test01(){
       /* User userHeaderByUsername = userMapper.findUserHeaderByUsername("541813460446");
        System.out.println(userHeaderByUsername);*/
    }

    @Test
    public void test02(){
        /*User detailUserInfo = userMapper.findDetailUserInfo("541813460446");
        System.out.println(detailUserInfo);*/
        /*UserMapper userMapper = AopUtils.aop(UserMapperImpl.class,UserMapper.class);
        User initUserInfoById = userMapper.findInitUserInfoById(1);
        System.out.println(initUserInfoById);*/
    }

    @Test
    public void test03(){
        UserDao userDao = new UserDaoImpl();
        User user = userDao.findDetailUserById(1);
        System.out.println(user);
        String userHeaderById = userDao.findUserHeaderById(1);
        System.out.println(userHeaderById);
    }

    @Test
    public void test04(){
        FileInfoDao fileInfoDao = new FileInfoDaoImpl();
        FileInfo fileInfoByHex = fileInfoDao.findFileInfoByHex("123456");
        System.out.println(fileInfoByHex);
    }

    @Test
    public void test05() throws IOException {
        UserDao userDao = new UserDaoImpl();
        String userHeaderById = userDao.findUserHeaderById(1);
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setContent(userHeaderById);
        String s = ObjectMapperFactory.getInstance().writeValueAsString(responseMessage);

        ResponseMessage responseMessage1 = ObjectMapperFactory.getInstance().readValue(s, ResponseMessage.class);
        String imagePath = "C:/image/2.jpg";
        for (int i = 0; i < 9; i++) {
            int finalI = i;
            Thread t = new Thread(()->{
                try {
                    FileInputStream inputStream = new FileInputStream(responseMessage1.getContent());
                    IoUtil.copy(inputStream,new FileOutputStream("444"+ finalI +".jpg"));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            t.start();
        }
    }
}
