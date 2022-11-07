package zzuli.zw.main.ioc.interfaces;

public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object bean,String beanName);
    Object postProcessAfterInitialization(Object bean,String beanName);
}
