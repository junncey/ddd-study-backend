package com.example.ddd.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新权限请求 DTO
 *
 * @author DDD Demo
 */
@Data
public class PermissionUpdateRequest {

    /**
     * 权限ID
     */
    @NotNull(message = "权限ID不能为空")
    private Long id;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 权限编码
     */
    @Size(max = 100, message = "权限编码长度不能超过100")
    private String permissionCode;

    /**
     * 权限名称
     */
    @Size(max = 100, message = "权限名称长度不能超过100")
    private String permissionName;

    /**
     * 权限类型 1-菜单 2-按钮 3-接口
     */
    private Integer permissionType;

    /**
     * 路由路径
     */
    @Size(max = 200, message = "路由路径长度不能超过200")
    private String path;

    /**
     * 组件路径
     */
    @Size(max = 200, message = "组件路径长度不能超过200")
    private String component;

    /**
     * 图标
     */
    @Size(max = 50, message = "图标长度不能超过50")
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
}
