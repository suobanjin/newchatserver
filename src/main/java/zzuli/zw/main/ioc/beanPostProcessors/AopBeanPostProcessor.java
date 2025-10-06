package zzuli.zw.main.ioc.beanPostProcessors;

import lombok.extern.slf4j.Slf4j;
import zzuli.zw.main.annotation.Transaction;
import zzuli.zw.main.aop.AopUtils;
import zzuli.zw.main.ioc.BeanFactory;
import zzuli.zw.main.ioc.interfaces.BeanPostProcessor;
import zzuli.zw.main.ioc.interfaces.SmartInstantiationAwareBeanPostProcessor;

import java.lang.reflect.Proxy;

/**
 * @author 索半斤
 * @description AOP BeanPostProcessor
 * @date 2025/10/01
 * @className AopBeanPostProcessor.java
 */
@Slf4j
public class AopBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName, BeanFactory factory) {
        // 仅在需要时创建代理；如果已经是代理则直接返回，避免重复代理
        if (bean.getClass().isAnnotationPresent(Transaction.class)) {
            if (Proxy.isProxyClass(bean.getClass())) return bean;
            return AopUtils.aop(bean); // 你的代理工具（返回 JDK proxy 或原 bean）
        }
        return bean;
    }
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName, BeanFactory factory) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName, BeanFactory factory) {
        if (bean.getClass().isAnnotationPresent(Transaction.class)) {
            if (Proxy.isProxyClass(bean.getClass())) return bean;
            return AopUtils.aop(bean);
        }
        return bean;
    }
}

