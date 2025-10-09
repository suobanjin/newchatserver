package zzuli.zw.main.ioc.beanPostProcessors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import zzuli.zw.main.annotation.Value;
import zzuli.zw.main.ioc.BeanFactory;
import zzuli.zw.main.ioc.interfaces.InstantiationAwareBeanPostProcessor;
import zzuli.zw.main.utils.PropertiesUtils;

import java.lang.reflect.Field;
import java.util.Properties;

@Slf4j
public class ValueAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    private final Properties properties = new Properties();

    public ValueAnnotationBeanPostProcessor(Properties props) {
        this.properties.putAll(props);
    }

    @Override
    public Object postProcessProperties(Object bean, String beanName, BeanFactory beanFactory) {
        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Value.class)) {
                String rawVal = field.getAnnotation(Value.class).value();
                // 支持占位符解析 ${...}
                String resolvedVal = PropertiesUtils.resolvePlaceholderValue(properties, rawVal);
                try {
                    field.setAccessible(true);
                    Object convertedValue = PropertiesUtils.convertValue(field.getType(), resolvedVal);
                    field.set(bean, convertedValue);
                    log.info("[Value] 注入属性 {} = {} 到 {}", field.getName(), resolvedVal, beanName);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName, BeanFactory factory) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName, BeanFactory factory) {
        return bean;
    }
}

