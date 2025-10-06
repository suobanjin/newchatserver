package zzuli.zw.main.ioc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import zzuli.zw.NewChatServerStart;
import zzuli.zw.main.annotation.*;
import zzuli.zw.main.ioc.beanPostProcessors.AopBeanPostProcessor;
import zzuli.zw.main.ioc.beanPostProcessors.AutowiredAnnotationBeanPostProcessor;
import zzuli.zw.main.ioc.beanPostProcessors.ValueAnnotationBeanPostProcessor;
import zzuli.zw.main.ioc.interfaces.BeanPostProcessor;
import zzuli.zw.main.utils.ClassUtil;
import zzuli.zw.service.interfaces.UserService;

import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author 索半斤
 * @description IOC上下文环境
 * @date 2022/2/9
 * @className ServerContext
 */
@Slf4j
public class ServerContext {

    private final BeanFactory beanFactory = new BeanFactory();
    private final Properties properties = new Properties();
    private static final Class<? extends Annotation>[] BEAN_ANNOTATIONS = new Class[]{
            Bean.class,
            Service.class,
            Repository.class,
            Request.class,
    };
    private static final Class<? extends Annotation>[] BEAN_ANNOTATIONS_CONFIGURATION = new Class[]{
            Bean.class,
            Service.class,
            Repository.class,
            Request.class,
            Configuration.class
    };

    public ServerContext(Class<?> configClass) {
        // 1. 加载配置文件
        loadProperties();
        beanFactory.registerProperties(properties);
        // 2. 先注册内置的 BeanPostProcessor（保证它们在其它 Bean 创建前可用）
        registerDefaultBeanPostProcessor();
        // 3. 扫描包路径
        BeanScan scanAnnotation = configClass.getAnnotation(BeanScan.class);
        if (scanAnnotation == null) {
            throw new RuntimeException("缺少 @BeanScan 注解");
        }
        // 4. 注册Bean
        registerBeans(scanAnnotation);
        // 5. 刷新容器
        refresh();

        // 6. 扫描 Mapper
        registerMappers(configClass);
    }

    private void scanAndRegisterMappers(String basePackage) {
        Set<Class<?>> classes = ClassUtil.extractPackageClass(basePackage);
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Mapper.class) && clazz.isInterface()) {
                registerMapper(clazz);
            }
        }
    }


    private void registerMappers(Class<?> configClass) {
        // 扫描 MapperScan 或者 @Mapper 注解
        MapperScan mapperScan = configClass.getAnnotation(MapperScan.class);
        if (!Objects.isNull(mapperScan)) {
            scanAndRegisterMappers(mapperScan.value());
        }else {
            scanAndRegisterMappers(configClass.getAnnotation(BeanScan.class).value());
        }

        // 处理单个 Mapper 注解
        for (BeanDefinition bd : beanFactory.getBeanDefinitions().values()) {
            Class<?> clazz = bd.getBeanClass();
            if (clazz.isAnnotationPresent(Mapper.class) && clazz.isInterface()) {
                registerMapper(clazz);
            }
        }
    }

    private void registerMapper(Class<?> mapperClass) {
        try {
            SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) getBean(SqlSessionFactory.class);
            sqlSessionFactory.getConfiguration().addMapper(mapperClass);
            Object mapperProxy = sqlSessionFactory.openSession(true).getMapper(mapperClass);
            String beanName = Introspector.decapitalize(mapperClass.getSimpleName());
            BeanDefinition beanDefinition = new BeanDefinition(beanName, mapperClass.getClass().getName(), mapperClass.
                    getClass(), Mapper.class);
            beanFactory.registerBeanDefinition(beanDefinition);
            beanFactory.registerBean(beanName, mapperProxy);
            log.info("[Mapper] Registered mapper: {}", mapperClass.getName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to register mapper: " + mapperClass.getName(), e);
        }
    }


    private void registerDefaultBeanPostProcessor() {
        try {
            // 注册内置的 BeanPostProcessor
            beanFactory.registerBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
            beanFactory.registerBeanPostProcessor(new ValueAnnotationBeanPostProcessor(properties));
            beanFactory.registerBeanPostProcessor(new AopBeanPostProcessor());
            log.info("[loading...] 成功注册BeanPostProcessors: Autowired, Value, AOP");
        } catch (Throwable e) {
            log.warn("[loading...] BeanPostProcessors注册失败: {}", e.getMessage());
        }
    }

    public Object getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }

    public Object getBean(Class clazz) {
        return beanFactory.getBean(clazz);
    }

    public Object getBeanBySuper(Class clazz) {
        List<Object> beans = this.beanFactory.getBeans();
        for (Object bean : beans) {
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            for (Class<?> anInterface : interfaces) {
                if (anInterface.equals(clazz)) return bean;
            }
        }
        return null;
    }

    /**
     * 加载properties文件
     *
     */
    private void loadProperties() {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                properties.load(in);
                log.info("[loading...] 成功加载properties配置文件, 属性 {} 个", properties.size());
            } else {
                log.warn("[loading...] classpath路径下未发现配置文件 (跳过)");
            }
        } catch (IOException e) {
            throw new RuntimeException("加载properties配置文件失败", e);
        }
    }

    private void refresh() {
        // 首先注入BeanPostProcessor
        Set<String> processedBps = new HashSet<>();
        for (Map.Entry<String, BeanDefinition> entry : beanFactory.getBeanDefinitions().entrySet()) {
            if (BeanPostProcessor.class.isAssignableFrom(entry.getValue().getBeanClass())) {
                BeanPostProcessor bpp = (BeanPostProcessor) beanFactory.getBean(entry.getKey());
                if (!processedBps.contains(entry.getKey())) {
                    beanFactory.registerBeanPostProcessor(bpp);
                    processedBps.add(entry.getKey());
                }
            }
        }

        for (BeanDefinition beanDefinition : beanFactory.getBeanDefinitions().values()) {
            if (processedBps.contains(beanDefinition.getBeanName())) continue;
            beanFactory.getBean(beanDefinition.getBeanClass());
        }

    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    private boolean isBeanCandidate(Class<?> clazz) {
        for (Class<? extends Annotation> annotationClass : BEAN_ANNOTATIONS) {
            if (clazz.isAnnotationPresent(annotationClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从 class 上解析 Bean 信息
     */
    private BeanDefinition resolveBeanDefinition(Class<?> clazz) {
        for (Class<? extends Annotation> annotationClass : BEAN_ANNOTATIONS_CONFIGURATION) {
            if (clazz.isAnnotationPresent(annotationClass)) {
                Annotation annotation = clazz.getAnnotation(annotationClass);

                // 尝试从注解属性中解析 BeanName
                String beanName = tryResolveBeanName(annotation);
                if (StringUtils.isEmpty(beanName)) {
                    beanName = Introspector.decapitalize(clazz.getSimpleName());
                }

                return new BeanDefinition(beanName, clazz.getName(), clazz, annotationClass);
            }
        }
        return null; // 非候选 Bean
    }

    /**
     * 尝试解析注解上的 Bean 名称
     */
    private String tryResolveBeanName(Annotation annotation) {
        // 按优先级顺序尝试属性
        String[] candidateAttributes = {"request", "value", "name", "path"};
        for (String attr : candidateAttributes) {
            String value = getAnnotationAttribute(annotation, attr);
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return null;
    }


    /**
     * 通用方法：读取注解属性
     */
    private String getAnnotationAttribute(Annotation annotation, String attribute) {
        try {
            Method method = annotation.annotationType().getMethod(attribute);
            Object value = method.invoke(annotation);
            if (value instanceof String && StringUtils.isNotEmpty((String) value)) {
                return (String) value;
            }
        } catch (NoSuchMethodException ignored) {
            // 注解没有这个属性，忽略即可
        } catch (Exception e) {
            throw new RuntimeException("[loading...] Failed to read annotation attribute: " + attribute, e);
        }
        return null;
    }

    private void processConfigurationClasses(Class<?> clazz) {

        // 1. 先把配置类本身作为Bean注册
        BeanDefinition configDef = resolveBeanDefinition(clazz);
        beanFactory.registerBeanDefinition(configDef);

        // 2. 遍历其 @IOC 方法
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(IOC.class)) continue;

            String beanName = method.getAnnotation(IOC.class).value();
            if (StringUtils.isEmpty(beanName)) {
                beanName = method.getName(); // 默认方法名作为beanName
            }
            // 用 IOCMethodBeanDefinition 包装
            BeanDefinition bd = new IOCMethodBeanDefinition(
                    beanName,
                    method.getReturnType().getName(),
                    method.getReturnType(),
                    clazz,   // 配置类
                    method,   // @IOC方法
                    configDef
            );
            beanFactory.registerBeanDefinition(bd);
        }

    }

    public void registerBeans(BeanScan scanAnnotation) {
        // 3. 注册Bean
        String basePackage = scanAnnotation.value();
        Set<Class<?>> classes = ClassUtil.extractPackageClass(basePackage);
        // 过滤掉注解类型
        classes.removeIf(Class::isAnnotation);
        for (Class<?> clazz : classes) {
            if (isBeanCandidate(clazz)) {
                BeanDefinition beanDefinition = resolveBeanDefinition(clazz);
                if (Objects.isNull(beanDefinition)) continue;
                beanFactory.registerBeanDefinition(beanDefinition);
            } else if (clazz.isAnnotationPresent(Configuration.class)) {
                processConfigurationClasses(clazz);
            }
        }
    }

    public static void main(String[] args) {
        //friendRequest.test();
        ServerContext serverContext = new ServerContext(NewChatServerStart.class);
        UserService bean = (UserService) serverContext.getBeanBySuper(UserService.class);
//        System.out.println(bean.test(););
        bean.test();

        //bean.broadcast(null);

    }
}
