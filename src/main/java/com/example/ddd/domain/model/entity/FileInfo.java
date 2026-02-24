package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ddd.domain.model.valueobject.BizType;
import com.example.ddd.domain.model.valueobject.FileStatus;
import com.example.ddd.domain.model.valueobject.StorageType;
import com.example.ddd.infrastructure.persistence.handler.BizTypeHandler;
import com.example.ddd.infrastructure.persistence.handler.FileStatusHandler;
import com.example.ddd.infrastructure.persistence.handler.StorageTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件信息实体
 * 统一管理所有上传文件的元数据
 *
 * @author DDD Demo
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_file", autoResultMap = true)
public class FileInfo extends BaseEntity {

    /**
     * 文件唯一标识（UUID）
     */
    private String fileKey;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 存储路径（相对路径）
     */
    private String storagePath;

    /**
     * 存储类型
     */
    @TableField(typeHandler = StorageTypeHandler.class)
    private StorageType storageType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * MIME类型
     */
    private String contentType;

    /**
     * 文件MD5哈希（用于去重）
     */
    private String fileHash;

    /**
     * 访问URL
     */
    private String accessUrl;

    /**
     * 业务类型
     */
    @TableField(typeHandler = BizTypeHandler.class)
    private BizType bizType;

    /**
     * 业务ID
     */
    private Long bizId;

    /**
     * 上传者ID
     */
    private Long uploaderId;

    /**
     * 文件状态
     */
    @TableField(typeHandler = FileStatusHandler.class)
    private FileStatus status;

    /**
     * 过期时间（临时文件自动清理）
     */
    private LocalDateTime expireTime;

    /**
     * 绑定业务
     *
     * @param bizType    业务类型
     * @param bizId      业务ID
     * @param expireDays 过期天数（已绑定文件）
     */
    public void bindBusiness(BizType bizType, Long bizId, int expireDays) {
        this.bizType = bizType;
        this.bizId = bizId;
        this.status = FileStatus.BOUND;
        this.expireTime = LocalDateTime.now().plusDays(expireDays);
    }

    /**
     * 绑定业务（使用默认2年过期）
     *
     * @param bizType 业务类型
     * @param bizId   业务ID
     */
    public void bindBusiness(BizType bizType, Long bizId) {
        bindBusiness(bizType, bizId, 730);
    }

    /**
     * 标记删除
     *
     * @param expireDays 过期天数
     */
    public void markDeleted(int expireDays) {
        this.status = FileStatus.DELETED;
        this.expireTime = LocalDateTime.now().plusDays(expireDays);
    }

    /**
     * 标记删除（使用默认3天过期）
     */
    public void markDeleted() {
        markDeleted(3);
    }

    /**
     * 是否已过期
     *
     * @return true 如果已过期
     */
    public boolean isExpired() {
        return expireTime != null && expireTime.isBefore(LocalDateTime.now());
    }

    /**
     * 是否待绑定状态
     *
     * @return true 如果是待绑定状态
     */
    public boolean isPending() {
        return status == FileStatus.PENDING;
    }

    /**
     * 是否已绑定
     *
     * @return true 如果已绑定
     */
    public boolean isBound() {
        return status == FileStatus.BOUND;
    }
}
