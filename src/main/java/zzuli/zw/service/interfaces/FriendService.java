package zzuli.zw.service.interfaces;

import java.util.List;

public interface FriendService {
    /**
    * @Author 索半斤
    * @Description 查找好友id列表
    * @Date 12:31 2022/11/26
    * @Param [userId]
    * @return java.util.List<java.lang.Integer>
    **/
    List<Integer> findFriendIds(int userId);
}
