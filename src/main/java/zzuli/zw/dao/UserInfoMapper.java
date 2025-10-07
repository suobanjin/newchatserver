package zzuli.zw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import zzuli.zw.main.annotation.Mapper;
import zzuli.zw.pojo.UserInfo;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

//    //@Select("select * from user_info")
//    public UserInfo selectById(String id);
}
