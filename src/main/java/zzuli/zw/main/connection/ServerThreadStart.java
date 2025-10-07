package zzuli.zw.main.connection;

import cn.hutool.core.thread.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzuli.zw.main.annotation.BeanScan;
import zzuli.zw.main.annotation.Request;
import zzuli.zw.main.factory.InterceptorsQueue;
import zzuli.zw.main.interfaces.HandlerInterceptor;
import zzuli.zw.main.factory.ArgumentResolvers;
import zzuli.zw.main.factory.RequestBeanContainer;
import zzuli.zw.main.factory.SocketContainer;
import zzuli.zw.main.interfaces.HandlerMethodArgumentResolver;
import zzuli.zw.main.ioc.BeanFactory;
import zzuli.zw.main.ioc.ServerContext;
import zzuli.zw.main.utils.ClassUtil;
import zzuli.zw.main.utils.ConfigUtils;
import zzuli.zw.main.utils.InterceptorUtils;
import zzuli.zw.main.utils.SocketUtils;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 负责服务端和客户端通信
 * @author 索半斤
 */
public class ServerThreadStart {
    private static final int DEFAULT_PORT = 2077;
    private static final String SERVER_PORT = "serverPort";
    private static final Logger logger = LoggerFactory.getLogger(ServerThreadStart.class);
    private Class<?> clazz;
    private ServerContext serverContext;

    /**
     * @Author 索半斤
     * @Description 初始化服务端服务
     * @Date 2022/1/29 22:04
     * @Param []
     * @return
     **/
    public ServerThreadStart(Class<?> clazz) throws IOException {
        ServerSocket ss = null;
        try {
            this.clazz = clazz;
            initServerContext();
//            initRequestBean();
//            initInterceptorsBean();
//            initArgumentResolvers();
            String configAttribute = ConfigUtils.getConfigAttribute(SERVER_PORT);
            if (configAttribute == null) {
                ss = new ServerSocket(DEFAULT_PORT);
                logger.info("server is running in port ----> " + DEFAULT_PORT);
            }else{
                ss = new ServerSocket(Integer.parseInt(configAttribute));
                logger.info("server is running in port ----> " + configAttribute);
            }
            while (!ss.isClosed()) {
                Socket socket = ss.accept();
                logger.info(socket.getInetAddress().getHostAddress() +"使用端口" + socket.getPort() + "连接成功!");
                RequestServerThread requestServerThread = new RequestServerThread(socket,serverContext);
                ThreadUtil.execute(requestServerThread);
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                ss.close();
                SocketContainer<Object, Socket> instance = SocketContainer.getInstance();
                instance.forEach((key,value) -> SocketUtils.closeSocket(value));
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    /**
     * @Author 索半斤
     * @Description 初始化request
     * @Date 2022/1/29 22:02
     * @Param []
     * @return void
     **/
    private void initServerContext(){
        this.serverContext = new ServerContext(clazz);
        logger.info("serverContext config completed");
    }

    private void initRequestBean(){
        BeanFactory beanFactory = serverContext.getBeanFactory();
        for (Object bean : beanFactory.getBeans()) {
            Request annotation = bean.getClass().getAnnotation(Request.class);
            if (annotation != null){
                String value = annotation.value();
                if (value == null || value.equals("")) {
                    String request = annotation.request();
                    if (request == null || request.equals("")) {
                        String name = bean.getClass().getName();
                        name = name.substring(name.lastIndexOf(".") + 1);
                        name = name.toLowerCase();
                        RequestBeanContainer.addRequest(name, bean);
                    } else {
                        RequestBeanContainer.addRequest(request, bean);
                    }
                }else{
                    RequestBeanContainer.addRequest(value, bean);
                }
            }
        }
        logger.info("requestBeans config completed");
        logger.info("requestBeans count {}", RequestBeanContainer.getInstance().size());
    }

    /**
     * @Author 索半斤
     * @Description 初始化拦截器
     * @Date 2022/1/29 22:02
     * @Param []
     * @return void
     **/
    private void initInterceptorsBean(){
        ArrayBlockingQueue<HandlerInterceptor> interceptors = InterceptorUtils.getInterceptors();
        InterceptorsQueue.getInstance().addBeans(interceptors);
        logger.info("interceptors config completed");
        logger.info("interceptors count " + interceptors.size());

    }

    /**
     * @Author 索半斤
     * @Description 初始化参数解析器
     * @Date 2022/1/29 22:02
     * @Param []
     * @return void
     **/
    private void initArgumentResolvers() throws InterruptedException {
        BeanScan annotation = clazz.getAnnotation(BeanScan.class);
        if (annotation == null){
            logger.error("serverContext init error");
            throw new RuntimeException();
        }
        String value = annotation.value();
        if (value == null || value.equals("")){
            logger.error("BeanScan value is null");
            throw new RuntimeException();
        }
        Set<Class<?>> classes = ClassUtil.extractPackageClass(value);
        for (Class<?> next : classes) {
            if (next.isInterface())continue;
            Class<?>[] interfaces = next.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                if (anInterface == HandlerMethodArgumentResolver.class){
                    ArgumentResolvers.addResolver(ClassUtil.newObject(next));
                }
            }
        }
        logger.info("resolver config completed");
        logger.info("resolver count " + ArgumentResolvers.getInstance().size());
    }

    /*String basePacket = ConfigUtils.getConfigAttribute(REQUEST_PACKET);
        Set<Class<?>> classSet = ClassUtil.extractPackageClass(basePacket);
        if (classSet == null || classSet.size() == 0){
            logger.warn("requestBean count 0");
            throw new RuntimeException("can not init requestBean");
        }
        classSet.forEach(x -> {
            Request annotation = x.getAnnotation(Request.class);
            if (annotation != null){
                String value = annotation.value();
                Object baseRequestBean = ClassUtil.newObject(x);
                if (value == null || value.equals("")) {
                    String request = annotation.request();
                    if (request == null || request.equals("")) {
                        String name = x.getName();
                        name = name.substring(name.lastIndexOf(".") + 1);
                        name = name.toLowerCase();
                        RequestBeanContainer.addRequest(name, baseRequestBean);
                    } else {
                        RequestBeanContainer.addRequest(request, baseRequestBean);
                    }
                }else{
                    RequestBeanContainer.addRequest(value, baseRequestBean);
                }
            }
        });*/
}