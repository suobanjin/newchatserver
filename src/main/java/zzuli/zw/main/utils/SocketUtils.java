package zzuli.zw.main.utils;

import java.io.IOException;
import java.net.Socket;

/**
 * @ClassName SocketUtils
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/7 16:43
 * @Version 1.0
 */
public class SocketUtils {

    public static void closeSocket(Socket socket)  {
        if (socket != null && !socket.isClosed()){
            try {
                socket.shutdownInput();
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


}
