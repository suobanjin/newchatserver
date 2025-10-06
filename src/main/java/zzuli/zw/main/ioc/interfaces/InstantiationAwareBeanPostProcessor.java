package zzuli.zw.main.ioc.interfaces;

import zzuli.zw.main.ioc.BeanFactory;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    /**
     * 在实例化之后、属性注入之前调用；返回false表示后续不继续属性填充
     */
    default boolean postProcessAfterInstantiation(Object bean, String beanName, BeanFactory factory) {
        return true;
    }

    /**
     * 实现属性注入逻辑
     * @param bean
     * @param beanName
     * @param beanFactory
     * @return
     */
    Object postProcessProperties(Object bean, String beanName, BeanFactory beanFactory);
}
