package com.example.ddd.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 用户 Token 服务
 *
 * 使用 Redis 存储用户当前的 token，实现单点登录
 * 同一用户只能有一个有效的 token，新登录会使旧 token 失效
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;

    /**
     * Redis key 前缀
     */
    private static final String USER_TOKEN_PREFIX = "user:token:";

    /**
     * 存储用户当前有效的 token
     *
     * @param username 用户名
     * @param token access token
     */
    public void storeUserToken(String username, String token) {
        try {
            Long remainingTime = jwtUtil.getTokenRemainingTime(token);
            if (remainingTime > 0) {
                String key = USER_TOKEN_PREFIX + username;
                redisTemplate.opsForValue().set(key, token, remainingTime, TimeUnit.SECONDS);
                log.debug("存储用户 token: {}, 剩余有效时间: {} 秒", username, remainingTime);
            }
        } catch (Exception e) {
            log.error("存储用户 token 失败: {}", e.getMessage());
        }
    }

    /**
     * 获取用户当前有效的 token
     *
     * @param username 用户名
     * @return 当前有效的 token，如果不存在则返回 null
     */
    public String getUserToken(String username) {
        try {
            String key = USER_TOKEN_PREFIX + username;
            Object token = redisTemplate.opsForValue().get(key);
            return token != null ? token.toString() : null;
        } catch (Exception e) {
            log.error("获取用户 token 失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 删除用户 token（退出登录）
     *
     * @param username 用户名
     */
    public void removeUserToken(String username) {
        try {
            String key = USER_TOKEN_PREFIX + username;
            redisTemplate.delete(key);
            log.debug("删除用户 token: {}", username);
        } catch (Exception e) {
            log.error("删除用户 token 失败: {}", e.getMessage());
        }
    }

    /**
     * 验证 token 是否是用户当前有效的 token
     *
     * @param username 用户名
     * @param token 待验证的 token
     * @return 是否是当前有效的 token
     */
    public boolean isCurrentValidToken(String username, String token) {
        try {
            String currentToken = getUserToken(username);
            // 如果 Redis 中没有 token，允许通过（兼容 Redis 不可用的情况）
            if (currentToken == null) {
                log.debug("Redis 中未找到用户 {} 的 token，允许通过", username);
                return true;
            }
            return token.equals(currentToken);
        } catch (Exception e) {
            log.error("验证用户 token 失败: {}", e.getMessage());
            // 异常情况下允许通过，避免影响正常使用
            return true;
        }
    }
}
