package zzuli.zw.dao.daoImpl;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import zzuli.zw.dao.FriendGroupDao;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.pojo.Friend;
import zzuli.zw.pojo.FriendGroup;
import zzuli.zw.main.utils.TxQueryRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FriendGroupDaoImpl
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 15日 11:06
 * @Version: 1.0
 */
@Bean("friendGroupDao")
public class FriendGroupDaoImpl implements FriendGroupDao {
    TxQueryRunner queryRunner = new TxQueryRunner();
    @Override
    public List<FriendGroup> findFriendGroupsByUserId(int userId) {
        String sql = "select id,grouping_name groupingName,grouping_type groupingType from friend_group" +
                "where user_id = ?";
        try {
            return queryRunner.query(sql, new BeanListHandler<>(FriendGroup.class), userId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<FriendGroup> findFriendGroupsAndFriends(int userId) {
        String sql = "SELECT fg.id,grouping_type group_type,fg.id group_id,grouping_name group_name,fg.user_id owner_id,friend_remark,friend_id,account,username,signature,header_picture,`status`\n" +
                "FROM friend_group fg LEFT JOIN friend_relation fr ON fg.id = fr.grouping_id\n" +
                "LEFT JOIN `user` u ON u.id = fr.friend_id\n" +
                "WHERE fg.user_id = ?";
        List<Map<String, Object>> maps;
        try {
            maps = queryRunner.query(sql, new MapListHandler(), userId);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        List<FriendGroup> groups = new ArrayList<>();
        for (Map<String, Object> objectMap : maps) {
            FriendGroup friendGroup = new FriendGroup();
            friendGroup.setGroupingName((String) objectMap.get("group_name"));
            friendGroup.setGroupingType((Integer) objectMap.get("group_type"));
            friendGroup.setId((Integer) objectMap.get("group_id"));
            if (groups.size() == 0 || !groups.contains(friendGroup))groups.add(friendGroup);
            if (objectMap.get("friend_id") == null)continue;
            Friend friend = new Friend();
            friend.setId((Integer) objectMap.get("friend_id"));
            friend.setAccount((String) objectMap.get("account"));
            friend.setUsername((String) objectMap.get("username"));
            friend.setRemark((String) objectMap.get("friend_remark"));
            friend.setHeaderPicture((String) objectMap.get("header_picture"));
            friend.setStatus((Integer) objectMap.get("status"));
            List<Friend> friendList = groups.get(groups.indexOf(friendGroup)).getFriendList();
            if (friendList == null){
                friendList = new ArrayList<>();
                friendList.add(friend);
                friendGroup.setFriendList(friendList);
            }else{
                friendList.add(friend);
            }
        }
        if (groups.size() == 0)return null;
        return groups;
    }

    @Override
    public int insertOneGroup(FriendGroup friendGroup, int userId) {
        String sql = "insert into friend_group(,grouping_name,grouping_type,user_id) values(?,?,?)";
        Object[] objects = new Object[]{friendGroup.getGroupingName(),friendGroup.getGroupingType(),userId};
        try {
            return queryRunner.update(sql, objects);
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }
}
