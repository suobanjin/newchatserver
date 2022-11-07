package zzuli.zw.mapper;

import org.junit.jupiter.api.Test;
import zzuli.zw.domain.StatusType;
import zzuli.zw.service.FriendService;
import zzuli.zw.service.FriendServiceImpl;
import zzuli.zw.main.aop.AopUtils;

import java.sql.SQLException;

/**
 * @ClassName FriendMapperTest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/17 23:09
 * @Version 1.0
 */
public class FriendMapperTest {
    @Test
    public void test01() throws SQLException {
        /*TxQueryRunner queryRunner = new TxQueryRunner();
        String sql = "SELECT u.username username1,u2.username username2,u.headerpicture hp1," +
                "u2.headerpicture hp2,u.signature sg1," +
                "u2.signature sg2,u.gender gender1,u2.gender gender2,u.nickname nickname1," +
                "u2.nickname nickname2,f.fstatus,f.remark,f.id fid," +
                "r.date group_date,f.date friend_date,r.`name` group_name,r.id rid " +
                "FROM `user` u LEFT JOIN friends f ON u.username = f.mqqid  LEFT JOIN " +
                "`user` u2 ON f.fqqid = u2.username LEFT JOIN relation r ON r.id = f.relation " +
                "WHERE u.username = ?";
        List<Map<String, Object>> query = queryRunner.query(sql, new MapListHandler(), "541813460446");
        User user = new User();
        int index = 0;
        List<Friend> list = new ArrayList<>();
        for (Map<String, Object> map : query) {
            if (index == 0){
                user.setUsername((String) map.get("username1"));
                String userHeaderPicture = (String) map.get("hp1");
                userHeaderPicture = Base64.encode(userHeaderPicture);
                user.setHeaderPicture(userHeaderPicture);
                user.setSignature((String) map.get("sg1"));
                user.setGender((Integer) map.get("gender1"));
                user.setNickname((String) map.get("nickname1"));
                Friend friend = new Friend();
                Relation relation = new Relation();
                friend.setUsername((String) map.get("username2"));
                friend.setId((Integer) map.get("fid"));
                String friendHeaderPicture = (String) map.get("hp2");
                friendHeaderPicture = Base64.encode(friendHeaderPicture);
                friend.setHeaderPicture(friendHeaderPicture);
                friend.setSignature((String) map.get("sg2"));
                friend.setGender((Integer) map.get("gender2"));
                friend.setNickname((String) map.get("nickname2"));
                friend.setStatus((Integer) map.get("fstatus"));
                friend.setRemark((String) map.get("remark"));
                friend.setAddDate((Date) map.get("friend_date"));
                relation.setDate((Date) map.get("group_date"));
                relation.setName((String) map.get("group_name"));
                relation.setId((Integer) map.get("rid"));
                friend.setRelation(relation);
                list.add(friend);
            }else{
                Friend friend = new Friend();
                Relation relation = new Relation();
                friend.setUsername((String) map.get("username2"));
                friend.setId((Integer) map.get("fid"));
                String friendHeaderPicture = (String) map.get("hp2");
                friendHeaderPicture = Base64.encode(friendHeaderPicture);
                friend.setHeaderPicture(friendHeaderPicture);
                friend.setSignature((String) map.get("sg2"));
                friend.setGender((Integer) map.get("gender2"));
                friend.setNickname((String) map.get("nickname2"));
                friend.setStatus((Integer) map.get("fstatus"));
                friend.setRemark((String) map.get("remark"));
                friend.setAddDate((Date) map.get("friend_date"));
                relation.setDate((Date) map.get("group_date"));
                relation.setName((String) map.get("group_name"));
                relation.setId((Integer) map.get("rid"));
                friend.setRelation(relation);
                list.add(friend);
            }
            index++;
        }
        user.setFriends(list);
        List<Friend> friends = user.getFriends();
        Map<String,List<Friend>> collect = friends.parallelStream().collect(Collectors.groupingBy(x-> x.getRelation().getName()));
        collect.forEach((key,value)->{
            System.out.println("key--->"+key);
            System.out.println("value--->"+value);
        });*/
        //System.out.println(user);
    }

    @Test
    public void test02(){
       /* FriendMapper friendMapper = new FriendMapperImpl();
        User info = friendMapper.findFriendInfoByUsername("541813460446");
        List<Friend> friends = info.getFriends();
        friends.forEach(System.out::println);
        //Map<String,List<Friend>> collect = friends.parallelStream().collect(Collectors.groupingBy(x-> x.getRelation().getName()));
        *//*collect.forEach((key,value)->{
            System.out.println("key--->"+key);
            value.forEach(System.out::println);
            if (value.size() == 1){
                Friend friend = value.get(0);
                if (friend.getId() == 0){

                }
            }
        });*/
    }

    @Test
    public void test03(){
        FriendMapper friendMapper = new FriendMapperImpl();
        //int status = friendMapper.findStatus("541813460446");
        //System.out.println(status);
    }

    @Test
    public void test04(){
        FriendMapper friendMapper = new FriendMapperImpl();
        FriendService aop = AopUtils.aop(FriendServiceImpl.class, FriendService.class);
        aop.updateStatus("541813460446", StatusType.ONLINE);
    }

    @Test
    public void test05(){
        FriendMapper friendMapper = new FriendMapperImpl();
        //String remark = friendMapper.findFriendRemark("541813460445", "541813460446");
        //System.out.println(remark);
        FriendService friendService = AopUtils.aop(FriendServiceImpl.class, FriendService.class);
        String remark1 = friendService.findFriendRemark("541813460445", "541813460446");
        System.out.println(remark1);
    }

    @Test
    public void test06(){
        /*FriendMapper friendMapper = new FriendMapperImpl();
        Relation relation = friendMapper.findRelationByMyAndFriend("541813460446", "541813460445");
        System.out.println(relation);*/
    }

}
