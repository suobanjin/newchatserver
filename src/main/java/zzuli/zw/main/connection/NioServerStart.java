package zzuli.zw.main.connection;

import lombok.extern.slf4j.Slf4j;
import zzuli.zw.main.annotation.BeanScan;
import zzuli.zw.main.ioc.ServerContext;
import zzuli.zw.main.utils.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * NIO服务器启动类
 * 替代原有的BIO ServerThreadStart
 */
@Slf4j
public class NioServerStart {
    private static final int DEFAULT_PORT = 2077;
    private static final String SERVER_PORT = "server.port";
    
    private Class<?> clazz;
    private ServerContext serverContext;
    //private NioServer nioServer;
    private MultiThreadNioServer multiThreadNioServer;
    
    /**
     * 初始化NIO服务器
     */
    public NioServerStart(Class<?> clazz) throws IOException {
        this.clazz = clazz;
        initServerContext();
        initNioServer();
    }
    
    /**
     * 初始化服务器上下文
     */
    private void initServerContext() {
        this.serverContext = new ServerContext(clazz);
        log.info("NIO服务器上下文初始化完成");
    }
    
    /**
     * 初始化NIO服务器
     */
    private void initNioServer() throws IOException {
        // 获取端口配置
        String configAttribute = ConfigUtils.getConfigAttribute(SERVER_PORT);
        int port = DEFAULT_PORT;
        if (configAttribute != null) {
            port = Integer.parseInt(configAttribute);
        }
        
        // 创建NIO服务器
        //this.nioServer = new NioServer(port, serverContext);
        // 使用多线程NIO服务器
        this.multiThreadNioServer = new MultiThreadNioServer(port, serverContext);
        log.info("NIO服务器初始化完成，端口: {}", port);
    }
    
    /**
     * 启动NIO服务器
     */
    public void start() throws IOException {
        if (!Objects.isNull(multiThreadNioServer)) {
            multiThreadNioServer.start();
        } else {
            throw new IllegalStateException("NIO服务器未初始化");
        }
    }
    
    /**
     * 停止NIO服务器
     */
    public void stop() throws InterruptedException {
        if (!Objects.isNull(multiThreadNioServer)) {
            multiThreadNioServer.stop();
        }
    }
    
    /**
     * 获取服务器状态
     */
    public String getServerStatus() {
        if (!Objects.isNull(multiThreadNioServer)) {
            return "多线程NIO服务器运行中";
        }
        return "NIO服务器未运行";
    }
}

