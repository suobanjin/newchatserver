package zzuli.zw.service;

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
public class HeaderImageRequestThread implements Runnable {
    private Socket socket;
    private String imagePath;

    public HeaderImageRequestThread(Socket socket, String imagePath) {
        this.socket = socket;
        this.imagePath = imagePath;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = new FileInputStream(imagePath);
            OutputStream outputStream = socket.getOutputStream();
            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            outputStream.flush();
            SocketUtils.closeSocket(socket);
        } catch (Exception e) {
            SocketUtils.closeSocket(socket);
            e.printStackTrace();
        }
    }
}
