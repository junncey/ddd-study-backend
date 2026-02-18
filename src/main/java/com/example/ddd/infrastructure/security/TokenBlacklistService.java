package com.example.ddd.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Token 黑名单服务
 *
 * 使用 Redis 存储 token 黑名单，实现登出后 token 失效
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;

    /**
     * Redis key 前缀
     */
    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    /**
     * Refresh Token 黑名单前缀
     */
    private static final String REFRESH_BLACKLIST_PREFIX = "token:refresh:blacklist:";

    /**
     * 将 access token 加入黑名单
     *
     * @param token JWT token
     */
    public void addToBlacklist(String token) {
        try {
            Long remainingTime = jwtUtil.getTokenRemainingTime(token);
            if (remainingTime > 0) {
                String key = BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(key, "1", remainingTime, TimeUnit.SECONDS);
                log.debug("Token 已加入黑名单，剩余有效时间: {} 秒", remainingTime);
            }
        } catch (Exception e) {
            log.error("添加 token 到黑名单失败: {}", e.getMessage());
        }
    }

    /**
     * 将 refresh token 加入黑名单
     *
     * @param refreshToken refresh token
     */
    public void addRefreshTokenToBlacklist(String refreshToken) {
        try {
            Long remainingTime = jwtUtil.getTokenRemainingTime(refreshToken);
            if (remainingTime > 0) {
                String key = REFRESH_BLACKLIST_PREFIX + refreshToken;
                redisTemplate.opsForValue().set(key, "1", remainingTime, TimeUnit.SECONDS);
                log.debug("Refresh token 已加入黑名单，剩余有效时间: {} 秒", remainingTime);
            }
        } catch (Exception e) {
            log.error("添加 refresh token 到黑名单失败: {}", e.getMessage());
        }
    }

    /**
     * 检查 token 是否在黑名单中
     *
     * @param token JWT token
     * @return 是否在黑名单中
     */
    public boolean isBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("检查 token 黑名单失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查 refresh token 是否在黑名单中
     *
     * @param refreshToken refresh token
     * @return 是否在黑名单中
     */
    public boolean isRefreshTokenBlacklisted(String refreshToken) {
        try {
            String key = REFRESH_BLACKLIST_PREFIX + refreshToken;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("检查 refresh token 黑名单失败: {}", e.getMessage());
            return false;
        }
    }
}
