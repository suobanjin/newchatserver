package zzuli.zw.utils;

import cn.hutool.core.codec.Base64;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @ClassName FileUtil
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 25日 22:23
 * @Version: 1.0
 */
public class FileUtil {
    /**
    * @Author 索半斤
    * @Description 从文件中读取数据并压缩，转换为压缩后的byte数组
    * @Date 22:36 2022/11/25
    * @Param [file]
    * @return byte[]
    **/
    public static byte[] getGzipBytesFromFile(File file) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[2048];
        int len;
        while ((len = fileInputStream.read(bytes)) != -1) {
            gzipOutputStream.write(bytes, 0, len);
            gzipOutputStream.flush();
        }
        gzipOutputStream.close();
        fileInputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    /**
    * @Author 索半斤
    * @Description 从文件中压缩数据并转换为Base64字符串
    * @Date 22:36 2022/11/25
    * @Param [file]
    * @return java.lang.String
    **/
    public static String getGzipBase64FromFile(File file) throws Exception {
        byte[] gzipBytesFromFile = getGzipBytesFromFile(file);
        return Base64.encode(gzipBytesFromFile);
    }

    /**
    * @Author 索半斤
    * @Description 将Base64字符串转换为byte数组，并使用GZIP流解压byte数组
    * @Date 22:37 2022/11/25
    * @Param [base64]
    * @return byte[]
    **/
    public static byte[] getUnZipBytesFromBase64(String base64) throws IOException {
        if (base64 == null || base64.length() == 0)return null;
        byte[] decode = Base64.decode(base64);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decode);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        int len;
        byte[] bytes = new byte[2048];
        while ((len = gzipInputStream.read(bytes)) != -1){
            byteArrayOutputStream.write(bytes,0,len);
            byteArrayOutputStream.flush();
        }
        gzipInputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
}
