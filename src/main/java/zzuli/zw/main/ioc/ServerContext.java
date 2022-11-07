package zzuli.zw.main.ioc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzuli.zw.NewChatServerStart;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.main.annotation.BeanScan;
import zzuli.zw.main.annotation.Injection;
import zzuli.zw.main.annotation.Transaction;
import zzuli.zw.main.ioc.interfaces.BeanPostProcessor;
import zzuli.zw.main.ioc.interfaces.InitializingBean;
import zzuli.zw.request.FriendRequest;
import zzuli.zw.main.aop.AopUtils;
import zzuli.zw.main.utils.ClassUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 索半斤
 * @description IOC上下文环境
 * @date 2022/2/9
 * @className ServerContext
 */
public class ServerContext {
    private static final Logger logger = LoggerFactory.getLogger(ServerContext.class);
    //主要保存了bean的信息，保存了class，className，beanName
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private Map<Class<?>, BeanDefinition> classBeanDefinitionMap = new ConcurrentHashMap<>();
    //存放BeanPostProcessor
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    //对象工厂
    private final BeanFactory beanFactory = new BeanFactory();
    //
    private Object object;

    public ServerContext(Class<?> configClass) {
        BeanScan annotation = configClass.getAnnotation(BeanScan.class);
        if (annotation == null) {
            logger.error("serverContext init error");
            throw new RuntimeException();
        }
        String value = annotation.value();
        if (value == null || value.equals("")) {
            logger.error("BeanScan value is null");
            throw new RuntimeException();
        }
        Set<Class<?>> classes = ClassUtil.extractPackageClass(value);
        for (Class<?> clazz : classes) {
            BeanDefinition beanDefinition = new BeanDefinition(null, clazz.getTypeName(), clazz);
            Bean beanAnnotation = clazz.getAnnotation(Bean.class);
            String beanName;
            if (beanAnnotation != null) {
                beanName = beanAnnotation.value();
                //判断clazz是否是BeanPostProcessor的实现类/子类
                if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                    try {
                        BeanPostProcessor bpp = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                        beanPostProcessors.add(bpp);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
                beanDefinitionMap.put(beanName, beanDefinition);
                classBeanDefinitionMap.put(clazz, beanDefinition);
            }
        }
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            Object bean = initBean(beanName, beanDefinition);
            beanFactory.init(beanName, bean);
        }
    }

    private Object initBean(String beanName, BeanDefinition beanDefinition) {
        Class beanClass = beanDefinition.getBeanClass();
        try {
            //实例化（用class调用构造方法来进行实例化）
            Object bean = beanClass.getDeclaredConstructor().newInstance();
            //填充属性
            Field[] fields = beanClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Injection.class)) {
                    Injection annotation = field.getAnnotation(Injection.class);
                    String fieldName;
                    if (annotation == null || annotation.name() == ""){
                        fieldName = field.getName();
                    }else{
                        fieldName = annotation.name();
                    }
                    object = getBean(fieldName);
                    Transaction transaction = object.getClass().getAnnotation(Transaction.class);
                    if (transaction != null) {
                        //切面处理
                        object = AopUtils.aop(object);
                    }
                    //反射中必须设置true才可以通过反射访问字段，才能给实例化赋值
                    field.setAccessible(true);
                    field.set(bean, object);
                }
            }
            //可实现自定义的逻辑
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                bean = beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
            }
            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
            }
            //可实现自定义的逻辑
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                bean = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
            }
            return bean;
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        //单例生成，从单例池中拿对象
        Object bean = beanFactory.getBean(beanName);
        if (bean == null) {
            Object o = initBean(beanName, beanDefinition);
            beanFactory.init(beanName, o);
            return o;
        }
        return bean;
    }

    public Object getBean(Class clazz) {
        return beanFactory.getBean(clazz);
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public static void main(String[] args) {
        ServerContext serverContext = new ServerContext(NewChatServerStart.class);
        FriendRequest friendRequest = (FriendRequest) serverContext.getBean(FriendRequest.class);
        //friendRequest.test();
    }
}
