package zzuli.zw.main.ioc;

/**
 * @author 索半斤
 * @description 定义Bean的公共属性
 * @date 2022/2/9
 * @className BeanDefine
 */

public class BeanDefinition {
    private String id;
    private String className;
    private Class<?> beanClass;

    public BeanDefinition(String id, String className) {
        this.id = id;
        this.className = className;
    }

    public BeanDefinition(String id, String className, Class<?> beanClass) {
        this.id = id;
        this.className = className;
        this.beanClass = beanClass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
