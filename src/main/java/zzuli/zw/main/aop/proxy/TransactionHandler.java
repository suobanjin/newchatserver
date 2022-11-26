package zzuli.zw.main.aop.proxy;

import zzuli.zw.main.annotation.Transaction;
import zzuli.zw.main.utils.JDBCUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @ClassName TransactionHandler
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/21 21:05
 * @Version 1.0
 */
public class TransactionHandler implements InvocationHandler {
    private Object object;
    public TransactionHandler(Object object){
        this.object = object;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Transaction annotation = method.getAnnotation(Transaction.class);
        Object o = null;
        if (annotation == null){
            o = method.invoke(object, args);
        }else{
            try{
                JDBCUtils.beginTransaction();
                o = method.invoke(object, args);
                JDBCUtils.commitTransaction();
            }catch (Exception e){
                JDBCUtils.rollbackTransaction();
            }
        }
        return o;
    }
}
