package zzuli.zw;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.junit.jupiter.api.Test;

import zzuli.zw.main.utils.JDBCUtils;
import zzuli.zw.pojo.*;
import zzuli.zw.utils.TxQueryRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;

public class Peop {
    int id;

    TxQueryRunner queryRunner = new TxQueryRunner();
    @Test
    public void test01() throws SQLException {
        String sql = "SELECT fr.user_id user_id,u1.`name` username,u1.gender gender,u2.`name` friend_username,u2.id friend_id,u2.gender friend_gender,\n" +
                "g.id group_id,g.name group_name,g.type group_type,fr.friend_remark friend_remark,fr.add_date friend_add_date\n" +
                "FROM\n" +
                "(\n" +
                "(\n" +
                "(\n" +
                "friend_relation fr LEFT JOIN `user` u1 ON fr.user_id = u1.id\n" +
                ") \n" +
                "LEFT JOIN  `user` u2 ON friend_id = u2.id\n" +
                ")\n" +
                "LEFT JOIN grouping g ON g.id = grouping_id\n" +
                ") WHERE fr.user_id = ?";
        List<Map<String, Object>> query = queryRunner.query(sql, new MapListHandler(), 101);
        /*query.get(0).forEach((key,value)->{
            System.out.println("key-->"+key);
            System.out.println("value-->"+value);
        });*/
        int index = 0;
        User user = new User();
        List<FriendGroup> groups = new ArrayList<>();
        for (Map<String, Object> objectMap : query) {
            if (index == 0){
                user.setId((Integer) objectMap.get("user_id"));
                user.setGender((Integer) objectMap.get("gender"));
                user.setUsername((String) objectMap.get("username"));
                index++;
            }
            FriendGroup friendGroup = new FriendGroup();
            friendGroup.setGroupingName((String) objectMap.get("group_name"));
            friendGroup.setGroupingType((Integer) objectMap.get("group_type"));
            friendGroup.setId((Integer) objectMap.get("group_id"));
            if (groups.size() == 0 || !groups.contains(friendGroup))groups.add(friendGroup);
            Friend friend = new Friend();
            friend.setId((Integer) objectMap.get("friend_id"));
            friend.setUsername((String) objectMap.get("friend_username"));
            friend.setRemark((String) objectMap.get("friend_remark"));
            List<Friend> friendList = groups.get(groups.indexOf(friendGroup)).getFriendList();
            if (friendList == null){
                friendList = new ArrayList<>();
                friendList.add(friend);
                friendGroup.setFriendList(friendList);
            }else{
                friendList.add(friend);
            }
        }
        user.setFriendGroups(groups);
    }


    @Test
    public void test02() throws SQLException {
        String sql = "SELECT gg.grouping_name,gg.grouping_leader,gg.id grouping_id,g.group_intro,g.group_leader,g.group_type,g.gruop_name,g.id group_id,u1.account member_account,u1.gender member_gender,u1.header_picture member_header,\n" +
                "u1.id member_id,u1.signature member_signature,u1.`status` member_status,u1.username member_username,\n" +
                "gm.member_name,gm.is_admin,gm.is_owner,fr.friend_remark FROM\n" +
                "grouping gg LEFT JOIN group_relation gr ON gg.id = gr.grouping_id\n" +
                "LEFT JOIN `group` g ON g.id = gr.group_id\n" +
                "LEFT JOIN group_member gm ON gm.group_id = g.id  \n" +
                "LEFT JOIN `user` u1 ON u1.id = gm.member_id\n" +
                "LEFT JOIN friend_relation fr ON fr.friend_id = gm.member_id AND fr.user_id = gg.grouping_leader\n" +
                "WHERE gg.grouping_leader = ?";
        List<Map<String, Object>> query = queryRunner.query(sql, new MapListHandler(),1);
        /*query.get(0).forEach((key,value)->{
            System.out.println("key-->"+key);
            System.out.println("value-->"+value);
        });*/
        List<GroupGroup> groupGroupList = new ArrayList<>();
        //User user = new User();
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
            List<Member> members;
            if (groups.contains(group)){
                members = groups.get(groups.indexOf(group)).getMemberList();
            }else{
                members = new ArrayList<>();
                group.setMemberList(members);
                groups.add(group);
            }
            Member member = new Member();
            member.setId((Integer) objectMap.get("member_id"));
            member.setUsername((String) objectMap.get("member_username"));
            member.setGender((Integer) objectMap.get("member_gender"));
            if (objectMap.get("friend_remark") != null){
                member.setMemberName((String) objectMap.get("friend_remark"));
            }else{
                member.setMemberName((String) objectMap.get("member_username"));
            }
            member.setAdmin((Integer) objectMap.get("is_admin"));
            member.setSignature((String) objectMap.get("member_signature"));
            member.setStatus((Integer) objectMap.get("member_status"));
            member.setAccount("member_account");
            member.setHeaderPicture((String) objectMap.get("member_header"));
            member.setOwner((Integer) objectMap.get("is_owner"));
            if ((int)objectMap.get("is_owner") == 1){
                groups.get(groups.indexOf(group)).setOwner(member);
            }else {
                members.add(member);
            }
        }

        for (GroupGroup groupGroup : groupGroupList) {
            System.out.println(groupGroup);
        }
        //System.out.println(groupGroupList.get(0));
    }
}
