package zzuli.zw.dao.daoImpl;

import org.apache.commons.dbutils.handlers.*;
import zzuli.zw.dao.UserDao;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.pojo.User;
import zzuli.zw.main.utils.TxQueryRunner;
import java.sql.SQLException;
import java.util.*;

/**
 * @ClassName UserDaoImpl
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 14日 18:40
 * @Version: 1.0
 */
@Bean("userDao")
public class UserDaoImpl implements UserDao {
    private final TxQueryRunner queryRunner = new TxQueryRunner();
    @Override
    public User findPassAndIdByAccount(String account) {
        String sql = "select id,account,password from user where account=?";
        try {
            return queryRunner.query(sql, new BeanHandler<>(User.class), account);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User findDetailUserById(int userId) {
        String sql = "select id,account,username,gender,birthday,signature,profession,location,homeland," +
                "phone_number phoneNumber,email,header_picture headerPicture,status from user where id = ?";
        try {
            return queryRunner.query(sql,new BeanHandler<>(User.class),userId);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Integer> findFriendIdsById(int userId) {
        String sql = "select friend_id from friend_relation where user_id = ?";
        try {
            List<Map<String, Object>> query = queryRunner.query(sql, new MapListHandler(), userId);
            List<Integer> list = new ArrayList<>();
            for (Map<String, Object> objectMap : query) {
                list.add((Integer) objectMap.get("friend_id"));
            }
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int updateUserStatusById(int status, int id) {
        String sql = "update user set status =? where id = ?";
        try {
            Object o = queryRunner.update(sql, status, id);
            if (o == null)return -1;
            return (int)o;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int findStatusById(int userId) {
        String sql = "select status from user where id = ?";
        try {
            Object o = queryRunner.query(sql, new ScalarHandler<>(), userId);
            if (o == null)return -1;
            return (int)o;
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int findStatusByAccount(String account) {
        String sql = "select status from user where account = ?";
        try {
            Object query = queryRunner.query(sql, new ScalarHandler<>(), account);
            if (query == null)return -1;
            return (int) query;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int updateUserInfoById(User user) {
        String sql = "update user set username = ?,gender=?,birthday=?,signature=?,profession=?," +
                "location=?,homeland=?,phone_number=?,email=? where id = ?";
        Object objects = new Object[]{user.getUsername(),user.getGender(),user.getBirthday(),user.getSignature(),
        user.getProfession(),user.getLocation(),user.getHomeland(),user.getPhoneNumber(),user.getEmail(),user.getId()};
        try {
            Object query = queryRunner.update(sql, objects);
            if (query == null)return -1;
            return (int)query;
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int updateUserInfoByAccount(User user) {
        String sql = "update user set username = ?,gender=?,birthday=?,signature=?,profession=?," +
                "location=?,homeland=?,phone_number=?,email=? where account = ?";
        Object objects = new Object[]{user.getUsername(),user.getGender(),user.getBirthday(),user.getSignature(),
                user.getProfession(),user.getLocation(),user.getHomeland(),user.getPhoneNumber(),user.getEmail(),user.getAccount()};
        try {
            Object query = queryRunner.update(sql, objects);
            if (query == null)return -1;
            return (int)query;
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public String findUserHeaderById(int userId) {
        String sql = "select header_picture from user where id = ?";
        try {
            return queryRunner.query(sql,new ScalarHandler<>(),userId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int updateUserHeaderById(String imagePath, int userId) {
        String sql = "update user set header_picture = ? where id = ?";
        try{
            Object query = queryRunner.update(sql,imagePath,userId);
            if (query == null)return -1;
            return (int)query;
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int updateStatusById(int id, int status) {
        String sql = "update user set status = ? where id = ?";
        try {
            Object o = queryRunner.update(sql, status, id);
            if (o == null)return -1;
            return (int)o;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public User findIndexUserInfoById(int userId) {
        String sql = "select id,account,username,signature,status,header_picture headerPicture from user where id =?";
        try {
            return queryRunner.query(sql, new BeanHandler<>(User.class), userId);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
