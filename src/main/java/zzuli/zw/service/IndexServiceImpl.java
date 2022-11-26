package zzuli.zw.service;

import zzuli.zw.dao.FriendGroupDao;
import zzuli.zw.dao.GroupGroupDao;
import zzuli.zw.dao.UserDao;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.main.annotation.Injection;
import zzuli.zw.pojo.FriendGroup;
import zzuli.zw.pojo.GroupGroup;
import zzuli.zw.pojo.User;
import zzuli.zw.service.interfaces.IndexService;

import java.util.List;

/**
 * @ClassName IndexServiceImpl
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 18日 22:12
 * @Version: 1.0
 */
@Bean("indexService")
public class IndexServiceImpl implements IndexService {
    @Injection(name = "userDao")
    private UserDao userDao;
    @Injection(name = "friendGroupDao")
    private FriendGroupDao friendGroupDao;
    @Injection(name = "groupGroupDao")
    private GroupGroupDao groupGroupDao;
    @Override
    public User findIndexInfo(int userId) {
        User user = userDao.findIndexUserInfoById(userId);
        List<FriendGroup> friendGroupsAndFriends = friendGroupDao.findFriendGroupsAndFriends(userId);
        List<GroupGroup> groupsAndMembers = groupGroupDao.findGroupGroupAndGroupsAndMembers(userId);
        user.setFriendGroups(friendGroupsAndFriends);
        user.setGroupGroupList(groupsAndMembers);
        return user;
    }
}
