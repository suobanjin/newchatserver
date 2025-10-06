package zzuli.zw.dao.daoImpl;

import org.apache.commons.dbutils.handlers.MapListHandler;
import zzuli.zw.dao.FriendDao;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.main.annotation.Repository;
import zzuli.zw.pojo.Friend;
import zzuli.zw.main.utils.TxQueryRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FriendDaoImpl
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 14日 22:19
 * @Version: 1.0
 */
@Repository("friendDao")
public class FriendDaoImpl implements FriendDao {
    TxQueryRunner queryRunner = new TxQueryRunner();
    @Override
    public Friend findFriendByFriendIdAndUserId(int friendId, int userId) {
        return null;
    }

    @Override
    public List<Friend> findFriendsByUserId(int userId) {
        return null;
    }

    @Override
    public List<Friend> findFriendsByGroupId(int groupId) {
        return null;
    }

    @Override
    public Friend findFriendByFriendId(int friendId) {
        return null;
    }

    @Override
    public int deleteFriendById(int userId, int friendId) {
        String sql = "delete from friend_relation where (user_id = ? and friend_id =?)";
        try {
            return queryRunner.update(sql, userId, friendId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<Integer> findFriendIdsByUserId(int userId) {
        String sql = "SELECT f.friend_id FROM friend_relation f WHERE f.user_id = ?";
        try {
            List<Map<String, Object>> query =
                    queryRunner.query(sql, new MapListHandler(), userId);
            List<Integer> list = new ArrayList<>();
            for (Map<String, Object> objectMap : query) {
                list.add((Integer) objectMap.get("friend_id"));
            }
            if (list.size() == 0)return null;
            return list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int updateLikeNumById(int friendId,int num) {
        String sql = "update user set like_num = ? where id = ?";
        try {
            return queryRunner.update(sql, num, friendId);
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }
}
