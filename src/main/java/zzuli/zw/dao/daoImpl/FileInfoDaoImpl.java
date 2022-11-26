package zzuli.zw.dao.daoImpl;

import org.apache.commons.dbutils.handlers.BeanHandler;
import zzuli.zw.dao.FileInfoDao;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.pojo.FileInfo;
import zzuli.zw.main.utils.TxQueryRunner;

/**
 * @ClassName FileInfoDaoImpl
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 19日 15:50
 * @Version: 1.0
 */
@Bean("fileInfoDao")
public class FileInfoDaoImpl implements FileInfoDao {
    private TxQueryRunner queryRunner = new TxQueryRunner();
    @Override
    public FileInfo findFileInfoByHex(String hex) {
        String sql = "select id,file_path filePath from file_info where file_hex =?";
        try {
            return queryRunner.query(sql, new BeanHandler<>(FileInfo.class), hex);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int insertFileInfo(FileInfo info) {
        String sql = "insert into file_info(file_path,file_hex) values(?,?)";
        try {
            return queryRunner.update(sql, info.getFilePath(), info.getFileHex());
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }
}
