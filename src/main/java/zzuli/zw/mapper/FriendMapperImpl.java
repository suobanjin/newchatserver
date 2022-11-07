package zzuli.zw.mapper;

import cn.hutool.core.codec.Base64;
import org.apache.commons.dbutils.handlers.*;
import zzuli.zw.domain.Friend;
import zzuli.zw.domain.User;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.utils.TxQueryRunner;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.*;

/**
 * @ClassName FriendMapperImpl
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/18 12:04
 * @Version 1.0
 */
@Bean("friendMapper")
public class FriendMapperImpl implements FriendMapper{
    private TxQueryRunner queryRunner = new TxQueryRunner();

    @Override
    public int deleteFriendRelationByUserId(int id) {
        String sql = "DELETE FROM friendrelation WHERE user_id = ? OR friend_id = ?";
        try {
            Object[] objects = new Object[]{id,id};
            return queryRunner.update(sql,objects);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public List<Integer> findFriendIdsByUsername(String username) {
        String sql =
                "SELECT" +
                " f.friend_id" +
                "FROM friendrelation f " +
                "LEFT JOIN " +
                "`user` u " +
                "ON " +
                "u.id = f.user_id " +
                "WHERE " +
                "u.username = ?";
        try {
            List<Integer> friendIds = queryRunner.query(sql, new BeanListHandler<>(Integer.class));
            return friendIds;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Integer> findFriendIdsByUserId(Integer userId) {
        String sql =
                "SELECT " +
                "f.friend_id " +
                "FROM friendrelation f " +
                "WHERE " +
                "f.user_id = ?";
        try {
            List<Integer> query = queryRunner.query(sql, new BeanListHandler<>(Integer.class));
            return query;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*@Override
    public User findFriendInfoByUsername(String username) {
        String sql = "SELECT u.username username1,u2.username username2,u.headerpicture hp1," +
                " u.signature sg1, u2.signature sg2,u.gender gender1,u2.gender gender2,u.nickname nickname1," +
                "u2.nickname nickname2,f.fstatus,f.remark,f.id fid,u2.headerpicture hp2," +
                "r.date group_date,f.date friend_date,r.`name` group_name,r.id rid  FROM relation r " +
                "LEFT JOIN friends f " +
                "ON r.qq = f.mqqid AND r.id = f.relation " +
                "LEFT JOIN `user` u2 " +
                "ON f.fqqid = u2.username " +
                "LEFT JOIN `user` u ON r.qq = u.username " +
                "WHERE r.qq = ?";
        try {
            List<Map<String, Object>> query = queryRunner.query(sql, new MapListHandler(), username);
            User user = new User();
            int index = 0;
            List<Friend> list = new ArrayList<>();
            for (Map<String, Object> map : query) {
                if (index == 0) {
                    user.setUsername((String) map.get("username1"));
                    String userHeaderPicture = (String) map.get("hp1");
                    FileInputStream inputStream = new FileInputStream(userHeaderPicture);
                    userHeaderPicture = Base64.encode(inputStream);
                    inputStream.close();
                    user.setHeaderPicture(userHeaderPicture);
                    user.setSignature((String) map.get("sg1"));
                    user.setGender((Integer) map.get("gender1"));
                    user.setNickname((String) map.get("nickname1"));
                }
                //setFriendInfo(list, map);
                index++;
            }
            user.setFriends(list);
            return user;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }*/

   /* @Override
    public void updateStatus(String id, int status) {
        String sql = "update friends set fstatus = ? where fqqid = ?";
        try {
            queryRunner.execute(sql, status,id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int findStatus(String id) {
        String sql = "SELECT fstatus FROM friends WHERE fqqid = ? LIMIT 1;";
        try {
            return queryRunner.query(sql, new ScalarHandler<>(), id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public String findFriendRemark(String myQqId, String friendQqId) {
        String sql = "SELECT remark,id FROM friends WHERE " +
                "mqqid = ? AND fqqid = ?";
        try {
            Map<String, Object> map = queryRunner.query(sql, new MapHandler(), myQqId, friendQqId);
            if (map == null || map.size() == 0)return null;
            Object remark = map.get("remark");
            if (remark != null) return (String) remark;
            Object id = map.get("id");
            if (id == null) return null;
            return "remarkIsNull";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int updateFriendRemark(int friendId, String remark) {
        String sql = "update friends set remark = ? where id = ?";
        try {
            return queryRunner.update(sql, remark,friendId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int deleteFriend(String myQqId, String friendQqId) {
        String sql = "delete from friends where mqqid = ? and fqqid = ?";
        try {
            return queryRunner.update(sql, myQqId,friendQqId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }*/

   /* @Override
    public Relation findRelationByMyAndFriend(String myQqId, String friendQqId) {
        String sql = "SELECT r.`name`,r.id FROM friends f LEFT JOIN relation r ON f.relation = r.id " +
                "WHERE f.mqqid = ? and f.fqqid = ?";
        try {
            return queryRunner.query(sql, new BeanHandler<>(Relation.class), myQqId,friendQqId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setFriendInfo(List<Friend> list,Map<String,Object> map){
        Friend friend = new Friend();
        Relation relation = new Relation();
        boolean flag = false;
        if (map.get("username2") != null){
            friend.setUsername((String) map.get("username2"));
        }
        if (map.get("fid") != null) {
            friend.setId((Integer) map.get("fid"));
        }
        if (map.get("sg2") != null) {
            friend.setSignature((String) map.get("sg2"));
        }
        if (map.get("gender2") != null) {
            friend.setGender((Integer) map.get("gender2"));
        }
        if (map.get("nickname2") != null) {
            friend.setNickname((String) map.get("nickname2"));
        }
        if (map.get("fstatus") != null) {
            friend.setStatus((Integer) map.get("fstatus"));
        }
        if (map.get("remark") != null) {
            friend.setRemark((String) map.get("remark"));
        }
        *//*if (map.get("friend_date") != null) {
            friend.setAddDate((Date) map.get("friend_date"));
        }
        if (map.get("hp2") != null){
            friend.setHeaderPicture((String) map.get("hp2"));
        }
        if (map.get("group_date") != null) {
            relation.setDate((Date) map.get("group_date"));
            flag = true;
        }
        if (map.get("group_name") != null) {
            relation.setName((String) map.get("group_name"));
            flag = true;
        }
        if (map.get("rid") != null) {
            relation.setId((Integer) map.get("rid"));
            flag = true;
        }
        if (flag) {
            friend.setRelation(relation);
            list.add(friend);
        }*//*
    }*/
}
