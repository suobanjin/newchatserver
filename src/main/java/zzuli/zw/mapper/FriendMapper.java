package zzuli.zw.mapper;

import zzuli.zw.domain.User;

import java.util.List;

/**
 * @ClassName FriendMapper
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/18 12:03
 * @Version 1.0
 */
public interface FriendMapper {
    /*User findFriendInfoByUsername(String username);
    void updateStatus(String id,int status);
    int findStatus(String id);
    String findFriendRemark(String myQqId,String friendQqId);
    int updateFriendRemark(int friendId,String remark);
    int deleteFriend(String myQqId,String friendQqId);*/
    int deleteFriendRelationByUserId(int id);
    List<Integer> findFriendIdsByUsername(String username);
    List<Integer> findFriendIdsByUserId(Integer userId);
    //Relation findRelationByMyAndFriend(String myQqId,String friendQqId);
}
