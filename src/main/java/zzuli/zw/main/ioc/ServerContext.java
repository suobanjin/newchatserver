package zzuli.zw.main.ioc;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import zzuli.zw.NewChatServerStart;
import zzuli.zw.main.annotation.*;
import zzuli.zw.main.factory.ArgumentResolvers;
import zzuli.zw.main.factory.InterceptorsQueue;
import zzuli.zw.main.factory.RequestBeanContainer;
import zzuli.zw.main.interfaces.HandlerInterceptor;
import zzuli.zw.main.interfaces.HandlerMethodArgumentResolver;
import zzuli.zw.main.ioc.beanPostProcessors.AopBeanPostProcessor;
import zzuli.zw.main.ioc.beanPostProcessors.AutowiredAnnotationBeanPostProcessor;
import zzuli.zw.main.ioc.beanPostProcessors.ValueAnnotationBeanPostProcessor;
import zzuli.zw.main.ioc.interfaces.BeanPostProcessor;
import zzuli.zw.main.ioc.mybatis.MybatisSessionTemplate;
import zzuli.zw.main.utils.ClassUtil;
import zzuli.zw.service.interfaces.UserService;
import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] BEAN_ANNOTATIONS = new Class[]{
            Bean.class,
            Service.class,
            Repository.class,
            Request.class,
    };
    @SuppressWarnings("unchecked")
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
        Set<Class<?>> classes = registerBeans(scanAnnotation);
        // 5. 刷新容器
        refresh(configClass, classes);
    }

    private void registerDefaultBeanPostProcessor() {
        try {
            // 注册内置的 BeanPostProcessor
            beanFactory.registerBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
            beanFactory.registerBeanPostProcessor(new ValueAnnotationBeanPostProcessor(properties));
            beanFactory.registerBeanPostProcessor(new AopBeanPostProcessor());
            log.info("[loading...] 成功注册BeanPostProcessors: Autowired, Value, AOP");
        } catch (Exception e) {
            log.warn("[loading...] BeanPostProcessors注册失败: {}", e.getMessage());
        }
    }

    public Object getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }

    public Object getBean(Class<?> clazz) {
        return beanFactory.getBean(clazz);
    }

    public Object getBeanBySuper(Class<?> clazz) {
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

    /**
     * 刷新容器
     *
     * @param primaryConfigClass
     * @param scanned
     */
    private void refresh(Class<?> primaryConfigClass, Set<Class<?>> scanned){
        // 首先注入BeanPostProcessor
        Set<String> processedBps = new HashSet<>();
        for (Map.Entry<String, BeanDefinition> entry : beanFactory.getBeanDefinitions().entrySet()) {
            if (BeanPostProcessor.class.isAssignableFrom(entry.getValue().getBeanClass())) {
                Object obj =  beanFactory.getBean(entry.getKey());
                if (Request.class.isAssignableFrom(entry.getValue().getAnnotationType())){
                    RequestBeanContainer.addRequest(entry.getValue().getBeanName(), obj);
                }
                BeanPostProcessor bpp = (BeanPostProcessor) obj;
                if (!processedBps.contains(entry.getKey())) {
                    beanFactory.registerBeanPostProcessor(bpp);
                    processedBps.add(entry.getKey());
                }
            }
        }
        log.info("[loading...] 成功注册BeanPostProcessors: {}", processedBps.size());

//        // 创建和初始化配置类
//        Set<String> processedConfigurations = new HashSet<>();
//        for (BeanDefinition beanDefinition : beanFactory.getBeanDefinitions().values()) {
//            if (processedBps.contains(beanDefinition.getBeanName())) continue;
//            if (beanDefinition.getAnnotationType().isAnnotationPresent(Configuration.class)){
//                beanFactory.getBean(beanDefinition.getBeanClass());
//                processedConfigurations.add(beanDefinition.getBeanName());
//            }
//        }

        // 处理Mapper
        processMapperScan(primaryConfigClass, scanned);

        // 创建普通Bean
        for (BeanDefinition beanDefinition : beanFactory.getBeanDefinitions().values()) {
            if (processedBps.contains(beanDefinition.getBeanName())) continue;
            //if (processedConfigurations.contains(beanDefinition.getBeanName()))continue;
            Object bean = beanFactory.getBean(beanDefinition.getBeanClass());
            if (Request.class.isAssignableFrom(beanDefinition.getAnnotationType())){
                RequestBeanContainer.addRequest(beanDefinition.getBeanName(), bean);
                continue;
            }
            if (bean instanceof HandlerMethodArgumentResolver){
                ArgumentResolvers.addResolver((HandlerMethodArgumentResolver) bean);
                continue;
            }
            if (bean instanceof HandlerInterceptor){
                InterceptorsQueue.getInstance().add((HandlerInterceptor) bean);
            }
        }
        //log.info("[loading...] 创建普通Bean: {}", beanFactory.getBeanDefinitions().size() - processedBps.size() - processedConfigurations.size());
        log.info("[loading...] 容器初始化完成");
    }

    /**
     * 刷新容器
     */
    @SuppressWarnings("unused")
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
        Set<String> processedConfigurations = new HashSet<>();
        for (BeanDefinition beanDefinition : beanFactory.getBeanDefinitions().values()) {
            if (processedBps.contains(beanDefinition.getBeanName())) continue;
            if (beanDefinition.getAnnotationType().isAnnotationPresent(Configuration.class)){
                beanFactory.getBean(beanDefinition.getBeanClass());
            }
            processedConfigurations.add(beanDefinition.getBeanName());
        }

        for (BeanDefinition beanDefinition : beanFactory.getBeanDefinitions().values()) {
            if (processedBps.contains(beanDefinition.getBeanName())) continue;
            if (processedConfigurations.contains(beanDefinition.getBeanName()))continue;
            beanFactory.getBean(beanDefinition.getBeanClass());
        }

    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * 判断类是否是 Bean 的候选者
     * @param clazz
     * @return
     */
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
     * @param clazz
     * @return
     */
    private BeanDefinition resolveBeanDefinition(Class<?> clazz) {
        for (Class<? extends Annotation> annotationClass : BEAN_ANNOTATIONS_CONFIGURATION) {
            if (clazz.isAnnotationPresent(annotationClass)) {
                Annotation annotation = clazz.getAnnotation(annotationClass);

                // 尝试从注解属性中解析 BeanName
                String beanName = tryResolveBeanName(annotation);
                // 如果未指定 BeanName，则使用类名
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
            // 根据method.invoke获取对应的值
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
            throw new RuntimeException("Failed to read annotation attribute: " + attribute, e);
        }
        return null;
    }

    /**
     * 单独处理配置类的逻辑
     * @param clazz
     */
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

    /**
     * 扫描并将Bean定义为BeanDefinition，注册到BeanFactory中
     * @param scanAnnotation
     * @return
     */
    public Set<Class<?>> registerBeans(BeanScan scanAnnotation) {
        // 3. 注册Bean
        String basePackage = scanAnnotation.value();
        Set<Class<?>> classes = ClassUtil.extractPackageClass(basePackage);
        // 过滤掉注解类型
        classes.removeIf(Class::isAnnotation);
        for (Class<?> clazz : classes) {
            // 如果是候选Bean，则注册为BeanDefinition
            if (isBeanCandidate(clazz)) {
                BeanDefinition beanDefinition = resolveBeanDefinition(clazz);
                if (Objects.isNull(beanDefinition)) continue;
                beanFactory.registerBeanDefinition(beanDefinition);
                // 如果是配置类，按照单独的逻辑处理
            } else if (clazz.isAnnotationPresent(Configuration.class)) {
                processConfigurationClasses(clazz);
            }
        }
        return classes;
    }

    /**
     * 单独处理Mybatis-plus的代理Mapper注册逻辑
     * @param primaryConfigClass
     * @param scanned
     */
    private void processMapperScan(Class<?> primaryConfigClass, Set<Class<?>> scanned) {
        // 检测 @MapperScan 是否配置
        MapperScan mapperScan = primaryConfigClass.getAnnotation(MapperScan.class);
        // 获取扫描包路径
        String mapperPackage = (!Objects.isNull(mapperScan)) ? mapperScan.value() : primaryConfigClass.getAnnotation(BeanScan.class).value();
        // 从容器获取MybatisConfiguration
        MybatisConfiguration mybatisConfiguration = (MybatisConfiguration) getBean(MybatisConfiguration.class);
        if (Objects.isNull(mybatisConfiguration)){
            throw new RuntimeException("MybatisConfiguration not found");
        }
        // 从容器中获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) getBean(SqlSessionFactory.class);
        if (Objects.isNull(sqlSessionFactory)){
            throw new RuntimeException("SqlSessionFactory not found");
        }
        // 根据包路径获取Mapper类
        if (mapperPackage != null && !mapperPackage.isEmpty()) {
            Set<Class<?>> mapperClasses = ClassUtil.extractPackageClass(mapperPackage);
            for (Class<?> mapperClass : mapperClasses) {
                if (!mapperClass.isInterface()) continue;
                if (!mapperClass.isAnnotationPresent(Mapper.class)) continue;
                // 使用 MyBatis-Plus 的 Configuration
                if (!mybatisConfiguration.hasMapper(mapperClass)) {
                    mybatisConfiguration.addMapper(mapperClass);
                }
                registerSingleMapper(mapperClass, sqlSessionFactory, null);
            }
        }
        // 扫描其他标注@Mapper的接口进行注册
        for (Class<?> c : scanned) {
            if (c.isInterface() && c.isAnnotationPresent(Mapper.class)) {
                // 使用 MyBatis-Plus 的 Configuration
                if (!mybatisConfiguration.hasMapper(c)) {
                    mybatisConfiguration.addMapper(c);
                }
                registerSingleMapper(c, sqlSessionFactory, null);
            }
        }
    }

    /**
     * 将单个Mapper注册到BeanFactory
     * @param mapperClass
     * @param sqlSessionFactory
     * @param template
     * @param <T>
     */
    private <T> void registerSingleMapper(Class<T> mapperClass, SqlSessionFactory sqlSessionFactory, MybatisSessionTemplate template) {
        try {
            // 创建代理对象，可以通过自定义模板或者默认的SqlSessionFactory
            Object proxy;
            if (template != null) {
                proxy = template.getMapperProxy(mapperClass);
            } else if (sqlSessionFactory != null) {
                proxy = createSessionBasedMapperProxy(mapperClass, sqlSessionFactory);
            } else {
                throw new RuntimeException("No SqlSessionFactory or MybatisSessionTemplate available for registering mapper: " + mapperClass);
            }
            // 将代理对象注册到BeanFactory
            String beanName = Introspector.decapitalize(mapperClass.getSimpleName());
            beanFactory.registerBean(beanName, proxy);
            BeanDefinition beanDefinition = new BeanDefinition(beanName, mapperClass.getName(), mapperClass, Mapper.class);
            beanFactory.registerBeanDefinition(beanDefinition);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register mapper: " + mapperClass.getName(), e);
        }
    }

    /**
     * 创建Mapper代理
     * @param mapperClass
     * @param sqlSessionFactory
     * @return
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    private <T> T createSessionBasedMapperProxy(Class<T> mapperClass, SqlSessionFactory sqlSessionFactory) {
        return (T) Proxy.newProxyInstance(mapperClass.getClassLoader(), new Class[]{mapperClass},
                (proxy, method, args) -> {
                    try (SqlSession session = sqlSessionFactory.openSession(true)) {
                        Object mapper = session.getMapper(mapperClass);
                        return method.invoke(mapper, args);
                    }
                });
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
