-- 创建数据库
CREATE DATABASE IF NOT EXISTS `ddd_demo` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `ddd_demo`;

-- 用户表
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试数据（密码使用 BCrypt 加密）
-- admin123: $2a$10$YTGXvPt9TCyCjPJNyDgQX.XqZJXhHYdhi/KhxSKmAUqTvHm.KHV2O
-- test123: $2a$10$YTGXvPt9TCyCjPJNyDgQX.XqZJXhHYdhi/KhxSKmAUqTvHm.KHV2O
INSERT INTO `t_user` (`username`, `password`, `email`, `phone`, `nickname`, `status`) VALUES
('admin', '$2a$10$YTGXvPt9TCyCjPJNyDgQX.XqZJXhHYdhi/KhxSKmAUqTvHm.KHV2O', 'admin@example.com', '13800138000', '管理员', 1),
('test', '$2a$10$YTGXvPt9TCyCjPJNyDgQX.XqZJXhHYdhi/KhxSKmAUqTvHm.KHV2O', 'test@example.com', '13800138001', '测试用户', 1);

-- ========================================
-- 角色表
-- ========================================
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(200) COMMENT '描述',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 插入默认角色
INSERT INTO `t_role` (`role_code`, `role_name`, `description`, `sort`) VALUES
('ROLE_ADMIN', '管理员', '系统管理员角色', 1),
('ROLE_USER', '普通用户', '普通用户角色', 2);

-- ========================================
-- 用户角色关联表
-- ========================================
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 为管理员分配角色
INSERT INTO `t_user_role` (`user_id`, `role_id`)
SELECT u.id, r.id
FROM `t_user` u, `t_role` r
WHERE u.username = 'admin' AND r.role_code = 'ROLE_ADMIN';

-- ========================================
-- 权限表
-- ========================================
DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父权限ID',
    `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码',
    `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
    `permission_type` TINYINT NOT NULL DEFAULT 1 COMMENT '权限类型 1-菜单 2-按钮 3-接口',
    `path` VARCHAR(200) COMMENT '路由路径',
    `component` VARCHAR(200) COMMENT '组件路径',
    `icon` VARCHAR(100) COMMENT '图标',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `visible` TINYINT NOT NULL DEFAULT 1 COMMENT '是否可见 0-隐藏 1-可见',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 插入默认权限
INSERT INTO `t_permission` (`permission_code`, `permission_name`, `permission_type`, `sort`) VALUES
('user:list', '用户列表', 3, 1),
('user:create', '创建用户', 3, 2),
('user:update', '更新用户', 3, 3),
('user:delete', '删除用户', 3, 4),
('role:list', '角色列表', 3, 5),
('role:create', '创建角色', 3, 6),
('role:update', '更新角色', 3, 7),
('role:delete', '删除角色', 3, 8);

-- ========================================
-- 角色权限关联表
-- ========================================
DROP TABLE IF EXISTS `t_role_permission`;
CREATE TABLE `t_role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 为管理员角色分配所有权限
INSERT INTO `t_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `t_role` r, `t_permission` p
WHERE r.role_code = 'ROLE_ADMIN';

-- ========================================
-- 登录日志表
-- ========================================
DROP TABLE IF EXISTS `t_login_log`;
CREATE TABLE `t_login_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` BIGINT COMMENT '用户ID',
    `username` VARCHAR(50) COMMENT '用户名',
    `login_ip` VARCHAR(50) COMMENT '登录IP',
    `login_location` VARCHAR(100) COMMENT '登录地点',
    `browser` VARCHAR(50) COMMENT '浏览器类型',
    `os` VARCHAR(50) COMMENT '操作系统',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '登录状态 0-失败 1-成功',
    `message` VARCHAR(200) COMMENT '提示信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_username` (`username`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';
