package zzuli.zw.main.ioc.interfaces;

public interface ObjectFactory {
    void init(String objectName,Object object);
    Object getBean(String objectName);
    Object getBean(Class<?> objectType);
    void release();
}
