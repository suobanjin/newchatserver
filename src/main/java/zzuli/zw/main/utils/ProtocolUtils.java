package zzuli.zw.main.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import zzuli.zw.main.factory.ObjectMapperFactory;
import zzuli.zw.main.model.protocol.ResponseMessage;
import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * @ClassName ProtocolUtils
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/9 9:16
 * @Version 1.0
 */
public class ProtocolUtils {
    public synchronized static void send(ResponseMessage responseMessage, Socket socket) throws IOException{
        responseMessage.setSendTime(new Date().getTime());
        responseMessage.setVersion("Server1.0");
        if (responseMessage.getContent() != null){
            responseMessage.setContentLength(responseMessage.getContent().length());
        }
        ObjectMapper objectMapper = ObjectMapperFactory.getInstance();
        String string = objectMapper.writeValueAsString(responseMessage);
        System.out.println("send ---> " + string);
        OutputStream outputStream = socket.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(outputStream)
        );
        bufferedWriter.write(string+"\n");
        bufferedWriter.newLine();
        bufferedWriter.write("EOF\n");
        bufferedWriter.flush();
    }

    public synchronized static ResponseMessage receive(Socket socket) throws IOException {
        ObjectMapper objectMapper = ObjectMapperFactory.getInstance();
        InputStream inputStream = socket.getInputStream();
        if (inputStream.available() == 0 || inputStream.available() == -1)return null;
        StringBuilder stringBuilder = new StringBuilder();
        String result;
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream)
        );
        while (!(result = bufferedReader.readLine()).equals("EOF")) {
            stringBuilder.append(result);
        }
        result = stringBuilder.toString();
        System.out.println("receive ---> " + result);
        return objectMapper.readValue(result, ResponseMessage.class);
    }

    public  ResponseMessage receive2(Socket socket) throws IOException {
        ObjectMapper objectMapper = ObjectMapperFactory.getNewInstance();
        InputStream inputStream = socket.getInputStream();
        if (inputStream.available() == 0 || inputStream.available() == -1)return null;
        StringBuilder stringBuilder = new StringBuilder();
        String result;
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream)
        );
        while (!(result = bufferedReader.readLine()).equals("EOF")) {
            stringBuilder.append(result);
        }
        result = stringBuilder.toString();
        System.out.println("receive ---> " + result);
        return objectMapper.readValue(result, ResponseMessage.class);
    }
}
