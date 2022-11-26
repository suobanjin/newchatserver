package zzuli.zw.service;

import zzuli.zw.dao.FriendDao;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.main.annotation.Injection;
import zzuli.zw.service.interfaces.FriendService;

import java.util.List;

/**
 * @ClassName FriendServiceImpl
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 15日 19:09
 * @Version: 1.0
 */
@Bean("friendService")
public class FriendServiceImpl implements FriendService {
    @Injection(name = "friendDao")
    private FriendDao friendDao;
    @Override
    public List<Integer> findFriendIds(int userId) {
        return friendDao.findFriendIdsByUserId(userId);
    }
}
