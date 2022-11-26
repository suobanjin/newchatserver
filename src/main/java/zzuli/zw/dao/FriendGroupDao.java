package zzuli.zw.dao;

import zzuli.zw.pojo.FriendGroup;
import zzuli.zw.pojo.Group;

import java.util.List;

/**
 * @ClassName FriendGroupDao
 * @Description 好友分组相关数据库操作
 * @Author 索半斤
 * @Date 2021/1/22 22:48
 * @Version 1.0
 */
public interface FriendGroupDao {
    /**
    * @Author 索半斤
    * @Description 根据用户id查询该好友的好友分组信息
    * @Date 11:02 2022/11/15
    * @Param [userId]
    * @return java.util.List<zzuli.zw.pojo.FriendGroup>
    **/
    List<FriendGroup> findFriendGroupsByUserId(int userId);

    /**
    * @Author 索半斤
    * @Description 根据用户id查询好友所属分组，以及该分组下的好友信息
    * @Date 11:04 2022/11/15
    * @Param [userId]
    * @return java.util.List<zzuli.zw.pojo.FriendGroup>
    **/
    List<FriendGroup> findFriendGroupsAndFriends(int userId);

    /**
    * @Author 索半斤
    * @Description 新增一个好友分组
    * @Date 11:19 2022/11/15
    * @Param [friendGroup, userId]
    * @return int
    **/
    int insertOneGroup(FriendGroup friendGroup,int userId);

}
