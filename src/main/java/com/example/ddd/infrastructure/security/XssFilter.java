package com.example.ddd.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * XSS过滤器配置
 * 对所有请求参数进行XSS清洗
 *
 * @author DDD Demo
 */
@Configuration
public class XssFilter {

    /**
     * 注册XSS过滤器
     */
    @Bean
    public FilterRegistrationBean<XssFilterImpl> xssFilterRegistration() {
        FilterRegistrationBean<XssFilterImpl> registration = new FilterRegistrationBean<>();
        registration.setFilter(new XssFilterImpl());
        registration.addUrlPatterns("/*");
        registration.setOrder(2);
        registration.setName("xssFilter");
        return registration;
    }

    /**
     * XSS过滤器实现
     */
    public static class XssFilterImpl extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {

            // 创建包装后的请求对象
            XssHttpServletRequestWrapper wrappedRequest = new XssHttpServletRequestWrapper(request);

            filterChain.doFilter(wrappedRequest, response);
        }
    }

    /**
     * HttpServletRequest包装器，用于清洗参数
     */
    public static class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

        public XssHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return sanitizeIfNeeded(value);
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) {
                return null;
            }

            String[] sanitizedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                sanitizedValues[i] = sanitizeIfNeeded(values[i]);
            }
            return sanitizedValues;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> originalMap = super.getParameterMap();
            Map<String, String[]> sanitizedMap = new HashMap<>();

            for (Map.Entry<String, String[]> entry : originalMap.entrySet()) {
                String[] values = entry.getValue();
                String[] sanitizedValues = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    sanitizedValues[i] = sanitizeIfNeeded(values[i]);
                }
                sanitizedMap.put(entry.getKey(), sanitizedValues);
            }

            return sanitizedMap;
        }

        @Override
        public String getHeader(String name) {
            String value = super.getHeader(name);
            return sanitizeIfNeeded(value);
        }

        /**
         * 根据需要清洗字符串
         * 对于JSON请求体，不进行清洗（由Jackson反序列化时处理）
         */
        private String sanitizeIfNeeded(String value) {
            if (value == null || value.isEmpty()) {
                return value;
            }

            // 检测是否包含XSS模式
            if (XssSanitizer.containsXssPattern(value)) {
                // 记录潜在攻击（实际生产环境应该记录日志）
                return XssSanitizer.sanitize(value);
            }

            return value;
        }
    }
}
