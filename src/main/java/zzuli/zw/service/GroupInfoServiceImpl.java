package zzuli.zw.service;

import zzuli.zw.mapper.GroupInfoMapper;
import zzuli.zw.mapper.GroupInfoMapperImpl;

import java.util.List;

/**
 * @ClassName GroupInfoServiceImpl
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/28 21:34
 * @Version 1.0
 */
public class GroupInfoServiceImpl implements GroupInfoService{
    private GroupInfoMapper groupInfoMapper = new GroupInfoMapperImpl();
   /* @Override
    public List<IndexGroupInfo> findIndexGroupInfo(String username) {
        return groupInfoMapper.findGroupInfoByUsername(username);
    }*/
}
