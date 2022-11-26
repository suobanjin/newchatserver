package zzuli.zw.main.connection;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import org.apache.commons.lang3.StringUtils;
import zzuli.zw.config.Router;
import zzuli.zw.main.factory.ObjectMapperFactory;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.utils.ProtocolUtils;
import zzuli.zw.main.utils.SocketUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * @ClassName FileDataServerThread
 * @Description 请求文件、图片等操作的线程
 * @Author 索半斤
 * @Date 2021/1/28 11:35
 * @Version 1.0
 */
public class FileDataServerThread implements Runnable {
    private Socket socket;

    public FileDataServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        try {
            //打印客户端信息
            System.out.println(socket.getInetAddress().getHostAddress() + "在文件服务器端进行了.........connected");
            ResponseMessage result = readContent(socket.getInputStream(), 800);
            int request = result.getRequest();
            if (request == Router.FIND_COMMON_PIC) {
                String content = result.getContent();
                content = content.replaceAll("\\\\", "\\\\\\\\");
                content = StringUtils.deleteWhitespace(content);
                content = content.replaceAll("\u202a", "");
                FileInputStream inputStream = new FileInputStream(content);
                byte[] bytes = new byte[1024];
                int len;
                while((len = inputStream.read(bytes)) != -1){
                    socket.getOutputStream().write(bytes,0,len);
                    socket.getOutputStream().flush();
                }
                inputStream.close();
            } else {
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setRequest(request);
                ProtocolUtils.send(responseMessage, socket);
                SocketUtils.closeSocket(socket);
            }
        } catch (Exception e) {
            SocketUtils.closeSocket(socket);
            e.printStackTrace();
        } finally {
            SocketUtils.closeSocket(socket);
        }
    }

    public void readStreamWithRecursion(ByteArrayOutputStream output, InputStream inStream, int timeout) throws Exception {
        long start = System.currentTimeMillis();
        //超时退出
        while (inStream.available() == 0) {
            if ((System.currentTimeMillis() - start) > timeout) {
                throw new SocketTimeoutException("超时读取");
            }
        }
        byte[] buffer = new byte[2048];
        int read = inStream.read(buffer);
        output.write(buffer, 0, read);
        int wait = readWait();
        long startWait = System.currentTimeMillis();
        boolean checkExist = false;
        while (System.currentTimeMillis() - startWait <= wait) {
            int a = inStream.available();
            if (a > 0) {
                checkExist = true;
                break;
            }
        }
        if (checkExist) {
            readStreamWithRecursion(output, inStream, timeout);
        }

    }

    protected int readWait() {
        return 100;
    }

    private byte[] readStream(InputStream inStream, int timeout) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        readStreamWithRecursion(output, inStream,timeout);
        output.close();
        //int size = output.size();
        return output.toByteArray();
    }

    private ResponseMessage readContent(InputStream inStream,int timeout) throws Exception {
        byte[] bytes = readStream(inStream, timeout);
        if (bytes == null || bytes.length == 0)return null;
        return ObjectMapperFactory.getInstance().readValue(bytes,ResponseMessage.class);
    }
}
