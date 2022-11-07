package zzuli.zw.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import zzuli.zw.config.Router;
import zzuli.zw.domain.User;
import zzuli.zw.main.model.ResponseMessage;
import zzuli.zw.main.factory.ObjectMapperFactory;
import zzuli.zw.main.utils.ProtocolUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 索半斤
 * @description 模拟客户端发送信息
 * @date 2022/2/1
 * @className ConnectionTest
 */
public class ConnectionTest {
    @Test
    public void test01() throws IOException, InterruptedException {
        Socket socket = new Socket("localhost",5539);
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setRequest(Router.LOGIN);
        Map<String,Object> requestMap = new HashMap<>();
        User user = new User();
        user.setId(1);
        user.setUsername("1234567");
        user.setPassword("123456");
        //requestMap.put("ints",1);
        requestMap.put("age",12);
        requestMap.put("user",user);
        String s = ObjectMapperFactory.getInstance().writeValueAsString(requestMap);
        responseMessage.setContentMap(s);
        responseMessage.setContentLength(s.length());

        ProtocolUtils.send(responseMessage,socket);
        Thread.sleep(3000);
        ResponseMessage receive = ProtocolUtils.receive(socket);
        System.out.println(receive);
    }

    @Test
    public void test02() throws JsonProcessingException {
        List<Map<Object,Object>> mapList = new ArrayList<>();
        Map<Object,Object> map1 = new HashMap<>();
        map1.put("1","2");
        Map<Object,Object> map2 = new HashMap<>();
        map1.put("2","1");
        mapList.add(map1);
        mapList.add(map2);
        String s = ObjectMapperFactory.getInstance().writeValueAsString(mapList);
        List<Map<Object,Object>> list = ObjectMapperFactory.getInstance().readValue(s, List.class);
        for (Map<Object, Object> objectObjectMap : list) {
            objectObjectMap.forEach((key,value)->{
                System.out.println("key--->"+key);
                System.out.println("value--->"+value);
            });
        }

    }
}
