package zzuli.zw.main.ioc;

import zzuli.zw.main.annotation.Injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author 索半斤
 * @description 定义Bean的公共属性
 * @date 2022/2/9
 * @className BeanDefine
 */

public class BeanDefinition {
    private String beanName;
    private String className;
    private Class<?> beanClass;
    private boolean isBpp = false; // 是否是 BeanPostProcessor
    private Class<? extends Annotation> annotationType; // 标识来源注解
    // 构造参数类型列表（自动扫描填充）
    private final List<Class<?>> constructorArgTypes = new ArrayList<>();

    public boolean isBpp() { return isBpp; }
    public void setBpp(boolean bpp) { isBpp = bpp; }
    public BeanDefinition(String id, String className) {
        this.beanName = id;
        this.className = className;
    }

    public BeanDefinition(String id, String className, Class<?> beanClass) {
        this.beanName = id;
        this.className = className;
        this.beanClass = beanClass;
        resolveConstructorArgs();
    }

    public BeanDefinition(String id, String className, Class<?> beanClass, Class<? extends Annotation> annotationType) {
        this.beanName = id;
        this.className = className;
        this.beanClass = beanClass;
        this.annotationType = annotationType;
        resolveConstructorArgs();
    }
    private void resolveConstructorArgs() {
        Constructor<?>[] ctors = beanClass.getDeclaredConstructors();
        if (ctors.length == 1) {
            // 单一构造器，默认走有参注入
            for (Parameter param : ctors[0].getParameters()) {
                constructorArgTypes.add(param.getType());
            }
        } else {
            // 多构造器：优先找带 @Injection 或者参数最多的
            Constructor<?> chosen = null;
            for (Constructor<?> ctor : ctors) {
                if (ctor.isAnnotationPresent(Injection.class)) {
                    chosen = ctor;
                    break;
                }
            }
            if (chosen == null) {
                chosen = Arrays.stream(ctors)
                        .max(Comparator.comparingInt(Constructor::getParameterCount))
                        .orElse(null);
            }
            if (chosen != null) {
                for (Parameter param : chosen.getParameters()) {
                    constructorArgTypes.add(param.getType());
                }
            }
        }
    }
    public List<Class<?>> getConstructorArgTypes() {
        return constructorArgTypes;
    }
    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<? extends Annotation> getAnnotationType() {
        return annotationType;
    }
    public void setAnnotationType(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "beanName='" + beanName + '\'' +
                ", className='" + className + '\'' +
                ", beanClass=" + beanClass +
                ", isBpp=" + isBpp +
                ", annotationType=" + annotationType +
                ", constructorArgTypes=" + constructorArgTypes +
                '}';
    }
}
