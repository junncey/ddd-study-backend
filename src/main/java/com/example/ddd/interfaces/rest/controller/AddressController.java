package com.example.ddd.interfaces.rest.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.ddd.application.service.AddressApplicationService;
import com.example.ddd.application.service.AuthorizationService;
import com.example.ddd.infrastructure.security.SecurityUtil;
import com.example.ddd.interfaces.rest.dto.AddressCreateRequest;
import com.example.ddd.interfaces.rest.dto.AddressResponse;
import com.example.ddd.interfaces.rest.dto.AddressUpdateRequest;
import com.example.ddd.interfaces.rest.vo.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器
 * 六边形架构的主适配器（Driving Adapter）
 *
 * @author DDD Demo
 */
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressApplicationService addressApplicationService;
    private final AuthorizationService authorizationService;

    /**
     * 创建地址
     *
     * @param request 创建请求
     * @return 地址响应
     */
    @PostMapping
    public Response<AddressResponse> create(@Valid @RequestBody AddressCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        AddressResponse response = addressApplicationService.createAddress(userId, request);
        return Response.success(response);
    }

    /**
     * 更新地址
     *
     * @param request 更新请求
     * @return 地址响应
     */
    @PutMapping
    public Response<AddressResponse> update(@Valid @RequestBody AddressUpdateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        // 验证地址归属
        authorizationService.checkAddressOwnership(request.getId());
        AddressResponse response = addressApplicationService.updateAddress(userId, request);
        return Response.success(response);
    }

    /**
     * 删除地址
     *
     * @param id 地址ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        // 验证地址归属
        authorizationService.checkAddressOwnership(id);
        addressApplicationService.deleteAddress(id, userId);
        return Response.success();
    }

    /**
     * 查询地址详情
     *
     * @param id 地址ID
     * @return 地址响应
     */
    @GetMapping("/{id}")
    public Response<AddressResponse> getById(@PathVariable Long id) {
        // 验证地址归属
        authorizationService.checkAddressOwnership(id);
        AddressResponse response = addressApplicationService.getAddressById(id);
        return Response.success(response);
    }

    /**
     * 查询我的所有地址
     *
     * @return 地址列表
     */
    @GetMapping("/my")
    public Response<List<AddressResponse>> getMyAddresses() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<AddressResponse> responses = addressApplicationService.getAddressesByUserId(userId);
        return Response.success(responses);
    }

    /**
     * 查询我的默认地址
     *
     * @return 地址响应
     */
    @GetMapping("/my/default")
    public Response<AddressResponse> getMyDefaultAddress() {
        Long userId = SecurityUtil.getCurrentUserId();
        AddressResponse response = addressApplicationService.getDefaultAddress(userId);
        return Response.success(response);
    }

    /**
     * 设置默认地址
     *
     * @param id 地址ID
     * @return 地址响应
     */
    @PutMapping("/{id}/default")
    public Response<AddressResponse> setDefault(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        // 验证地址归属
        authorizationService.checkAddressOwnership(id);
        AddressResponse response = addressApplicationService.setDefaultAddress(id, userId);
        return Response.success(response);
    }

    /**
     * 分页查询我的地址
     *
     * @param current 当前页
     * @param size    每页大小
     * @return 分页响应
     */
    @GetMapping("/page")
    public Response<IPage<AddressResponse>> pageMyAddresses(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = SecurityUtil.getCurrentUserId();
        IPage<AddressResponse> page = addressApplicationService.pageAddressesByUserId(current, size, userId);
        return Response.success(page);
    }
}
