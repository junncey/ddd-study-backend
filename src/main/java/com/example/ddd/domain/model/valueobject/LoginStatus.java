package com.example.ddd.domain.model.valueobject;

/**
 * 登录状态枚举
 *
 * @author DDD Demo
 */
public enum LoginStatus implements StatusType {

    /**
     * 失败
     */
    FAILED(0, "失败"),

    /**
     * 成功
     */
    SUCCESS(1, "成功");

    private final Integer value;
    private final String description;

    LoginStatus(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * 根据值获取枚举
     *
     * @param value 状态值
     * @return 登录状态枚举
     */
    public static LoginStatus fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (LoginStatus status : LoginStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的登录状态值: " + value);
    }
}
