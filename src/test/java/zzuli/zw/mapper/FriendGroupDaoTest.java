package zzuli.zw.mapper;

import org.junit.jupiter.api.Test;
import zzuli.zw.dao.daoImpl.FriendGroupDaoImpl;
import zzuli.zw.main.utils.TxQueryRunner;
import zzuli.zw.pojo.FriendGroup;

import java.util.List;

/**
 * @ClassName FriendGroupDaoTest
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 27日 10:20
 * @Version: 1.0
 */
public class FriendGroupDaoTest {
    private FriendGroupDaoImpl friendGroupDao = new FriendGroupDaoImpl();

    @Test
    public void test01(){
        List<FriendGroup> friendGroupsAndFriends = friendGroupDao.findFriendGroupsAndFriends(2);
        System.out.println(friendGroupsAndFriends);
    }
}
