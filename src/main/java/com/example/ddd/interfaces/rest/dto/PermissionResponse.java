package com.example.ddd.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限响应 DTO
 *
 * @author DDD Demo
 */
@Data
public class PermissionResponse implements Serializable {

    /**
     * 权限ID
     */
    private Long id;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限类型 1-菜单 2-按钮 3-接口
     */
    private Integer permissionType;

    /**
     * 权限类型描述
     */
    private String permissionTypeDesc;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否可见 0-隐藏 1-可见
     */
    private Integer visible;

    /**
     * 状态 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
