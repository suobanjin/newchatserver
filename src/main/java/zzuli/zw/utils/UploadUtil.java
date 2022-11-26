package zzuli.zw.utils;

import cn.hutool.core.lang.UUID;
import zzuli.zw.pojo.model.FilePath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName UploadUtil
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 25日 22:55
 * @Version: 1.0
 */
public class UploadUtil {

    public static String uploadToDefaultPath(byte[] bytes,String fileName) throws IOException {
        //获取服务器默认配置的上传路径
        String parentPath = FilePath.HEAD_IMAGE_PREFIX;
        ////获取当前日期的字符串形式作为子路径文件夹
        String uploadDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String childrenPath = "\\" + uploadDate;
        File defaultFile = new File(parentPath, childrenPath);
        //如果文件夹不存在则先进行创建
        defaultFile.mkdirs();
        //分割文件名获取文件后缀
        String[] split = fileName.split("\\.");
        //拼接文件路径，规则为服务器默认上传路径+上传日期+UUID+文件格式
        String s = UUID.randomUUID(true).toString();
        String filePath = defaultFile + "\\" + s + "." + split[1];
        //获取输出流
        FileOutputStream outputStream = new FileOutputStream(filePath);
        //将文件写入本地
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
        return filePath;
    }

    public static String uploadToFilePath(byte[] bytes,String filePath) throws IOException {
        //获取输出流
        FileOutputStream outputStream = new FileOutputStream(filePath);
        //将文件写入本地
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
        return filePath;
    }
}
