package com.example.ddd.interfaces.rest.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.ddd.application.service.UserApplicationService;
import com.example.ddd.domain.model.entity.User;
import com.example.ddd.domain.model.valueobject.Status;
import com.example.ddd.domain.model.valueobject.UserStatus;
import com.example.ddd.interfaces.rest.converter.UserConverter;
import com.example.ddd.interfaces.rest.dto.UserCreateRequest;
import com.example.ddd.interfaces.rest.dto.UserResponse;
import com.example.ddd.interfaces.rest.dto.UserUpdateRequest;
import com.example.ddd.interfaces.rest.vo.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 六边形架构的主适配器（Driving Adapter）
 *
 * @author DDD Demo
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;
    private final UserConverter userConverter;

    /**
     * 创建用户
     *
     * @param request 创建请求
     * @return 用户响应
     */
    @PostMapping
    public Response<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        User user = userConverter.toEntity(request);
        // 设置默认状态为启用
        if (user.getStatus() == null) {
            user.setStatus(Status.ofUser(UserStatus.ENABLED));
        }

        User createdUser = userApplicationService.createUser(user);
        return Response.success(userConverter.toResponse(createdUser));
    }

    /**
     * 更新用户
     *
     * @param request 更新请求
     * @return 用户响应
     */
    @PutMapping
    public Response<UserResponse> update(@Valid @RequestBody UserUpdateRequest request) {
        User user = userConverter.toEntity(request);

        User updatedUser = userApplicationService.updateUser(user);
        return Response.success(userConverter.toResponse(updatedUser));
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        userApplicationService.deleteUser(id);
        return Response.success();
    }

    /**
     * 查询用户
     *
     * @param id 用户ID
     * @return 用户响应
     */
    @GetMapping("/{id}")
    public Response<UserResponse> getById(@PathVariable Long id) {
        User user = userApplicationService.getUserById(id);
        return Response.success(userConverter.toResponse(user));
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户响应
     */
    @GetMapping("/username/{username}")
    public Response<UserResponse> getByUsername(@PathVariable String username) {
        User user = userApplicationService.getUserByUsername(username);
        return Response.success(userConverter.toResponse(user));
    }

    /**
     * 分页查询用户
     *
     * @param current 当前页
     * @param size    每页大小
     * @return 分页响应
     */
    @GetMapping("/page")
    public Response<IPage<UserResponse>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        IPage<User> page = userApplicationService.pageUsers(current, size);

        // 转换为响应DTO
        IPage<UserResponse> responsePage = page.convert(userConverter::toResponse);
        return Response.success(responsePage);
    }
}
