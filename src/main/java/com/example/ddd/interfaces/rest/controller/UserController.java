package com.example.ddd.interfaces.rest.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.ddd.application.service.UserApplicationService;
import com.example.ddd.domain.model.entity.User;
import com.example.ddd.interfaces.rest.dto.UserCreateRequest;
import com.example.ddd.interfaces.rest.dto.UserResponse;
import com.example.ddd.interfaces.rest.dto.UserUpdateRequest;
import com.example.ddd.interfaces.rest.vo.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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

    /**
     * 创建用户
     *
     * @param request 创建请求
     * @return 用户响应
     */
    @PostMapping
    public Response<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setStatus(1); // 默认启用

        User createdUser = userApplicationService.createUser(user);
        return Response.success(toResponse(createdUser));
    }

    /**
     * 更新用户
     *
     * @param request 更新请求
     * @return 用户响应
     */
    @PutMapping
    public Response<UserResponse> update(@Valid @RequestBody UserUpdateRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);

        User updatedUser = userApplicationService.updateUser(user);
        return Response.success(toResponse(updatedUser));
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
        return Response.success(toResponse(user));
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
        return Response.success(toResponse(user));
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
        IPage<UserResponse> responsePage = page.convert(this::toResponse);
        return Response.success(responsePage);
    }

    /**
     * 转换为响应DTO
     *
     * @param user 用户实体
     * @return 响应DTO
     */
    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}
