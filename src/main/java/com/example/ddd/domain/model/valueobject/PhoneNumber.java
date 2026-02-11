package com.example.ddd.domain.model.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 手机号值对象
 * 封装手机号格式验证逻辑
 *
 * @author DDD Demo
 */
public class PhoneNumber extends BaseValueObject {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    private final String value;

    private PhoneNumber(String value) {
        this.value = value;
    }

    /**
     * 创建手机号值对象
     *
     * @param value 手机号字符串
     * @return 手机号值对象
     * @throws IllegalArgumentException 如果手机号格式不正确
     */
    public static PhoneNumber of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        String trimmed = value.trim();
        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("手机号格式不正确: " + value);
        }
        return new PhoneNumber(trimmed);
    }

    /**
     * 创建手机号值对象（允许 null）
     *
     * @param value 手机号字符串
     * @return 手机号值对象，如果输入为 null 或空字符串则返回 null
     */
    public static PhoneNumber ofNullable(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return of(value);
    }

    /**
     * 获取手机号值
     *
     * @return 手机号字符串
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
        PhoneNumber phoneNumber = (PhoneNumber) o;
        return Objects.equals(value, phoneNumber.value);
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
