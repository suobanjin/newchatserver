package zzuli.zw.pojo;

import java.io.Serializable;

/**
 * @ClassName FileInfo
 * @Description: 存储用户上传的文件的信息
 * @Author 索半斤
 * @Datetime 2022年 11月 19日 15:36
 * @Version: 1.0
 */
public class FileInfo implements Serializable {
    private int id;  //用户id
    private String filePath;  //文件路径
    private String fileHex;   //文件Hex

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", fileHex='" + fileHex + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileHex() {
        return fileHex;
    }

    public void setFileHex(String fileHex) {
        this.fileHex = fileHex;
    }
}
