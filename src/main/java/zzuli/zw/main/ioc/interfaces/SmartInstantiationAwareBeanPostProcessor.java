package zzuli.zw.main.ioc.interfaces;

import zzuli.zw.main.ioc.BeanFactory;

public interface SmartInstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    /**
     * 返回“早期引用”——通常是对原始 bean 的代理（如果需要）。
     * 默认实现直接返回原始 bean。
     */
    default Object getEarlyBeanReference(Object bean, String beanName, BeanFactory factory) {
        return bean;
    }
}
