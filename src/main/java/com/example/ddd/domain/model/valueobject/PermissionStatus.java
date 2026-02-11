package com.example.ddd.domain.model.valueobject;

/**
 * 权限状态枚举
 *
 * @author DDD Demo
 */
public enum PermissionStatus implements StatusType {

    /**
     * 禁用
     */
    DISABLED(0, "禁用"),

    /**
     * 启用
     */
    ENABLED(1, "启用");

    private final Integer value;
    private final String description;

    PermissionStatus(Integer value, String description) {
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
     * @return 权限状态枚举
     */
    public static PermissionStatus fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (PermissionStatus status : PermissionStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的权限状态值: " + value);
    }
}
