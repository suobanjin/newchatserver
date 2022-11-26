package zzuli.zw.service;

import zzuli.zw.dao.PhotoWallDao;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.main.annotation.Injection;
import zzuli.zw.pojo.PhotoWall;
import zzuli.zw.service.interfaces.PhotoWallService;

/**
 * @ClassName PhotoWallServiceImpl
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 26日 12:55
 * @Version: 1.0
 */
@Bean("photoWallService")
public class PhotoWallServiceImpl implements PhotoWallService {
    @Injection(name = "photoWallDao")
    private PhotoWallDao photoWallDao;
    @Override
    public int insertOnePhoto(String filePath,int userId) {
        return photoWallDao.insertOnePhoto(filePath, userId);
    }

    @Override
    public PhotoWall findUserPhotoWall(int userId) {
        return photoWallDao.findPhotoWallByUserId(userId);
    }
}
