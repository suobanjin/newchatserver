package zzuli.zw.dao;

import zzuli.zw.pojo.FileInfo;

public interface FileInfoDao {
    /**
    * @Author 索半斤
    * @Description 根据文件Hex查找文件路径
    * @Date 15:48 2022/11/19
    * @Param [hex]
    * @return zzuli.zw.pojo.FileInfo
    **/
    FileInfo findFileInfoByHex(String hex);

    /**
    * @Author 索半斤
    * @Description 插入文件信息
    * @Date 15:49 2022/11/19
    * @Param [info]
    * @return int
    **/
    int insertFileInfo(FileInfo info);
}
