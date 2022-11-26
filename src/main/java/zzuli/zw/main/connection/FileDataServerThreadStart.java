package zzuli.zw.main.connection;

import cn.hutool.core.thread.ThreadUtil;
import zzuli.zw.main.connection.FileDataServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 负责服务端和客户端通信
 *
 * @author 索半斤
 */
public class FileDataServerThreadStart {
    private static final int FILE_SERVER_PORT = 2088;
    public FileDataServerThreadStart() throws IOException {
        ServerSocket fileServer = null;
        try {
            fileServer = new ServerSocket(FILE_SERVER_PORT);
            while (!fileServer.isClosed()) {
                Socket socket = fileServer.accept();
                FileDataServerThread fileDataServerThread = new FileDataServerThread(socket);
                ThreadUtil.execute(fileDataServerThread);
            }
        } catch (IOException e) {
            if (fileServer != null) {
                fileServer.close();
            }
            e.printStackTrace();
        }
    }
}