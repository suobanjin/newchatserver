package zzuli.zw.config.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * 自研框架版 StringRedisTemplate
 */
public class StringRedisTemplate {

    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisCommands<String, String> commands;

    public StringRedisTemplate(RedisClient redisClient) {
        this.redisClient = redisClient;
        this.connection = redisClient.connect();
        this.commands = connection.sync();
    }

    public void set(String key, String value) {
        commands.set(key, value);
    }

    public String get(String key) {
        return commands.get(key);
    }

    public void del(String key) {
        commands.del(key);
    }

    public boolean exists(String key) {
        return commands.exists(key) > 0;
    }

    public void expire(String key, long seconds) {
        commands.expire(key, seconds);
    }

    public void close() {
        connection.close();
        redisClient.shutdown();
    }
}

