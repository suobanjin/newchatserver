package zzuli.zw;

import org.junit.jupiter.api.Test;
import zzuli.zw.main.ioc.ServerContext;
import zzuli.zw.pojo.Friend;
import zzuli.zw.pojo.User;
import zzuli.zw.request.FriendRequest;

import java.util.*;

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


        Class[] classes = new Class[2];
        classes[1] = User.class;
        classes[0] = Friend.class;
        Class[] classee = new Class[4];
       System.arraycopy(classes,0,classee,0,2);
        System.out.println(classee[0]);
    }

    @Test
    public void test04(){
        Integer last = 3;
        test05(last);
        System.out.println(last);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello");
            }
        },3000,2000);

    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello");
            }
        };
        timer.schedule(timerTask,3000,40000);
        timerTask.cancel();
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                timerTask.run();
            }
        },1000,2000);

    }


    public void test05(Integer x){
        x = 5;


    }
}
