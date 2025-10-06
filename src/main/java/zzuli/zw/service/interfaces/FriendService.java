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
    /**
    * @Author 索半斤
    * @Description 更新好友的点赞数量
    * @Date 17:29 2022/11/26
    * @Param [friendId, num]
    * @return int
    **/
    int updateLike(int friendId,int num);
}
