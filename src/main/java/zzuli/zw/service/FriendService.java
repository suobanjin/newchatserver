package zzuli.zw.service;


import zzuli.zw.domain.User;

import java.util.List;

/**
 * @ClassName FriendService
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/18 16:04
 * @Version 1.0
 */
public interface FriendService {
    User findFriendInfoByUsername(String username);
    int findStatus(String id);
    void updateStatus(String id,int status);
    String findFriendRemark(String myQq,String friendQq);
    int updateFriendRemark(int friendId,String remark);
    int deleteFriendInfo(String myQqId,String friendQqId);
    List<Integer> findFriendIdsByUsername(String username);
    List<Integer> findFriendIdsByUserId(Integer userId);
    //Relation findRelation(String myQqId,String friendQqId);
}
