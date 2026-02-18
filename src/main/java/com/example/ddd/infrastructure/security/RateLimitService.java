package com.example.ddd.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的请求频率限制服务
 * 使用滑动窗口算法实现
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 默认限制：每分钟60次请求
     */
    private static final int DEFAULT_LIMIT = 60;

    /**
     * 默认窗口大小：60秒
     */
    private static final int DEFAULT_WINDOW_SECONDS = 60;

    /**
     * 登录接口限制：每分钟5次
     */
    private static final int LOGIN_LIMIT = 5;

    /**
     * 注册接口限制：每小时10次
     */
    private static final int REGISTER_LIMIT = 10;
    private static final int REGISTER_WINDOW_SECONDS = 3600;

    /**
     * 检查请求是否被允许（使用默认限制）
     *
     * @param key 限制键（如IP地址或用户ID）
     * @return 是否允许
     */
    public boolean allowRequest(String key) {
        return allowRequest(key, DEFAULT_LIMIT, DEFAULT_WINDOW_SECONDS);
    }

    /**
     * 检查登录请求是否被允许
     *
     * @param key 限制键（如IP地址或用户名）
     * @return 是否允许
     */
    public boolean allowLogin(String key) {
        return allowRequest("login:" + key, LOGIN_LIMIT, DEFAULT_WINDOW_SECONDS);
    }

    /**
     * 检查注册请求是否被允许
     *
     * @param key 限制键（如IP地址）
     * @return 是否允许
     */
    public boolean allowRegister(String key) {
        return allowRequest("register:" + key, REGISTER_LIMIT, REGISTER_WINDOW_SECONDS);
    }

    /**
     * 检查请求是否被允许
     *
     * @param key           限制键
     * @param limit         限制次数
     * @param windowSeconds 窗口大小（秒）
     * @return 是否允许
     */
    public boolean allowRequest(String key, int limit, int windowSeconds) {
        String redisKey = "rate_limit:" + key;

        try {
            // 获取当前计数
            String countStr = redisTemplate.opsForValue().get(redisKey);
            int count = countStr != null ? Integer.parseInt(countStr) : 0;

            if (count >= limit) {
                log.warn("请求频率超限: key={}, count={}, limit={}", key, count, limit);
                return false;
            }

            // 增加计数
            if (count == 0) {
                // 第一次请求，设置过期时间
                redisTemplate.opsForValue().set(redisKey, "1", windowSeconds, TimeUnit.SECONDS);
            } else {
                // 后续请求，只增加计数
                redisTemplate.opsForValue().increment(redisKey);
            }

            return true;
        } catch (Exception e) {
            log.error("Redis频率限制检查失败: {}", e.getMessage());
            // Redis失败时，默认允许请求通过（降级策略）
            return true;
        }
    }

    /**
     * 获取剩余请求次数
     *
     * @param key 限制键
     * @return 剩余次数，-1表示无限制或查询失败
     */
    public int getRemainingRequests(String key) {
        return getRemainingRequests(key, DEFAULT_LIMIT);
    }

    /**
     * 获取剩余请求次数
     *
     * @param key   限制键
     * @param limit 限制次数
     * @return 剩余次数，-1表示查询失败
     */
    public int getRemainingRequests(String key, int limit) {
        String redisKey = "rate_limit:" + key;

        try {
            String countStr = redisTemplate.opsForValue().get(redisKey);
            int count = countStr != null ? Integer.parseInt(countStr) : 0;
            return Math.max(0, limit - count);
        } catch (Exception e) {
            log.error("获取剩余请求次数失败: {}", e.getMessage());
            return -1;
        }
    }

    /**
     * 获取重置时间（秒）
     *
     * @param key 限制键
     * @return 重置时间（秒），-1表示查询失败
     */
    public long getResetTime(String key) {
        String redisKey = "rate_limit:" + key;

        try {
            Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            return ttl != null ? ttl : -1;
        } catch (Exception e) {
            log.error("获取重置时间失败: {}", e.getMessage());
            return -1;
        }
    }

    /**
     * 重置计数器
     *
     * @param key 限制键
     */
    public void reset(String key) {
        String redisKey = "rate_limit:" + key;
        redisTemplate.delete(redisKey);
    }
}
