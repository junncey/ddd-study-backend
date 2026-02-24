package com.example.ddd.infrastructure.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 存储配置属性
 *
 * @author DDD Demo
 */
@Data
@Component
@ConfigurationProperties(prefix = "file.storage")
public class StorageProperties {

    /**
     * 存储类型：LOCAL/OSS/COS/S3
     */
    private String type = "LOCAL";

    /**
     * 允许的文件类型（逗号分隔）
     */
    private String allowedTypes = "jpg,jpeg,png,gif,webp,bmp";

    /**
     * 最大文件大小（字节）
     */
    private Long maxSize = 10 * 1024 * 1024L; // 10MB

    /**
     * 待绑定文件过期时间（小时）- 已废弃，使用 pendingExpireDays
     */
    @Deprecated
    private Integer pendingExpireHours = 24;

    /**
     * 临时文件/已删除文件过期天数（默认3天）
     */
    private Integer pendingExpireDays = 3;

    /**
     * 已绑定业务文件过期天数（默认2年 = 730天）
     */
    private Integer boundExpireDays = 730;

    /**
     * 本地存储配置
     */
    private LocalConfig local = new LocalConfig();

    /**
     * 阿里云OSS配置
     */
    private OssConfig oss = new OssConfig();

    @Data
    public static class LocalConfig {
        /**
         * 本地存储路径
         */
        private String path = "uploads";

        /**
         * 访问URL前缀
         */
        private String urlPrefix = "http://localhost:8080/api/uploads";
    }

    @Data
    public static class OssConfig {
        /**
         * OSS endpoint
         */
        private String endpoint;

        /**
         * AccessKey ID
         */
        private String accessKeyId;

        /**
         * AccessKey Secret
         */
        private String accessKeySecret;

        /**
         * Bucket名称
         */
        private String bucketName;

        /**
         * 访问URL前缀
         */
        private String urlPrefix;
    }

    /**
     * 获取允许的文件类型列表
     */
    public List<String> getAllowedTypeList() {
        return Arrays.asList(allowedTypes.toLowerCase().split(","));
    }

    /**
     * 检查文件类型是否允许
     */
    public boolean isAllowedType(String extension) {
        if (extension == null) {
            return false;
        }
        return getAllowedTypeList().contains(extension.toLowerCase());
    }
}
