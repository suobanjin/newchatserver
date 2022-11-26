package zzuli.zw.utils;

import org.junit.jupiter.api.Test;
import zzuli.zw.main.aop.AopUtils;

/**
 * @ClassName AopUtilsTest
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/21 19:58
 * @Version 1.0
 */
public class AopUtilsTest {

    @Test
    public void test01(){
       /* LoginServiceImpl loginService = new LoginServiceImpl();
        ClassLoader classLoader = loginService.getClass().getClassLoader();
        Class[] interfaces = loginService.getClass().getInterfaces();
        LogHandler logHandler = new LogHandler(loginService);
        LoginService aop = (LoginService) Proxy.newProxyInstance(classLoader, interfaces, logHandler);
        aop.login(new User());*/
        /*LoginService aop = AopUtils.aop(LoginServiceImpl.class, LoginService.class);
        aop.login(new User());*/
    }

    @Test
    public void test02(){
        Integer a = 126;
        Integer b = 126;
        System.out.println(a == b);
    }
}
