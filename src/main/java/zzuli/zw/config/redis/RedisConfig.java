package zzuli.zw.config.redis;


import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import zzuli.zw.main.annotation.Configuration;
import zzuli.zw.main.annotation.IOC;
import zzuli.zw.main.annotation.Value;
import java.time.Duration;

@Configuration
public class RedisConfig {

    /**
     * 注入 RedisClient（先注册）
     */
    @IOC
    public RedisClient redisClient(
            @Value("${redis.host:127.0.0.1}") String host,
            @Value("${redis.port:6379}") int port,
            @Value("${redis.password}") String password,
            @Value("${redis.timeout:3000}") long timeout
    ) {
        RedisURI uri = RedisURI.builder()
                .withHost(host)
                .withPort(port)
                .withTimeout(Duration.ofSeconds(timeout))
                .build();

        if (password != null && !password.isEmpty()) {
            uri.setPassword(password.toCharArray());
        }

        return RedisClient.create(uri);
    }
    /**
     * 再注入 StringRedisTemplate（依赖 RedisClient）
     */
    @IOC
    public StringRedisTemplate stringRedisTemplate(RedisClient redisClient) {
        return new StringRedisTemplate(redisClient);
    }
}

