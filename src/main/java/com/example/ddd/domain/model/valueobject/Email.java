package com.example.ddd.domain.model.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 邮箱值对象
 * 封装邮箱格式验证逻辑
 *
 * @author DDD Demo
 */
public class Email extends BaseValueObject {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    /**
     * 创建邮箱值对象
     *
     * @param value 邮箱字符串
     * @return 邮箱值对象
     * @throws IllegalArgumentException 如果邮箱格式不正确
     */
    public static Email of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        String trimmed = value.trim();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("邮箱格式不正确: " + value);
        }
        return new Email(trimmed);
    }

    /**
     * 创建邮箱值对象（允许 null）
     *
     * @param value 邮箱字符串
     * @return 邮箱值对象，如果输入为 null 则返回 null
     */
    public static Email ofNullable(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return of(value);
    }

    /**
     * 获取邮箱值
     *
     * @return 邮箱字符串
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
