package zzuli.zw.dao.daoImpl;

import org.apache.commons.dbutils.handlers.MapListHandler;
import zzuli.zw.dao.GroupGroupDao;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.pojo.Group;
import zzuli.zw.pojo.GroupGroup;
import zzuli.zw.main.utils.TxQueryRunner;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName GroupGroupDaoImpl
 * @Description: 群分组操作
 * @Author 索半斤
 * @Datetime 2022年 11月 15日 11:35
 * @Version: 1.0
 */
@Bean("groupGroupDao")
public class GroupGroupDaoImpl implements GroupGroupDao {
    TxQueryRunner queryRunner = new TxQueryRunner();
    @Override
    public List<GroupGroup> findGroupGroupAndGroupsAndMembers(int userId) {
        String sql = "SELECT gg.id grouping_id,gg.grouping_name,g.group_name,g.group_avatar,g.group_intro,g.creat_date,g.id group_id\n" +
                "FROM grouping gg LEFT JOIN group_relation gr ON gg.id = gr.grouping_id\n" +
                "LEFT JOIN `group` g ON g.id = gr.group_id\n" +
                "WHERE gg.grouping_leader = ?";
        List<Map<String, Object>> query;
        try {
            query = queryRunner.query(sql, new MapListHandler(), 1);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        List<GroupGroup> groupGroupList = new ArrayList<>();
        for (Map<String, Object> objectMap : query) {
            GroupGroup groupGroup = new GroupGroup();
            groupGroup.setGroupGroupName((String) objectMap.get("grouping_name"));
            groupGroup.setId((Integer) objectMap.get("grouping_id"));
            List<Group> groups;
            if (groupGroupList.contains(groupGroup)){
                groups = groupGroupList.get(groupGroupList.indexOf(groupGroup)).getGroupList();
            }else{
                groupGroupList.add(groupGroup);
                groups = new ArrayList<>();
                groupGroup.setGroupList(groups);
            }
            if (objectMap.get("group_id") == null)continue;
            Group group = new Group();
            group.setGroupAvatar((String) objectMap.get("group_avatar"));
            group.setId((Integer) objectMap.get("group_id"));
            group.setGroupName((String) objectMap.get("group_name"));
            group.setGroupIntro((String) objectMap.get("group_intro"));
            group.setCreateDate((Date) objectMap.get("creat_date"));
            if (!groups.contains(group)){
                groups.add(group);
            }
        }
        if (groupGroupList.size() == 0)return null;
        return groupGroupList;
    }
}
