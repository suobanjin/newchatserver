package zzuli.zw.main.aop;
import zzuli.zw.main.factory.ObjectFactory;
import zzuli.zw.main.aop.proxy.TransactionHandler;
import java.lang.reflect.Proxy;

/**
 * @ClassName AopUtils
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/21 19:47
 * @Version 1.0
 */
public class AopUtils {

    @SuppressWarnings("unchecked")
    public synchronized static  <T,R>  R aop(Class<T> implementsClass,Class<R> InterfaceClass) {
        T instance = ObjectFactory.getInstance(implementsClass);
        TransactionHandler transactionHandler = new TransactionHandler(instance);
        assert instance != null;
        ClassLoader classLoader = instance.getClass().getClassLoader();
        Class<?>[] interfaces = instance.getClass().getInterfaces();
        return (R) Proxy.newProxyInstance(classLoader, interfaces, transactionHandler);
    }

    @SuppressWarnings("unchecked")
    public synchronized static  <T,R>  R aop(Class<T> implementsClass) {
        T instance = ObjectFactory.getInstance(implementsClass);
        TransactionHandler transactionHandler = new TransactionHandler(instance);
        assert instance != null;
        ClassLoader classLoader = instance.getClass().getClassLoader();
        Class<?>[] interfaces = instance.getClass().getInterfaces();
        return (R) Proxy.newProxyInstance(classLoader, interfaces, transactionHandler);
    }
    public synchronized static  <T,R>  R aop(Object obj) {
        TransactionHandler transactionHandler = new TransactionHandler(obj);
        ClassLoader classLoader = obj.getClass().getClassLoader();
        Class<?>[] interfaces = obj.getClass().getInterfaces();
        return (R) Proxy.newProxyInstance(classLoader, interfaces, transactionHandler);
    }

}
