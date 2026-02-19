-- =====================================================
-- 文件存储表
-- V1.0.2__file_storage_tables.sql
-- =====================================================

-- 文件元数据表
CREATE TABLE IF NOT EXISTS t_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    file_key VARCHAR(64) NOT NULL COMMENT '文件唯一标识（UUID）',
    file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    storage_path VARCHAR(500) NOT NULL COMMENT '存储路径（相对路径）',
    storage_type TINYINT NOT NULL DEFAULT 0 COMMENT '存储类型：0-LOCAL 1-OSS 2-COS 3-S3',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    content_type VARCHAR(100) COMMENT 'MIME类型',
    file_hash VARCHAR(64) COMMENT '文件MD5哈希（用于去重）',
    access_url VARCHAR(500) COMMENT '访问URL',
    biz_type VARCHAR(50) COMMENT '业务类型：product_image/avatar/shop_logo等',
    biz_id BIGINT COMMENT '关联的业务ID',
    uploader_id BIGINT COMMENT '上传者用户ID',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待绑定 1-已绑定 2-已删除',
    expire_time DATETIME COMMENT '过期时间（待绑定文件自动清理）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(64) COMMENT '创建人',
    update_by VARCHAR(64) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',

    UNIQUE KEY uk_file_key (file_key),
    INDEX idx_biz (biz_type, biz_id),
    INDEX idx_uploader (uploader_id),
    INDEX idx_expire (expire_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件元数据表';

-- 创建上传目录（本地存储）
-- 注意：这个目录需要手动创建或由应用程序自动创建
-- Windows: mkdir uploads
-- Linux/Mac: mkdir -p uploads
