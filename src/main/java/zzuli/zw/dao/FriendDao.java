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
    * @Description 根据好友的id以及用户的id查找多个好友的信息，查到的好友信息和上面的类似
    * @Date 21:40 2022/11/7
    * @Param [friendIds, userId]
    * @return java.util.List<zzuli.zw.pojo.Friend>
    **/
    List<Friend> findFriendsByFriendIdsAndUserId(int[] friendIds,int userId);

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
    * @Description 和findUserByUserId方法作用差不多，只是做语义上的区分
    * @Date 21:36 2022/11/7
    * @Param [friendId]
    * @return zzuli.zw.pojo.Friend
    **/
    Friend findFriendByFriendId(int friendId);
}
