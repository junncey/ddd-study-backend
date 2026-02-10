package com.example.ddd.infrastructure.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户代理解析工具类
 *
 * @author DDD Demo
 */
@UtilityClass
public class UserAgentUtil {

    /**
     * 获取浏览器类型
     *
     * @param userAgent User-Agent 字符串
     * @return 浏览器类型
     */
    public String getBrowser(String userAgent) {
        if (userAgent == null) {
            return "未知";
        }

        if (userAgent.contains("Chrome") && !userAgent.contains("Edg")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            return "Safari";
        } else if (userAgent.contains("Edg")) {
            return "Edge";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "IE";
        } else {
            return "未知";
        }
    }

    /**
     * 获取操作系统
     *
     * @param userAgent User-Agent 字符串
     * @return 操作系统
     */
    public String getOs(String userAgent) {
        if (userAgent == null) {
            return "未知";
        }

        if (userAgent.contains("Windows")) {
            return "Windows";
        } else if (userAgent.contains("Mac")) {
            return "Mac";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS";
        } else {
            return "未知";
        }
    }

    /**
     * 从 IP 地址获取地理位置（简化版，实际应使用第三方 API）
     *
     * @param ip IP 地址
     * @return 地理位置
     */
    public String getLocation(String ip) {
        // 简化处理，实际应使用第三方 IP 定位服务
        if (ip == null || "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            return "本地";
        }
        return "未知";
    }
}
