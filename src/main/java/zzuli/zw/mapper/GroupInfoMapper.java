package zzuli.zw.mapper;

import zzuli.zw.domain.GroupInfo;
import java.util.List;

/**
 * @ClassName GroupInfoMapper
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/26 20:00
 * @Version 1.0
 */
public interface GroupInfoMapper {
    //List<IndexGroupInfo> findGroupInfoByUsername(String username);
    List<GroupInfo> findGroupInfoByUsername(String username);
    int deleteUserInGroupByUserId(int userId);
}
