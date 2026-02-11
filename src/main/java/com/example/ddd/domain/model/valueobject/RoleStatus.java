package com.example.ddd.domain.model.valueobject;

/**
 * 角色状态枚举
 *
 * @author DDD Demo
 */
public enum RoleStatus implements StatusType {

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

    RoleStatus(Integer value, String description) {
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
     * @return 角色状态枚举
     */
    public static RoleStatus fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (RoleStatus status : RoleStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的角色状态值: " + value);
    }
}
