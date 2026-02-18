package com.example.ddd.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * JWT 认证过滤器
 *
 * 从请求头中提取 JWT token，验证并设置用户认证信息
 * 实现单点登录：检查 token 是否是用户当前有效的 token
 * 使用 Redis 缓存用户详情，降低数据库压力
 *
 * @author DDD Demo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserTokenService userTokenService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户详情缓存 key 前缀
     */
    private static final String USER_DETAILS_PREFIX = "user:details:";

    /**
     * 用户详情缓存过期时间（分钟）
     */
    private static final long CACHE_EXPIRE_MINUTES = 30;

    /**
     * JWT 请求头名称
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * JWT token 前缀
     */
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求中获取 JWT token
            String jwt = extractJwtFromRequest(request);

            // 验证 token 并设置认证信息
            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                // 从 token 中获取用户名
                String username = jwtUtil.getUsernameFromToken(jwt);

                // 检查 token 是否是用户当前有效的 token（单点登录）
                if (!userTokenService.isCurrentValidToken(username, jwt)) {
                    log.debug("Token 已失效（用户在其他地方登录）");
                    filterChain.doFilter(request, response);
                    return;
                }

                // 从缓存加载用户详情
                UserDetails userDetails = getUserDetailsFromCache(username);

                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // 设置认证详情
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 将认证信息设置到安全上下文中
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("用户 {} 认证成功", username);
            }
        } catch (Exception e) {
            log.error("无法设置用户认证: {}", e.getMessage());
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从缓存加载用户详情
     * 如果缓存未命中，则从数据库加载并缓存
     *
     * @param username 用户名
     * @return 用户详情
     */
    private UserDetails getUserDetailsFromCache(String username) {
        String cacheKey = USER_DETAILS_PREFIX + username;

        try {
            // 尝试从缓存获取
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached instanceof UserDetailsImpl userDetails) {
                log.debug("从缓存加载用户详情: {}", username);
                return userDetails;
            }
        } catch (Exception e) {
            log.warn("从缓存获取用户详情失败: {}", e.getMessage());
        }

        // 缓存未命中，从数据库加载
        log.debug("从数据库加载用户详情: {}", username);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 缓存用户详情
        try {
            long tokenRemainingTime = jwtUtil.getTokenRemainingTime(
                    userTokenService.getUserToken(username)
            );
            // 缓存时间取 token 剩余时间和默认缓存时间的较小值
            long cacheExpire = Math.min(tokenRemainingTime / 60, CACHE_EXPIRE_MINUTES);
            if (cacheExpire > 0) {
                redisTemplate.opsForValue().set(
                        cacheKey,
                        userDetails,
                        cacheExpire,
                        TimeUnit.MINUTES
                );
                log.debug("缓存用户详情: {}, 过期时间: {} 分钟", username, cacheExpire);
            }
        } catch (Exception e) {
            log.warn("缓存用户详情失败: {}", e.getMessage());
        }

        return userDetails;
    }

    /**
     * 从请求中提取 JWT token
     *
     * @param request HTTP 请求
     * @return JWT token，如果不存在则返回 null
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
