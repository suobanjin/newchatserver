package zzuli.zw.main.scheduler;

import lombok.extern.slf4j.Slf4j;
import zzuli.zw.main.manager.IMSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @ClassName IMHeartbeatScheduler
 * @Description IM系统心跳调度器 定期检查Session心跳状态，清理超时连接
 * @Author 索半斤
 * @Date 2025/10/01 14:17
 * @Version 1.0
 */
@Slf4j
public class IMHeartbeatScheduler {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static volatile boolean isRunning = false;
    
    // 每60秒检查一次心跳
    private static final long CHECK_INTERVAL = 60 * 1000; // 30秒
    private static final long INITIAL_DELAY = 10 * 1000; // 10秒后开始
    
    /**
     * 启动心跳检查任务
     */
    public static synchronized void start() {
        if (isRunning) {
            log.warn("心跳检查任务已在运行中");
            return;
        }
        
        scheduler.scheduleAtFixedRate(
            IMHeartbeatScheduler::checkHeartbeats,
            INITIAL_DELAY,
            CHECK_INTERVAL,
            TimeUnit.MILLISECONDS
        );
        
        isRunning = true;
        log.info("IM心跳检查任务已启动，检查间隔: {} 毫秒", CHECK_INTERVAL);
    }
    
    /**
     * 停止心跳检查任务
     */
    public static synchronized void stop() {
        if (!isRunning) {
            log.warn("心跳检查任务未在运行");
            return;
        }
        
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        isRunning = false;
        log.info("IM心跳检查任务已停止");
    }
    
    /**
     * 检查所有Session的心跳状态
     */
    private static void checkHeartbeats() {
        try {
            int cleanedCount = IMSessionManager.cleanupTimeoutSessions();
            if (cleanedCount > 0) {
                log.info("清理超时Session数量: {}", cleanedCount);
            }
            
            // 记录统计信息
            IMSessionManager.SessionStats stats = IMSessionManager.getSessionStats();
            if (stats.getTimeoutSessions() > 0) {
                log.debug("当前Session状态: {}", stats);
            }
            
        } catch (Exception e) {
            log.error("检查心跳时发生错误", e);
        }
    }
    
    /**
     * 手动检查心跳
     */
    public static int manualCheckHeartbeats() {
        log.info("手动检查Session心跳状态");
        return IMSessionManager.cleanupTimeoutSessions();
    }
    
    /**
     * 获取心跳检查状态
     */
    public static boolean isRunning() {
        return isRunning;
    }
}

