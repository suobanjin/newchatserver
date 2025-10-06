package zzuli.zw.main.ioc.interfaces;

import zzuli.zw.main.ioc.BeanDefinition;

public interface ObjectFactory {
    default void registerBeanDefinition(String objectName,Class<?> clazz){};

    void registerBeanDefinition(BeanDefinition beanDefinition);

    Object getBean(String objectName);
    Object getBean(Class<?> objectType);
    void release();
    void registerBeanPostProcessor(BeanPostProcessor processor);
}
