package com.example.ddd.interfaces.rest.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.ddd.application.service.RoleApplicationService;
import com.example.ddd.domain.model.entity.Role;
import com.example.ddd.interfaces.rest.dto.RoleCreateRequest;
import com.example.ddd.interfaces.rest.dto.RoleResponse;
import com.example.ddd.interfaces.rest.dto.RoleUpdateRequest;
import com.example.ddd.interfaces.rest.vo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 角色控制器
 * 六边形架构的主适配器（Driving Adapter）
 *
 * @author DDD Demo
 */
@Tag(name = "角色管理", description = "角色相关接口")
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleApplicationService roleApplicationService;

    /**
     * 创建角色
     *
     * @param request 创建请求
     * @return 角色响应
     */
    @Operation(summary = "创建角色")
    @PostMapping
    public Response<RoleResponse> create(@Valid @RequestBody RoleCreateRequest request) {
        Role role = new Role();
        BeanUtils.copyProperties(request, role);

        Role createdRole = roleApplicationService.createRole(role);
        return Response.success(toResponse(createdRole));
    }

    /**
     * 更新角色
     *
     * @param request 更新请求
     * @return 角色响应
     */
    @Operation(summary = "更新角色")
    @PutMapping
    public Response<RoleResponse> update(@Valid @RequestBody RoleUpdateRequest request) {
        Role role = new Role();
        BeanUtils.copyProperties(request, role);

        Role updatedRole = roleApplicationService.updateRole(role);
        return Response.success(toResponse(updatedRole));
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 成功响应
     */
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        roleApplicationService.deleteRole(id);
        return Response.success();
    }

    /**
     * 查询角色
     *
     * @param id 角色ID
     * @return 角色响应
     */
    @Operation(summary = "查询角色")
    @GetMapping("/{id}")
    public Response<RoleResponse> getById(@PathVariable Long id) {
        Role role = roleApplicationService.getRoleById(id);
        return Response.success(toResponse(role));
    }

    /**
     * 根据角色编码查询角色
     *
     * @param roleCode 角色编码
     * @return 角色响应
     */
    @Operation(summary = "根据角色编码查询角色")
    @GetMapping("/code/{roleCode}")
    public Response<RoleResponse> getByRoleCode(@PathVariable String roleCode) {
        Role role = roleApplicationService.getRoleByRoleCode(roleCode);
        return Response.success(toResponse(role));
    }

    /**
     * 分页查询角色
     *
     * @param current 当前页
     * @param size    每页大小
     * @return 分页响应
     */
    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public Response<IPage<RoleResponse>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        IPage<Role> page = roleApplicationService.pageRoles(current, size);

        // 转换为响应DTO
        IPage<RoleResponse> responsePage = page.convert(this::toResponse);
        return Response.success(responsePage);
    }

    /**
     * 转换为响应DTO
     *
     * @param role 角色实体
     * @return 响应DTO
     */
    private RoleResponse toResponse(Role role) {
        RoleResponse response = new RoleResponse();
        BeanUtils.copyProperties(role, response);
        return response;
    }
}
