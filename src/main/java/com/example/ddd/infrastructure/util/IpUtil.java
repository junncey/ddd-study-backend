package com.example.ddd.infrastructure.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP 地址工具类
 *
 * @author DDD Demo
 */
public class IpUtil {

    private static final String UNKNOWN = "unknown";
    private static final String IP_SEPARATOR = ",";
    private static final int IP_LENGTH = 15;

    /**
     * 获取客户端 IP 地址
     *
     * @param request HTTP 请求
     * @return IP 地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String ip = null;

        // 1. 检查 X-Forwarded-For
        ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            // 多次反向代理后会有多个 IP 值，第一个才是真实 IP
            int index = ip.indexOf(IP_SEPARATOR);
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }

        // 2. 检查 Proxy-Client-IP
        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        // 3. 检查 WL-Proxy-Client-IP
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        // 4. 检查 HTTP_CLIENT_IP
        ip = request.getHeader("HTTP_CLIENT_IP");
        if (isValidIp(ip)) {
            return ip;
        }

        // 5. 检查 HTTP_X_FORWARDED_FOR
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (isValidIp(ip)) {
            return ip;
        }

        // 6. 最后从 remote addr 获取
        ip = request.getRemoteAddr();

        return ip;
    }

    /**
     * 验证 IP 是否有效
     *
     * @param ip IP 地址
     * @return 是否有效
     */
    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip);
    }
}
