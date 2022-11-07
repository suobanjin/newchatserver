package zzuli.zw.service;

import zzuli.zw.main.annotation.Transaction;
import zzuli.zw.domain.User;
import zzuli.zw.mapper.FriendMapper;
import zzuli.zw.mapper.FriendMapperImpl;

import java.util.List;

/**
 * @ClassName FriendServiceImpl
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/18 16:05
 * @Version 1.0
 */
public class FriendServiceImpl implements FriendService{
    private FriendMapper friendMapper = new FriendMapperImpl();
    @Override
    public User findFriendInfoByUsername(String username) {
        //return friendMapper.findFriendInfoByUsername(username);
        return null;
    }

    @Override
    public int findStatus(String id) {
        //return friendMapper.findStatus(id);
        return -1;
    }

    @Override
    @Transaction
    public void updateStatus(String id, int status) {
        //friendMapper.updateStatus(id, status);
    }

    @Override
    public String findFriendRemark(String myQq, String friendQq) {
        //return friendMapper.findFriendRemark(myQq, friendQq);
        return null;
    }

    @Override
    @Transaction
    public int updateFriendRemark(int friendId, String remark) {
        //return friendMapper.updateFriendRemark(friendId, remark);
        return -1;
    }

    @Override
    @Transaction
    public int deleteFriendInfo(String myQqId, String friendQqId) {
        //return friendMapper.deleteFriend(myQqId, friendQqId);
        return -1;
    }

    @Override
    public List<Integer> findFriendIdsByUsername(String username) {
        if (username == null || username.length() == 0)return null;
        return friendMapper.findFriendIdsByUsername(username);
    }

    @Override
    public List<Integer> findFriendIdsByUserId(Integer userId) {
        if (userId < Integer.MIN_VALUE || userId > Integer.MAX_VALUE)return null;
        return friendMapper.findFriendIdsByUserId(userId);
    }

    /*@Override
    public Relation findRelation(String myQqId, String friendQqId) {
        return friendMapper.findRelationByMyAndFriend(myQqId, friendQqId);
    }*/

}
