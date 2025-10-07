package zzuli.zw.main.ioc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import zzuli.zw.main.annotation.Qualifier;
import zzuli.zw.main.annotation.Value;
import zzuli.zw.main.ioc.interfaces.*;
import zzuli.zw.main.utils.PropertiesUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author 索半斤
 * @description 对象工厂
 * @date 2022/2/9
 * @className BeanFactory
 */
@Slf4j
public class BeanFactory implements ObjectFactory {

    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();      // 一级缓存
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(); // 二级缓存
    private final Map<String, SingletonFactory> singletonFactories = new ConcurrentHashMap<>(); // 三级缓存
    private final Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
    private final List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>(); // BeanPostProcessor
    private final Map<Class<?>, String> classToBeanNameMap = new ConcurrentHashMap<>();    // beanClass -> Class
    private final Properties properties = new Properties();
    interface SingletonFactory {
        Object getObject();
    }

    public Map<String, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }

    public void registerProperties(Properties props) {
        this.properties.putAll(props);
    }
    @Override
    public void registerBeanPostProcessor(BeanPostProcessor processor) {
        beanPostProcessors.add(processor);
    }



    @Override
    public void release() {
        if (!singletonObjects.isEmpty()) singletonObjects.clear();
        if (!earlySingletonObjects.isEmpty()) earlySingletonObjects.clear();
        if (!singletonFactories.isEmpty()) singletonFactories.clear();
        if (!beanDefinitions.isEmpty()) beanDefinitions.clear();
        if (!classToBeanNameMap.isEmpty()) classToBeanNameMap.clear();
        if (!beanPostProcessors.isEmpty()) beanPostProcessors.clear();
        if (!properties.isEmpty()) properties.clear();
    }


    public List<Object> getBeans(){
        return new ArrayList<>(singletonObjects.values());
    }

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {

        beanDefinitions.put(beanDefinition.getBeanName(), beanDefinition);
        classToBeanNameMap.put(beanDefinition.getBeanClass(), beanDefinition.getBeanName());
    }

    public void registerBean(String beanName, Object  bean) {
        this.singletonObjects.put(beanName, bean);
        Class<?> beanClass = bean.getClass();
        Class<?>[] interfaces = beanClass.getInterfaces();
        if (interfaces != null && interfaces.length > 0) {
            for (Class<?> itf : interfaces) {
                classToBeanNameMap.putIfAbsent(itf, beanName);
            }
        } else {
            classToBeanNameMap.putIfAbsent(beanClass, beanName);
        }
    }

    @Override
    public Object getBean(String beanName) {
        // 1. 一级缓存
        if (singletonObjects.containsKey(beanName)) return singletonObjects.get(beanName);
        // 2. 二级缓存
        if (earlySingletonObjects.containsKey(beanName)) return earlySingletonObjects.get(beanName);
        // 3. 三级缓存
        if (singletonFactories.containsKey(beanName)) {
            Object earlyRef = singletonFactories.get(beanName).getObject();
            earlySingletonObjects.put(beanName, earlyRef);
            singletonFactories.remove(beanName);
            return earlyRef;
        }
        BeanDefinition beanDefinition = beanDefinitions.get(beanName);
        if (Objects.isNull(beanDefinition)) throw new RuntimeException("[bean init...] No bean definition for " + beanName);
        return createBean(beanName, beanDefinition);
    }


    @Override
    public Object getBean(Class<?> clazz) {
        List<String> candidateBeanNames = new ArrayList<>();

        // 1. 直接匹配（classToBeanNameMap 的直接注册）
        if (classToBeanNameMap.containsKey(clazz)) {
            return getBean(classToBeanNameMap.get(clazz));
        }

        // 2. 接口 / 父类查找
        for (Map.Entry<Class<?>, String> entry : classToBeanNameMap.entrySet()) {
            Class<?> registeredClass = entry.getKey();
            if (clazz.isAssignableFrom(registeredClass)) {
                candidateBeanNames.add(entry.getValue());
            }
        }

        // 3. 没找到
        if (candidateBeanNames.isEmpty()) {
            throw new RuntimeException("No bean of type " + clazz);
        }

        // 4. 多实现类冲突
        if (candidateBeanNames.size() > 1) {
            throw new RuntimeException("Multiple beans found for type " + clazz +
                    ": " + candidateBeanNames + ", please use @Qualifier or bean name.");
        }

        // 5. 只剩一个
        String beanName = candidateBeanNames.stream().findFirst().get();
        return getBean(beanName);
    }

    // BeanFactory 的 createBean
    private Object createBean(String beanName, BeanDefinition bd) {

        Object bean;

        try {
            // 1. 实例化（解耦）
            bean = instantiateBean(bd);

            final Object finalBean = bean;

            // 2. 暴露三级缓存（用于解决循环依赖）
            singletonFactories.put(beanName, () -> {
                Object proxy = finalBean;
                for (BeanPostProcessor bpp : beanPostProcessors) {
                    if (bpp instanceof SmartInstantiationAwareBeanPostProcessor) {
                        proxy = ((SmartInstantiationAwareBeanPostProcessor) bpp)
                                .getEarlyBeanReference(proxy, beanName, this);
                    }
                }
                return proxy;
            });

            // 3. 属性注入
            for (BeanPostProcessor bpp : beanPostProcessors) {
                if (bpp instanceof InstantiationAwareBeanPostProcessor) {
                    bean = ((InstantiationAwareBeanPostProcessor) bpp)
                            .postProcessProperties(bean, beanName, this);
                }
            }

            // 4. 初始化前
            for (BeanPostProcessor bpp : beanPostProcessors) {
                bean = bpp.postProcessBeforeInitialization(bean, beanName, this);
            }

            // 5. 初始化
            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
            }

            // 6. 初始化后
            Object exposedObject = bean;
            for (BeanPostProcessor bpp : beanPostProcessors) {
                exposedObject = bpp.postProcessAfterInitialization(exposedObject, beanName, this);
            }

            // 7. 放入一级缓存
            singletonObjects.put(beanName, exposedObject);
            earlySingletonObjects.remove(beanName);
            singletonFactories.remove(beanName);

            return exposedObject;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bean: " + beanName, e);
        }
    }

    private Object instantiateBean(BeanDefinition bd) throws Exception {
        if (bd instanceof IOCMethodBeanDefinition) {
            // ========= 1. 工厂方法 =========
            IOCMethodBeanDefinition fbd = (IOCMethodBeanDefinition) bd;
            Object factoryBean = getBean(fbd.getParentBeanDefinition().getBeanName());
            Method factoryMethod = fbd.getFactoryMethod();

            List<Object> args = resolveMethodArguments(factoryMethod);
            return factoryMethod.invoke(factoryBean, args.toArray());
        }

        // ========= 2. 有参构造 =========
        if (!CollectionUtils.isEmpty(bd.getConstructorArgTypes())) {
            Constructor<?> actor = bd.getBeanClass().getConstructor(bd.getConstructorArgTypes().toArray(new Class[0]));
            Parameter[] parameters = actor.getParameters();

            List<Object> args = new ArrayList<>();

            for (Parameter parameter : parameters) {
                Value valueAnno = parameter.getAnnotation(Value.class);
                if (valueAnno != null) {
                    // 解析占位符
                    String rawValue = valueAnno.value();
                    String resolvedValue = PropertiesUtils.resolvePlaceholderValue(properties, rawValue);
                    // 类型转换
                    Object convertedValue = PropertiesUtils.convertValue(parameter.getType(), resolvedValue);
                    log.info("参数注入 @Value({}) -> {}", rawValue, convertedValue);
                    args.add(convertedValue);
                    continue;
                }
                // 处理 @Qualifier
                Qualifier qualifierAnno = parameter.getAnnotation(Qualifier.class);
                if (qualifierAnno != null) {
                    Object dep = getBean(qualifierAnno.value());
                    args.add(dep);
                    continue;
                }
                if (isSimpleType(parameter.getType())) {
                    // 简单类型给默认值（比如 int=0, boolean=false）
                    args.add(getDefaultValue(parameter.getType()));
                } else {
                    // 引用类型走依赖注入
                    args.add(getBean(parameter.getType()));
                }
            }

            return actor.newInstance(args.toArray());
        }

        // ========= 3. 默认构造 =========
        Constructor<?> defaultCtr = bd.getBeanClass().getDeclaredConstructor();
        if (!defaultCtr.isAccessible()) defaultCtr.setAccessible(true);
        return defaultCtr.newInstance();
    }

    private List<Object> resolveMethodArguments(Method method) {
        List<Object> args = new ArrayList<>();
        for (Parameter param : method.getParameters()) {
            Class<?> type = param.getType();
            Value annotation = param.getAnnotation(Value.class);
            if(ObjectUtils.isNotEmpty(annotation)){
                String value = PropertiesUtils.resolvePlaceholderValue(properties, annotation.value());
                Object convertedValue = PropertiesUtils.convertValue(type, value);
                args.add(convertedValue);
                continue;
            }
            // 处理 @Qualifier
            Qualifier qualifierAnno = param.getAnnotation(Qualifier.class);
            if (qualifierAnno != null) {
                String beanName = qualifierAnno.value();
                args.add(getBean(beanName)); // 按名字精确获取
                continue;
            }
            if (isSimpleType(type)) {
                args.add(getDefaultValue(type));
            } else {
                args.add(getBean(type));
            }
        }
        return args;
    }

    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive()
                || type == String.class
                || Number.class.isAssignableFrom(type)
                || type == Boolean.class;
    }

    private Object getDefaultValue(Class<?> type) {
        if (type == int.class || type == Integer.class) return 0;
        if (type == long.class || type == Long.class) return 0L;
        if (type == boolean.class || type == Boolean.class) return false;
        if (type == String.class) return "";
        return null;
    }




}
