package com.example.ddd.infrastructure.security;

import org.springframework.util.StringUtils;

/**
 * XSS（跨站脚本攻击）防护工具类
 * 用于清洗和转义用户输入中的危险字符
 *
 * @author DDD Demo
 */
public final class XssSanitizer {

    private XssSanitizer() {
        // 私有构造函数，防止实例化
    }

    /**
     * 清洗字符串中的XSS危险字符
     *
     * @param value 原始字符串
     * @return 清洗后的字符串
     */
    public static String sanitize(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }

        StringBuilder result = new StringBuilder(value.length());

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                case '\'':
                    result.append("&#x27;");
                    break;
                case '/':
                    result.append("&#x2F;");
                    break;
                default:
                    result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * 清洗HTML属性值中的危险字符
     *
     * @param value 原始字符串
     * @return 清洗后的字符串
     */
    public static String sanitizeForHtmlAttribute(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }

        return value
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    /**
     * 清洗JavaScript字符串中的危险字符
     *
     * @param value 原始字符串
     * @return 清洗后的字符串
     */
    public static String sanitizeForJavaScript(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("<", "\\x3C")
                .replace(">", "\\x3E")
                .replace("&", "\\x26");
    }

    /**
     * 清洗URL中的危险字符
     *
     * @param value 原始字符串
     * @return 清洗后的字符串
     */
    public static String sanitizeForUrl(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }

        // 只允许合法的URL协议
        String lowerValue = value.toLowerCase().trim();
        if (lowerValue.startsWith("javascript:") ||
            lowerValue.startsWith("vbscript:") ||
            lowerValue.startsWith("data:text/html") ||
            lowerValue.startsWith("data:application")) {
            return "";
        }

        return value;
    }

    /**
     * 检测字符串中是否包含潜在的XSS攻击模式
     *
     * @param value 待检测的字符串
     * @return 是否包含危险模式
     */
    public static boolean containsXssPattern(String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }

        String lowerValue = value.toLowerCase();

        // 检测常见的XSS攻击模式
        String[] dangerousPatterns = {
                "<script", "</script>", "javascript:", "vbscript:",
                "onload=", "onerror=", "onclick=", "onmouseover=",
                "onfocus=", "onblur=", "onkeyup=", "onkeydown=",
                "onsubmit=", "onreset=", "onselect=", "onchange=",
                "ondblclick=", "oncontextmenu=", "onwheel=",
                "oncopy=", "oncut=", "onpaste=",
                "<iframe", "</iframe>", "<object", "</object>",
                "<embed", "<form", "</form>",
                "expression(", "eval(", "alert(", "prompt(",
                "document.cookie", "document.write",
                "window.location", "window.open"
        };

        for (String pattern : dangerousPatterns) {
            if (lowerValue.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 移除字符串中的HTML标签
     *
     * @param value 包含HTML的字符串
     * @return 纯文本字符串
     */
    public static String stripHtml(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }

        // 移除HTML标签
        return value.replaceAll("<[^>]*>", "");
    }
}
