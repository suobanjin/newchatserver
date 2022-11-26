package zzuli.zw.service;

import zzuli.zw.dao.FileInfoDao;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.main.annotation.Injection;
import zzuli.zw.pojo.FileInfo;
import zzuli.zw.service.interfaces.FileInfoService;

/**
 * @ClassName FileInfoServiceImpl
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 19日 16:17
 * @Version: 1.0
 */
@Bean("fileInfoService")
public class FileInfoServiceImpl implements FileInfoService {
    @Injection(name = "fileInfoDao")
    private FileInfoDao fileInfoDao;
    @Override
    public FileInfo findFileInfo(String hex) {
        if (hex == null || hex.length() == 0)return null;
        return fileInfoDao.findFileInfoByHex(hex);
    }

    @Override
    public int insertOneFileInfo(FileInfo fileInfo) {
        if (fileInfo == null)return -1;
        return fileInfoDao.insertFileInfo(fileInfo);
    }
}
