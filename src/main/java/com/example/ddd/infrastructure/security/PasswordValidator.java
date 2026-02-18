package com.example.ddd.infrastructure.security;

import java.util.regex.Pattern;

/**
 * 密码强度验证工具
 * 用于验证密码是否符合安全要求
 *
 * @author DDD Demo
 */
public final class PasswordValidator {

    private PasswordValidator() {
        // 私有构造函数，防止实例化
    }

    /**
     * 最小密码长度
     */
    public static final int MIN_LENGTH = 8;

    /**
     * 最大密码长度
     */
    public static final int MAX_LENGTH = 128;

    /**
     * 密码必须包含：大写字母
     */
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");

    /**
     * 密码必须包含：小写字母
     */
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");

    /**
     * 密码必须包含：数字
     */
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");

    /**
     * 密码必须包含：特殊字符
     */
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

    /**
     * 常见弱密码列表（部分）
     */
    private static final String[] WEAK_PASSWORDS = {
            "password", "123456", "12345678", "qwerty", "abc123",
            "monkey", "master", "dragon", "111111", "baseball",
            "iloveyou", "trustno1", "sunshine", "princess", "welcome",
            "shadow", "superman", "michael", "football", "password1",
            "password123", "admin", "letmein", "login", "starwars"
    };

    /**
     * 验证密码强度
     *
     * @param password 待验证的密码
     * @return 验证结果
     */
    public static ValidationResult validate(String password) {
        if (password == null || password.isEmpty()) {
            return ValidationResult.failure("密码不能为空");
        }

        if (password.length() < MIN_LENGTH) {
            return ValidationResult.failure("密码长度不能少于 " + MIN_LENGTH + " 个字符");
        }

        if (password.length() > MAX_LENGTH) {
            return ValidationResult.failure("密码长度不能超过 " + MAX_LENGTH + " 个字符");
        }

        int strengthScore = 0;
        StringBuilder suggestions = new StringBuilder();

        // 检查大写字母
        if (UPPERCASE_PATTERN.matcher(password).matches()) {
            strengthScore++;
        } else {
            suggestions.append("包含大写字母; ");
        }

        // 检查小写字母
        if (LOWERCASE_PATTERN.matcher(password).matches()) {
            strengthScore++;
        } else {
            suggestions.append("包含小写字母; ");
        }

        // 检查数字
        if (DIGIT_PATTERN.matcher(password).matches()) {
            strengthScore++;
        } else {
            suggestions.append("包含数字; ");
        }

        // 检查特殊字符
        if (SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            strengthScore++;
        } else {
            suggestions.append("包含特殊字符(!@#$%^&*等); ");
        }

        // 检查是否为常见弱密码
        String lowerPassword = password.toLowerCase();
        for (String weakPassword : WEAK_PASSWORDS) {
            if (lowerPassword.contains(weakPassword)) {
                return ValidationResult.failure("密码过于简单，请使用更复杂的密码");
            }
        }

        // 检查连续字符
        if (hasConsecutiveChars(password, 4)) {
            return ValidationResult.failure("密码不能包含4个以上连续字符");
        }

        // 检查重复字符
        if (hasRepeatingChars(password, 4)) {
            return ValidationResult.failure("密码不能包含4个以上重复字符");
        }

        // 至少需要满足3个复杂度要求
        if (strengthScore < 3) {
            String message = "密码强度不足，请确保密码至少满足以下3个条件: " + suggestions.toString();
            return ValidationResult.failure(message.trim());
        }

        return ValidationResult.success();
    }

    /**
     * 检查是否包含连续字符
     */
    private static boolean hasConsecutiveChars(String password, int count) {
        for (int i = 0; i <= password.length() - count; i++) {
            boolean consecutive = true;
            for (int j = 0; j < count - 1; j++) {
                if (password.charAt(i + j) + 1 != password.charAt(i + j + 1)) {
                    consecutive = false;
                    break;
                }
            }
            if (consecutive) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否包含重复字符
     */
    private static boolean hasRepeatingChars(String password, int count) {
        for (int i = 0; i <= password.length() - count; i++) {
            boolean repeating = true;
            char firstChar = password.charAt(i);
            for (int j = 1; j < count; j++) {
                if (password.charAt(i + j) != firstChar) {
                    repeating = false;
                    break;
                }
            }
            if (repeating) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, "密码验证通过");
        }

        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
