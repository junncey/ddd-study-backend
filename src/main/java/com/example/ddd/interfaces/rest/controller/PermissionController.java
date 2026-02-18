package com.example.ddd.interfaces.rest.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.ddd.application.service.PermissionApplicationService;
import com.example.ddd.domain.model.entity.Permission;
import com.example.ddd.interfaces.rest.dto.PermissionCreateRequest;
import com.example.ddd.interfaces.rest.dto.PermissionResponse;
import com.example.ddd.interfaces.rest.dto.PermissionUpdateRequest;
import com.example.ddd.interfaces.rest.vo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限控制器
 * 六边形架构的主适配器（Driving Adapter）
 * 所有权限管理操作仅限管理员
 *
 * @author DDD Demo
 */
@Tag(name = "权限管理", description = "权限相关接口（仅管理员）")
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {

    private final PermissionApplicationService permissionApplicationService;

    /**
     * 创建权限
     *
     * @param request 创建请求
     * @return 权限响应
     */
    @Operation(summary = "创建权限")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Response<PermissionResponse> create(@Valid @RequestBody PermissionCreateRequest request) {
        Permission permission = new Permission();
        BeanUtils.copyProperties(request, permission);

        Permission createdPermission = permissionApplicationService.createPermission(permission);
        return Response.success(toResponse(createdPermission));
    }

    /**
     * 更新权限
     *
     * @param request 更新请求
     * @return 权限响应
     */
    @Operation(summary = "更新权限")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public Response<PermissionResponse> update(@Valid @RequestBody PermissionUpdateRequest request) {
        Permission permission = new Permission();
        BeanUtils.copyProperties(request, permission);

        Permission updatedPermission = permissionApplicationService.updatePermission(permission);
        return Response.success(toResponse(updatedPermission));
    }

    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 成功响应
     */
    @Operation(summary = "删除权限")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        permissionApplicationService.deletePermission(id);
        return Response.success();
    }

    /**
     * 查询权限
     *
     * @param id 权限ID
     * @return 权限响应
     */
    @Operation(summary = "查询权限")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Response<PermissionResponse> getById(@PathVariable Long id) {
        Permission permission = permissionApplicationService.getPermissionById(id);
        return Response.success(toResponse(permission));
    }

    /**
     * 根据权限编码查询权限
     *
     * @param permissionCode 权限编码
     * @return 权限响应
     */
    @Operation(summary = "根据权限编码查询权限")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/code/{permissionCode}")
    public Response<PermissionResponse> getByPermissionCode(@PathVariable String permissionCode) {
        Permission permission = permissionApplicationService.getPermissionByPermissionCode(permissionCode);
        return Response.success(toResponse(permission));
    }

    /**
     * 分页查询权限
     *
     * @param current 当前页
     * @param size    每页大小
     * @return 分页响应
     */
    @Operation(summary = "分页查询权限")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/page")
    public Response<IPage<PermissionResponse>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        IPage<Permission> page = permissionApplicationService.pagePermissions(current, size);

        // 转换为响应DTO
        IPage<PermissionResponse> responsePage = page.convert(this::toResponse);
        return Response.success(responsePage);
    }

    /**
     * 查询权限树
     *
     * @return 权限树响应
     */
    @Operation(summary = "查询权限树")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tree")
    public Response<List<PermissionResponse>> tree() {
        List<Permission> tree = permissionApplicationService.getPermissionTree();
        List<PermissionResponse> responseList = tree.stream()
                .map(this::toResponse)
                .toList();
        return Response.success(responseList);
    }

    /**
     * 根据父权限ID查询子权限
     *
     * @param parentId 父权限ID
     * @return 子权限列表
     */
    @Operation(summary = "根据父权限ID查询子权限")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/children/{parentId}")
    public Response<List<PermissionResponse>> getChildrenByParentId(@PathVariable Long parentId) {
        List<Permission> permissions = permissionApplicationService.getPermissionsByParentId(parentId);
        List<PermissionResponse> responseList = permissions.stream()
                .map(this::toResponse)
                .toList();
        return Response.success(responseList);
    }

    /**
     * 根据权限类型查询权限
     *
     * @param permissionType 权限类型
     * @return 权限列表
     */
    @Operation(summary = "根据权限类型查询权限")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/type/{permissionType}")
    public Response<List<PermissionResponse>> getByType(@PathVariable Integer permissionType) {
        List<Permission> permissions = permissionApplicationService.getPermissionsByType(permissionType);
        List<PermissionResponse> responseList = permissions.stream()
                .map(this::toResponse)
                .toList();
        return Response.success(responseList);
    }

    /**
     * 转换为响应DTO
     *
     * @param permission 权限实体
     * @return 响应DTO
     */
    private PermissionResponse toResponse(Permission permission) {
        PermissionResponse response = new PermissionResponse();
        BeanUtils.copyProperties(permission, response);
        // 设置权限类型描述
        response.setPermissionTypeDesc(getPermissionTypeDesc(permission.getPermissionType()));
        return response;
    }

    /**
     * 获取权限类型描述
     *
     * @param permissionType 权限类型
     * @return 类型描述
     */
    private String getPermissionTypeDesc(Integer permissionType) {
        if (permissionType == null) {
            return "";
        }
        return switch (permissionType) {
            case 1 -> "菜单";
            case 2 -> "按钮";
            case 3 -> "接口";
            default -> "";
        };
    }
}
