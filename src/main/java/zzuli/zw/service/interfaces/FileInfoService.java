package zzuli.zw.service.interfaces;

import zzuli.zw.pojo.FileInfo;


public interface FileInfoService {
    FileInfo findFileInfo(String hex);
    int insertOneFileInfo(FileInfo fileInfo);
}
