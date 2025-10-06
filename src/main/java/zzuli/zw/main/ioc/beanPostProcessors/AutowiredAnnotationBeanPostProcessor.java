package zzuli.zw.main.ioc.beanPostProcessors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import zzuli.zw.main.annotation.Injection;
import zzuli.zw.main.annotation.Qualifier;
import zzuli.zw.main.ioc.BeanFactory;
import zzuli.zw.main.ioc.interfaces.InstantiationAwareBeanPostProcessor;

import java.lang.reflect.Field;


/**
 * @author 索半斤
 * @description AutowiredAnnotationBeanPostProcessor
 * @date 2025/10/01
 */
@Slf4j
public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName, BeanFactory factory) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName, BeanFactory factory) {
        return bean;
    }

    @Override
    public Object postProcessProperties(Object bean, String beanName, BeanFactory beanFactory) {
        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Injection.class)) {
                // 优先检查 Qualifier
                String depName = null;
                Qualifier qualifier = field.getAnnotation(Qualifier.class);
                if (qualifier != null) {
                    depName = qualifier.value();
                }

                Object dependency;
                if (StringUtils.isNotEmpty(depName)) {
                    // 按名字找
                    dependency = beanFactory.getBean(depName);
                } else {
                    // 按类型找
                    dependency = beanFactory.getBean(field.getType());
                }

                try {
                    field.setAccessible(true);
                    field.set(bean, dependency);
                    log.info("[Autowired] Injected dependency: {} into {}",
                            depName != null ? depName : field.getType().getSimpleName(),
                            beanName);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject dependency: " + field.getName(), e);
                }
            }
        }
        return bean;
    }
}
