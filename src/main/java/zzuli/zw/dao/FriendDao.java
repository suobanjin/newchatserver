package zzuli.zw.dao;

import zzuli.zw.pojo.Friend;

import java.util.List;
/**
 * @ClassName FriendDao
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/22 22:48
 * @Version 1.0
 */
public interface FriendDao {
    /**
    * @Author 索半斤
    * @Description 通过好友id以及用户id的组合查找到好友信息，相较于findUser，该方法可以查找到好友备注信息
    * @Date 21:35 2022/11/7
    * @Param [friendId, userId]
    * @return zzuli.zw.pojo.Friend
    **/
    Friend findFriendByFriendIdAndUserId(int friendId,int userId);


    /**
    * @Author 索半斤
    * @Description 根据用户Id查找用户的所有好友
    * @Date 21:42 2022/11/7
    * @Param [userId]
    * @return java.util.List<zzuli.zw.pojo.Friend>
    **/
    List<Friend> findFriendsByUserId(int userId);

    /**
    * @Author 索半斤
    * @Description 根据群分组id查询该分组下的好友
    * @Date 11:00 2022/11/15
    * @Param [groupId]
    * @return java.util.List<zzuli.zw.pojo.Friend>
    **/
    List<Friend> findFriendsByGroupId(int groupId);

    /**
    * @Author 索半斤
    * @Description 和findUserByUserId方法作用差不多，只是做语义上的区分
    * @Date 21:36 2022/11/7
    * @Param [friendId]
    * @return zzuli.zw.pojo.Friend
    **/
    Friend findFriendByFriendId(int friendId);

    /**
    * @Author 索半斤
    * @Description 删除用户的好友
    * @Date 22:18 2022/11/14
    * @Param [userId, friendId]
    * @return int
    **/
    int deleteFriendById(int userId,int friendId);

    /**
    * @Author 索半斤
    * @Description 根据用户id查询用户所有好友的id
    * @Date 18:27 2022/11/15
    * @Param [userId]
    * @return java.util.List<java.lang.Integer>
    **/
    List<Integer> findFriendIdsByUserId(int userId);
}
