package zzuli.zw.main.ioc.interfaces;

import zzuli.zw.main.ioc.BeanFactory;

public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object bean, String beanName, BeanFactory factory);
    Object postProcessAfterInitialization(Object bean,String beanName, BeanFactory factory);
}
