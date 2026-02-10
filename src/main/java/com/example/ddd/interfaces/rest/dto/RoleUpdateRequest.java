package com.example.ddd.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新角色请求 DTO
 *
 * @author DDD Demo
 */
@Data
public class RoleUpdateRequest {

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long id;

    /**
     * 角色编码
     */
    @Pattern(regexp = "^[A-Z_]+$", message = "角色编码只能包含大写字母和下划线")
    @Size(max = 50, message = "角色编码长度不能超过50")
    private String roleCode;

    /**
     * 角色名称
     */
    @Size(max = 100, message = "角色名称长度不能超过100")
    private String roleName;

    /**
     * 描述
     */
    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 0-禁用 1-启用
     */
    private Integer status;
}
