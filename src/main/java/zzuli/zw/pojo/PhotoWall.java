package zzuli.zw.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PhotoWall
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 26日 12:45
 * @Version: 1.0
 */
public class PhotoWall implements Serializable {
    private int id;
    private int userId;
    private List<String> filePaths;

    @Override
    public String toString() {
        return "PhotoWall{" +
                "id=" + id +
                ", userId=" + userId +
                ", filePath='" + filePaths + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    public void setFilePath(List<String> filePaths) {
        this.filePaths = filePaths;
    }
}
