package zzuli.zw.mapper;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import zzuli.zw.domain.Friend;
import zzuli.zw.domain.FriendGroup;
import zzuli.zw.domain.User;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.utils.TxQueryRunner;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * @ClassName UserMapperImpl
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/22 22:49
 * @Version 1.0
 */
@Bean("userMapper")
public class UserMapperImpl implements UserMapper {
    private TxQueryRunner queryRunner = new TxQueryRunner();
    /**
     * @Author 索半斤
     * @Description 根据用户名查找头像
     * @Date 2022/1/18 17:24
     * @Param [username]
     * @return zzuli.zw.domain.User
     **/
    @Override
    public String findUserHeaderByUsername(String username) {
        String sql = "select header_picture from user where username = ?";
        try {
            return queryRunner.query(sql, new BeanHandler<>(String.class), username);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @Author 索半斤
     * @Description 根据id查询用户
     * @Date 2022/1/18 17:24
     * @Param [id]
     * @return zzuli.zw.domain.User
     **/
    @Override
    public String findUserHeaderById(int id) {
        String sql = "select header_picture from user where id = ?";
        try {
            return queryRunner.query(sql, new BeanHandler<>(String.class), id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public int updateUserInfo(User user) {
        String sql = "UPDATE `user` SET nickname = ?,birthday = ?,gender = ?,signature = ?,location = ?,homeland = ?," +
                "profession = ?,phonenumber = ?,email = ? WHERE username = ?";
        Object [] objects = new Object[]{user.getUsername(),user.getBirthday(),user.getGender(),user.getSignature()
        ,user.getLocation(),user.getHomeland(),user.getProfession(),user.getPhoneNumber(),user.getEmail(),user.getUsername()};
        try {
            return queryRunner.update(sql, objects);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * @Author 索半斤
     * @Description 根据用户名更新用户头像
     * @Date 2022/1/18 17:19
     * @Param [username, headerPath]
     * @return int
     **/
    @Override
    public int updateUserHeaderByUsername(String username, String headerPath) {
        String sql = "UPDATE `user` SET header_picture = ? WHERE username = ?";
        Object[] objects = new Object[]{headerPath,username};
        try{
            return queryRunner.update(sql,objects);
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @Author 索半斤
     * @Description 根据id更新用户头像
     * @Date 2022/1/18 17:19
     * @Param [id, headerPath]
     * @return int
     **/
    @Override
    public int updateUserHeaderById(int id, String headerPath) {
        String sql = "UPDATE `user` SET header_picture = ? WHERE id = ?";
        Object[] objects = new Object[]{headerPath,id};
        try{
            return queryRunner.update(sql,objects);
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @Author 索半斤
     * @Description 根据id查找用户
     * @Date 2022/1/18 17:19
     * @Param [id]
     * @return zzuli.zw.domain.User
     **/
    @Override
    public User findUserById(int id) {
        String sql = "SELECT\n" +
                "u.id,u.username,u.nickname,u.birthday,u.email,u.gender,u.header_picture,u.homeland,u.location,\n" +
                "u.phone_number,u.profession,u.signature\n" +
                "FROM\n" +
                "`user` u\n" +
                "WHERE\n" +
                "u.id = ?";
        try {
            User query = queryRunner.query(sql, new BeanHandler<>(User.class), id);
            return query;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @Author 索半斤
     * @Description 根据用户名查找用户
     * @Date 2022/1/18 17:18
     * @Param [username]
     * @return zzuli.zw.domain.User
     **/
    @Override
    public User findUserByUsername(String username) {
        String sql = "SELECT\n" +
                "u.id,u.username,u.nickname,u.birthday,u.email,u.gender,u.header_picture,u.homeland,u.location,\n" +
                "u.phone_number,u.profession,u.signature\n" +
                "FROM\n" +
                "`user` u\n" +
                "WHERE\n" +
                "u.username = ?";
        try {
            User query = queryRunner.query(sql, new BeanHandler<>(User.class), username);
            return query;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @Author 索半斤
     * @Description 根据id删除用户
     * @Date 2022/1/18 17:18
     * @Param [id]
     * @return int
     **/
    @Override
    public int deleteUserById(int id) {
        String sql2 = "DELETE  FROM `user` WHERE id = ?";
        try {
            return queryRunner.update(sql2, id);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @Author 索半斤
     * @Description 根据用户名删除好友
     * @Date 2022/1/18 17:17
     * @Param [username]
     * @return int
     **/
    @Override
    public int deleteUserByUsername(String username) {
        String sql = "DELETE  FROM `user` WHERE username = ?";
        try {
            return queryRunner.update(sql, username);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @Author 索半斤
     * @Description 查询初始化用户信息，包括用户信息和好友列表信息。
     * @Date 2022/1/18 17:16
     * @Param [id]
     * @return zzuli.zw.domain.User
     **/
    @Override
    public User findInitUserInfoById(int id) {
        String sql = "SELECT\n" +
                "u.id user_id,\n" +
                "u.username user_username,\n" +
                "u.header_picture user_headerPicture,\n" +
                "u.signature user_signature,\n" +
                "u.nickname user_nickname,\n" +
                "fl.friend_id friend_id,\n" +
                "fl.remark remark,\n" +
                "u2.signature friend_signature,\n" +
                "u2.username friend_username,\n" +
                "u2.nickname friend_nickname,\n" +
                "fg.group_name group_name,\n" +
                "fg.id friend_group_id, \n" +
                "lf.`status`, \n" +
                "lf.id login_info_id \n"+
                "FROM\n" +
                "`user` u\n" +
                "RIGHT JOIN friendgroup fg ON u.id = fg.user_id\n" +
                "LEFT JOIN friendrelation fl ON fl.fg_id = fg.id\n" +
                "LEFT JOIN `user` u2 ON fl.friend_id = u2.id\n" +
                "LEFT JOIN logininfo lf ON u2.id = lf.`user` \n" +
                "WHERE\n" +
                "u.id = ?";
        try {
            List<Map<String, Object>> query = queryRunner.query(sql, new MapListHandler(), id);
            if (query == null || query.size() == 0)return null;
            User user = new User();
            Map<String, Object> stringObjectMap = query.get(0);
            user.setId((Integer) stringObjectMap.get("user_id"));
            user.setUsername((String) stringObjectMap.get("user_username"));
            user.setUsername((String) stringObjectMap.get("user_nickname"));
            user.setSignature((String) stringObjectMap.get("user_signature"));
            List<Friend> friends = new ArrayList<>();
            query.forEach(key->{
                Friend friend = new Friend();
                friend.setUsername((String) key.get("friend_username"));
                friend.setUserId((Integer) key.get("user_id"));
                friend.setFriendId((Integer) key.get("friend_id"));
                friend.setRemark((String) key.get("remark"));
                friend.setUsername((String) key.get("friend_nickname"));
                friend.setSignature((String) key.get("friend_signature"));
                FriendGroup friendGroup = new FriendGroup();
                //friendGroup.setId((Integer) key.get("friend_group_id"));
                //friendGroup((String) key.get("group_name"));
                friend.setStatus((Integer) key.get("status"));
                friend.setFriendGroup(friendGroup);
                friends.add(friend);
            });
            user.setFriends(friends);
            return user;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @Author 索半斤
     * @Description 根据用户名查找密码
     * @Date 2022/1/17 21:20
     * @Param [username]
     * @return java.lang.String
     **/
    @Override
    public String findUserPasswordByUsername(String username) {
        String sql = "select password from user where username = ?";
        try {
            return queryRunner.query(sql,new BeanHandler<>(String.class),username);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @Author 索半斤
     * @Description 更新用户信息
     * @Date 2022/1/18 17:43
     * @Param [user]
     * @return int
     **/
    @Override
    public int updateUserInfoById(User user) {
        String sql = "UPDATE `user` SET nickname = ?,header_picture=?,birthday=?,gender=?,profession=?,phone_number=?," +
                "location=?,homeland=?,email=?,signature=? WHERE id = ?";
        Object[] objects = new Object[]{user.getUsername(),user.getHeaderPicture(),user.getBirthday(),user.getGender()
        ,user.getProfession(),user.getPhoneNumber(),user.getLocation(),user.getHomeland(),user.getEmail(),user.getSignature(),user.getId()};
        try{
            return queryRunner.update(sql,objects);
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @Author 索半斤
     * @Description 根据用户名更新用户信息
     * @Date 2022/1/18 17:48
     * @Param [user]
     * @return int
     **/
    @Override
    public int updateUserInfoByUsername(User user) {
        String sql = "UPDATE `user` SET nickname = ?,header_picture=?,birthday=?,gender=?,profession=?,phone_number=?," +
                "location=?,homeland=?,email=?,signature=? WHERE username = ?";
        Object[] objects = new Object[]{user.getUsername(),user.getHeaderPicture(),user.getBirthday(),user.getGender()
                ,user.getProfession(),user.getPhoneNumber(),user.getLocation(),user.getHomeland(),user.getEmail(),user.getSignature(),user.getUsername()};
        try{
            return queryRunner.update(sql,objects);
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

}
