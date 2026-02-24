package com.example.ddd.infrastructure.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * JSON请求体XSS过滤器
 * 对POST/PUT请求中的JSON body进行XSS清洗
 *
 * @author DDD Demo
 */
@Slf4j
@Configuration
public class JsonXssFilter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public FilterRegistrationBean<JsonXssFilterImpl> jsonXssFilterRegistration() {
        FilterRegistrationBean<JsonXssFilterImpl> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JsonXssFilterImpl());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);  // 必须在XssFilter之前执行
        registration.setName("jsonXssFilter");
        return registration;
    }

    /**
     * JSON XSS过滤器实现
     */
    public static class JsonXssFilterImpl extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {

            // 只处理JSON类型的请求
            String contentType = request.getContentType();
            if (contentType != null && contentType.contains("application/json")) {
                // 包装请求，处理JSON body
                JsonXssRequestWrapper wrappedRequest = new JsonXssRequestWrapper(request);
                filterChain.doFilter(wrappedRequest, response);
            } else {
                // 非JSON请求直接通过（由XssFilter处理URL参数）
                filterChain.doFilter(request, response);
            }
        }
    }

    /**
     * JSON请求包装器
     * 读取并清洗JSON请求体中的XSS危险字符
     */
    public static class JsonXssRequestWrapper extends jakarta.servlet.http.HttpServletRequestWrapper {

        private final byte[] body;

        public JsonXssRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);

            // 读取请求体（使用 ServletInputStream 而不是 Reader，避免编码问题）
            byte[] rawBody;
            try (var inputStream = request.getInputStream()) {
                rawBody = inputStream.readAllBytes();
            } catch (Exception e) {
                log.warn("读取请求体失败: {}", e.getMessage());
                rawBody = new byte[0];
            }

            String jsonBody = new String(rawBody, StandardCharsets.UTF_8);

            // 清洗JSON中的XSS
            if (!jsonBody.isBlank()) {
                jsonBody = sanitizeJsonBody(jsonBody);
            }

            this.body = jsonBody.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(this.body), StandardCharsets.UTF_8));
        }

        @Override
        public jakarta.servlet.ServletInputStream getInputStream() {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.body);
            return new jakarta.servlet.ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return byteArrayInputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(jakarta.servlet.ReadListener listener) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int read() {
                    return byteArrayInputStream.read();
                }
            };
        }

        /**
         * 清洗JSON字符串中的XSS
         */
        private String sanitizeJsonBody(String json) {
            try {
                JsonNode rootNode = objectMapper.readTree(json);
                JsonNode sanitizedNode = sanitizeJsonNode(rootNode);
                return objectMapper.writeValueAsString(sanitizedNode);
            } catch (JsonProcessingException e) {
                // JSON解析失败，可能是非标准JSON或原始表单数据
                // 记录日志并返回原始内容（保守策略）
                log.debug("JSON解析失败，跳过XSS清洗: {}", e.getMessage());
                return json;
            }
        }

        /**
         * 递归清洗JSON节点
         */
        private JsonNode sanitizeJsonNode(JsonNode node) {
            if (node.isObject()) {
                ObjectNode objectNode = (ObjectNode) node.deepCopy();
                Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    JsonNode sanitizedValue = sanitizeJsonNode(entry.getValue());
                    objectNode.set(entry.getKey(), sanitizedValue);
                }
                return objectNode;
            } else if (node.isArray()) {
                ArrayNode arrayNode = objectMapper.createArrayNode();
                for (JsonNode element : node) {
                    arrayNode.add(sanitizeJsonNode(element));
                }
                return arrayNode;
            } else if (node.isTextual()) {
                String text = node.asText();
                // 检测并清洗XSS
                if (XssSanitizer.containsXssPattern(text)) {
                    String sanitized = XssSanitizer.sanitize(text);
                    log.debug("检测到XSS模式，已清洗: {} -> {}", text.substring(0, Math.min(50, text.length())), sanitized.substring(0, Math.min(50, sanitized.length())));
                    return TextNode.valueOf(sanitized);
                }
                return node;
            } else {
                // 数字、布尔、null等类型不需要清洗
                return node;
            }
        }
    }
}
