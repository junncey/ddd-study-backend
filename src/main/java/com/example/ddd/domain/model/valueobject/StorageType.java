package com.example.ddd.domain.model.valueobject;

/**
 * 存储类型枚举
 * 支持本地存储和云存储的切换
 *
 * @author DDD Demo
 */
public enum StorageType implements StatusType {

    /**
     * 本地存储
     */
    LOCAL(0, "本地存储"),

    /**
     * 阿里云OSS
     */
    OSS(1, "阿里云OSS"),

    /**
     * 腾讯云COS
     */
    COS(2, "腾讯云COS"),

    /**
     * AWS S3
     */
    S3(3, "AWS S3");

    private final Integer value;
    private final String description;

    StorageType(Integer value, String description) {
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
     * @return 存储类型枚举
     */
    public static StorageType fromValue(Integer value) {
        if (value == null) {
            return LOCAL;
        }
        for (StorageType type : StorageType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的存储类型值: " + value);
    }

    /**
     * 根据名称获取枚举（不区分大小写）
     *
     * @param name 存储类型名称
     * @return 存储类型枚举
     */
    public static StorageType fromName(String name) {
        if (name == null || name.isEmpty()) {
            return LOCAL;
        }
        return StorageType.valueOf(name.toUpperCase());
    }
}
