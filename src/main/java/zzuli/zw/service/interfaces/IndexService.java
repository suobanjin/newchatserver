package zzuli.zw.service.interfaces;

import zzuli.zw.pojo.User;

public interface IndexService {
    /**
    * @Author 索半斤
    * @Description 通过用户id查找首页显示的基本信息，包括用户基本信息，群组信息，好友信息等
    * @Date 22:11 2022/11/18
    * @Param [userId]
    * @return zzuli.zw.pojo.User
    **/
    public User findIndexInfo(int userId);
}
