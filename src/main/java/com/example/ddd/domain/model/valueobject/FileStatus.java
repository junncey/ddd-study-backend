package com.example.ddd.domain.model.valueobject;

/**
 * 文件状态枚举
 *
 * @author DDD Demo
 */
public enum FileStatus implements StatusType {

    /**
     * 待绑定（临时文件）
     */
    PENDING(0, "待绑定"),

    /**
     * 已绑定
     */
    BOUND(1, "已绑定"),

    /**
     * 已删除
     */
    DELETED(2, "已删除");

    private final Integer value;
    private final String description;

    FileStatus(Integer value, String description) {
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
     * @return 文件状态枚举
     */
    public static FileStatus fromValue(Integer value) {
        if (value == null) {
            return PENDING;
        }
        for (FileStatus status : FileStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的文件状态值: " + value);
    }
}
