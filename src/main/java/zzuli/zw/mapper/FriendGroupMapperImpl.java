package zzuli.zw.mapper;

import zzuli.zw.main.annotation.Bean;
import zzuli.zw.utils.TxQueryRunner;
import java.sql.SQLException;

/**
 * @author 索半斤
 * @description 好友分组相关操作
 * @date 2022/1/19
 * @className FriendGroupMapperImpl
 */
@Bean("friendGroupMapper")
public class FriendGroupMapperImpl implements FriendGroupMapper{
    private TxQueryRunner queryRunner = new TxQueryRunner();
    /**
     * @Author 索半斤
     * @Description 根据用户id删除用户所有分组
     * @Date 2022/1/19 20:48
     * @Param [userId]
     * @return int
     **/
    @Override
    public int deleteGroupByUserId(int userId) {
        String sql = "DELETE FROM friendgroup WHERE user_id = ?";
        try {
            return queryRunner.update(sql,userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @Author 索半斤
     * @Description 删除指定id的用户分组
     * @Date 2022/1/19 20:49
     * @Param [groupId]
     * @return int
     **/
    @Override
    public int deleteGroupById(int groupId) {
        String sql = "DELETE FROM friendgroup WHERE id = ?";
        try {
            return queryRunner.update(sql,groupId);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
