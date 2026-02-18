package com.example.ddd.infrastructure.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 安全响应头配置
 * 添加各种安全相关的HTTP响应头
 *
 * @author DDD Demo
 */
@Configuration
public class SecurityHeadersConfig {

    /**
     * 安全响应头过滤器
     */
    @Bean
    public FilterRegistrationBean<SecurityHeadersFilter> securityHeadersFilter() {
        FilterRegistrationBean<SecurityHeadersFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SecurityHeadersFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        registration.setName("securityHeadersFilter");
        return registration;
    }

    /**
     * 安全响应头过滤器实现
     */
    public static class SecurityHeadersFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {

            // 防止点击劫持
            response.setHeader("X-Frame-Options", "DENY");

            // 防止MIME类型嗅探
            response.setHeader("X-Content-Type-Options", "nosniff");

            // XSS保护（旧浏览器兼容）
            response.setHeader("X-XSS-Protection", "1; mode=block");

            // 引用策略
            response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

            // 权限策略（限制浏览器功能）
            response.setHeader("Permissions-Policy",
                    "geolocation=(), microphone=(), camera=(), payment=()");

            // Content Security Policy（基础配置，可根据需要调整）
            // 注意：CSP配置需要根据实际业务需求调整
            response.setHeader("Content-Security-Policy",
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                    "style-src 'self' 'unsafe-inline'; " +
                    "img-src 'self' data: https:; " +
                    "font-src 'self'; " +
                    "connect-src 'self'; " +
                    "frame-ancestors 'none';");

            // Cache-Control（对于API响应，通常不缓存）
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            filterChain.doFilter(request, response);
        }
    }
}
