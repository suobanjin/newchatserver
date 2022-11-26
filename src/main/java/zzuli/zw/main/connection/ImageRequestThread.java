package zzuli.zw.main.connection;

import cn.hutool.core.io.IoUtil;
import zzuli.zw.main.utils.SocketUtils;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
/**
 * @ClassName HeaderImageRequestThread
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/23 12:05
 * @Version 1.0
 */
public class ImageRequestThread implements Runnable {
    private Socket socket;
    private String imagePath;

    public ImageRequestThread(Socket socket, String imagePath) {
        this.socket = socket;
        this.imagePath = imagePath.replaceAll("\\\\","/");
    }

    @Override
    public void run() {
        try {
            System.out.println(imagePath);
            FileInputStream inputStream = new FileInputStream(imagePath);
            /*OutputStream outputStream = socket.getOutputStream();
            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }*/
            IoUtil.copy(inputStream,socket.getOutputStream());
            //outputStream.flush();
            inputStream.close();
            SocketUtils.closeSocket(socket);
            System.out.println("jkjkjkjkjkjk");
        } catch (Exception e) {
            SocketUtils.closeSocket(socket);
            e.printStackTrace();
        }
    }
}
