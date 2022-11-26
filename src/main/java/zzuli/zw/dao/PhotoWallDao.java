package zzuli.zw.dao;

import zzuli.zw.pojo.PhotoWall;

import java.util.List;

/**
 * @ClassName PhotoWallDao
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 26日 12:32
 * @Version: 1.0
 */
public interface PhotoWallDao {
    /**
    * @Author 索半斤
    * @Description 插入一条图片
    * @Date 14:20 2022/11/26
    * @Param [filePath, userId]
    * @return int
    **/
    int insertOnePhoto(String filePath,int userId);
    PhotoWall findPhotoWallByUserId(int userId);
}
