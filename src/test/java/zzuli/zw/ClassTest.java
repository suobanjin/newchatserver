package zzuli.zw;

import org.junit.jupiter.api.Test;
import zzuli.zw.main.ioc.ServerContext;
import zzuli.zw.request.FriendRequest;

/**
 * @author 索半斤
 * @description
 * @date 2022/2/9
 * @className ClassTest
 */
public class ClassTest {
    @Test
    public void test01(){
        Class<ClassTest> classTestClass = ClassTest.class;
        System.out.println(classTestClass.getTypeName());
    }

    @Test
    public void test02(){
        ServerContext serverContext = new ServerContext(NewChatServerStart.class);
        FriendRequest friendRequest = (FriendRequest)serverContext.getBean("friendRequest");
        //friendRequest.test();
    }


    @Test
    public void test03(){

    }
}
