package zzuli.zw.main.ioc;

import zzuli.zw.main.annotation.Configuration;

import java.lang.reflect.Method;

public class IOCMethodBeanDefinition extends BeanDefinition {
    private final Class<?> configClass;
    private final Method factoryMethod;
    private final BeanDefinition parentBeanDefinition;

    public IOCMethodBeanDefinition(String id, String className, Class<?> beanClass,
                                   Class<?> configClass, Method factoryMethod, BeanDefinition parentBeanDefinition) {
        super(id, className, beanClass, Configuration.class);
        this.configClass = configClass;
        this.factoryMethod = factoryMethod;
        this.parentBeanDefinition = parentBeanDefinition;
    }

    public BeanDefinition getParentBeanDefinition() {
        return parentBeanDefinition;
    }

    public Class<?> getConfigClass() {
        return configClass;
    }

    public Method getFactoryMethod() {
        return factoryMethod;
    }
}
