package zzuli.zw.service.interfaces;

import zzuli.zw.pojo.PhotoWall;

/**
 * @ClassName PhotoWallService
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 26日 12:55
 * @Version: 1.0
 */
public interface PhotoWallService {
    int insertOnePhoto(String filePath,int userId);

    PhotoWall findUserPhotoWall(int userId);
}
