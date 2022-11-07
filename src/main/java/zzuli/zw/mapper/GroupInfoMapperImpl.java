package zzuli.zw.mapper;

import org.apache.commons.dbutils.handlers.MapListHandler;
import zzuli.zw.domain.GroupInfo;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.utils.TxQueryRunner;
import java.sql.SQLException;
import java.util.*;

/**
 * @ClassName GroupInfoMapperImpl
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/26 20:09
 * @Version 1.0
 */
@Bean("groupInfoMapper")
public class GroupInfoMapperImpl implements GroupInfoMapper{
    private TxQueryRunner queryRunner = new TxQueryRunner();
    @Override
    public List<GroupInfo> findGroupInfoByUsername(String username) {
        return null;
    }

    /**
     * @Author 索半斤
     * @Description 删除群组中的用户
     * @Date 2022/1/19 21:44
     * @Param [userId]
     * @return int
     **/
    @Override
    public int deleteUserInGroupByUserId(int userId) {
        String sql = "DELETE FROM groupuser WHERE user_id = ?";
        try {
            return queryRunner.update(sql,userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    /*@Override
    public List<IndexGroupInfo> findGroupInfoByUsername(String username) {
        String sql = "SELECT gl.id gid,gl.identity,gf.`name`,gf.createdate," +
                "gf.introduction,gf.grouppicture,gf.createdate,u.nickname " +
                "FROM grouprelation gl LEFT JOIN groupinfo gf ON gl.groupid = gf.id " +
                "LEFT JOIN `user` u ON u.username = gf.creator WHERE gl.qqid = ?";
        try {
            List<Map<String, Object>> query = queryRunner.query(sql, new MapListHandler(), username);
            List<IndexGroupInfo> list = new ArrayList<>();
            for (Map<String, Object> map : query) {
                IndexGroupInfo groupInfo = new IndexGroupInfo();
                groupInfo.setGroupId((Integer) map.get("gid"));
                groupInfo.setCreateDate((Date) map.get("createdate"));
                groupInfo.setGroupCreator((String) map.get("nickname"));
                groupInfo.setGroupHeadPicture((String) map.get("grouppicture"));
                groupInfo.setGroupName((String) map.get("name"));
                groupInfo.setIdentity((Integer) map.get("identity"));
                groupInfo.setIntroduction((String) map.get("introduction"));
                list.add(groupInfo);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
