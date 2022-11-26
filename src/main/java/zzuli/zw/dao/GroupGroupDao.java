package zzuli.zw.dao;

import zzuli.zw.pojo.GroupGroup;
import java.util.List;

public interface GroupGroupDao {
    /**
    * @Author 索半斤
    * @Description 根据用户id查找用户的群分组以及群信息和
    * @Date 11:31 2022/11/15
    * @Param [userId]
    * @return java.util.List<zzuli.zw.pojo.GroupGroup>
    **/
    List<GroupGroup> findGroupGroupAndGroupsAndMembers(int userId);
}
