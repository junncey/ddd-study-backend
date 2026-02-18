package com.example.ddd.infrastructure.security;

import com.example.ddd.infrastructure.util.IpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求频率限制过滤器配置
 *
 * @author DDD Demo
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RateLimitFilter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    /**
     * 注册请求频率限制过滤器
     */
    @Bean
    public FilterRegistrationBean<RateLimitFilterImpl> rateLimitFilterRegistration() {
        FilterRegistrationBean<RateLimitFilterImpl> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RateLimitFilterImpl(rateLimitService, objectMapper));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        registration.setName("rateLimitFilter");
        return registration;
    }

    /**
     * 请求频率限制过滤器实现
     */
    @RequiredArgsConstructor
    public static class RateLimitFilterImpl extends OncePerRequestFilter {

        private final RateLimitService rateLimitService;
        private final ObjectMapper objectMapper;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            String uri = request.getRequestURI();
            String clientIp = IpUtil.getClientIp(request);

            try {
                // 对登录接口进行严格限制
                if (uri.contains("/auth/login")) {
                    String username = request.getParameter("username");
                    String limitKey = username != null ? clientIp + ":" + username : clientIp;

                    if (!rateLimitService.allowLogin(limitKey)) {
                        sendRateLimitResponse(response, "登录请求过于频繁，请稍后再试",
                                rateLimitService.getResetTime("login:" + limitKey));
                        return;
                    }
                }

                // 对注册接口进行严格限制
                if (uri.contains("/auth/register")) {
                    if (!rateLimitService.allowRegister(clientIp)) {
                        sendRateLimitResponse(response, "注册请求过于频繁，请稍后再试",
                                rateLimitService.getResetTime("register:" + clientIp));
                        return;
                    }
                }

                // 对其他API进行一般限制
                if (uri.startsWith("/api/") && !uri.contains("/auth/captcha")) {
                    String limitKey = clientIp + ":" + uri;

                    if (!rateLimitService.allowRequest(limitKey)) {
                        sendRateLimitResponse(response, "请求过于频繁，请稍后再试",
                                rateLimitService.getResetTime(limitKey));
                        return;
                    }
                }

                filterChain.doFilter(request, response);

            } catch (Exception e) {
                log.error("请求频率限制检查异常: {}", e.getMessage(), e);
                // 发生异常时允许请求通过（降级策略）
                filterChain.doFilter(request, response);
            }
        }

        /**
         * 发送频率限制响应
         */
        private void sendRateLimitResponse(HttpServletResponse response, String message, long retryAfter)
                throws IOException {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("X-RateLimit-Reset", String.valueOf(retryAfter));
            response.setHeader("Retry-After", String.valueOf(retryAfter));

            Map<String, Object> body = new HashMap<>();
            body.put("code", HttpStatus.TOO_MANY_REQUESTS.value());
            body.put("message", message);
            body.put("data", null);

            response.getWriter().write(objectMapper.writeValueAsString(body));
        }
    }
}
