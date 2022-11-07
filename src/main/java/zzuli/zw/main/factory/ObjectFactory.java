package zzuli.zw.main.factory;

/**
 * @ClassName ObjectFactory
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/21 21:39
 * @Version 1.0
 */
public class ObjectFactory {

    public static <T> T getInstance(Class<T> clazz){
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
