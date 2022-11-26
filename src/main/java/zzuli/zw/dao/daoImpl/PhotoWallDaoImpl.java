package zzuli.zw.dao.daoImpl;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import zzuli.zw.dao.PhotoWallDao;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.main.utils.TxQueryRunner;
import zzuli.zw.pojo.PhotoWall;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PhotoWallDaoImpl
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 26日 12:40
 * @Version: 1.0
 */
@Bean("photoWallDao")
public class PhotoWallDaoImpl implements PhotoWallDao {
    private QueryRunner queryRunner = new TxQueryRunner();
    @Override
    public int insertOnePhoto(String filePath,int userId) {
        String sql = "insert into photo_wall(photo_path,user_id) values(?,?)";
        try {
            return queryRunner.update(sql, filePath, userId);
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public PhotoWall findPhotoWallByUserId(int userId) {
        String sql = "select photo_path from photo_wall where userId = ?";
        try {
            List<Map<String, Object>> query = queryRunner.query(sql, new MapListHandler(), userId);
            if (query == null || query.size() == 0)return null;
            PhotoWall photoWall = new PhotoWall();
            List<String> paths = new ArrayList<>();
            for (Map<String, Object> objectMap : query) {
                paths.add((String) objectMap.get("photo_path"));
            }
            photoWall.setFilePath(paths);
            photoWall.setUserId(userId);
            return photoWall;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
