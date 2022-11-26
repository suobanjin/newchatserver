package zzuli.zw.mapper;

import org.apache.commons.dbutils.handlers.MapListHandler;
import org.junit.jupiter.api.Test;
import zzuli.zw.dao.GroupGroupDao;
import zzuli.zw.dao.daoImpl.GroupGroupDaoImpl;
import zzuli.zw.pojo.Group;
import zzuli.zw.pojo.GroupGroup;
import zzuli.zw.main.utils.TxQueryRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName GroupInfoMapperTest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/26 21:32
 * @Version 1.0
 */
public class GroupInfoMapperTest {
    @Test
    public void test01(){
        /*GroupInfoMapper groupInfoMapper = new GroupInfoMapperImpl();
        List<IndexGroupInfo> infoList = groupInfoMapper.findGroupInfoByUsername("541813460446");
        System.out.println(infoList);
        //infoList.forEach(System.out::println);
        Map<Integer, List<IndexGroupInfo>> map = infoList.parallelStream().collect(Collectors.groupingBy(IndexGroupInfo::getIdentity));
        map.forEach((key,value) -> {
            System.out.println(key);
            System.out.println(value);
        });*/

    }

    @Test
    public void test02(){
        GroupGroupDao groupGroupDao = new GroupGroupDaoImpl();
        List<GroupGroup> groupGroupAndGroupsAndMembers = groupGroupDao.findGroupGroupAndGroupsAndMembers(1);
        System.out.println(groupGroupAndGroupsAndMembers);
    }

    @Test
    public void test03(){
        TxQueryRunner queryRunner = new TxQueryRunner();
        String sql = "SELECT gg.id grouping_id,gg.grouping_name,g.group_name,g.group_avatar,g.group_intro,g.creat_date,g.id group_id\n" +
                "FROM grouping gg LEFT JOIN group_relation gr ON gg.id = gr.grouping_id\n" +
                "LEFT JOIN `group` g ON g.id = gr.group_id\n" +
                "WHERE gg.grouping_leader = ?";
        List<Map<String, Object>> query = null;
        try {
            query = queryRunner.query(sql, new MapListHandler(), 1);
        }catch (Exception e){
            e.printStackTrace();
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
        System.out.println(groupGroupList);
    }
}
