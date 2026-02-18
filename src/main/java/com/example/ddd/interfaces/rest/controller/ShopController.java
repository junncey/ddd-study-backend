package com.example.ddd.interfaces.rest.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.ddd.application.service.ShopApplicationService;
import com.example.ddd.interfaces.rest.dto.ShopApproveRequest;
import com.example.ddd.interfaces.rest.dto.ShopCreateRequest;
import com.example.ddd.interfaces.rest.dto.ShopResponse;
import com.example.ddd.interfaces.rest.dto.ShopUpdateRequest;
import com.example.ddd.interfaces.rest.vo.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 店铺控制器
 * 六边形架构的主适配器（Driving Adapter）
 *
 * @author DDD Demo
 */
@RestController
@RequestMapping("/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopApplicationService shopApplicationService;

    /**
     * 注册店铺
     *
     * @param request 创建请求
     * @return 店铺响应
     */
    @PostMapping
    public Response<ShopResponse> register(@Valid @RequestBody ShopCreateRequest request) {
        ShopResponse response = shopApplicationService.registerShop(request);
        return Response.success(response);
    }

    /**
     * 更新店铺
     *
     * @param request 更新请求
     * @return 店铺响应
     */
    @PutMapping
    public Response<ShopResponse> update(@Valid @RequestBody ShopUpdateRequest request) {
        ShopResponse response = shopApplicationService.updateShop(request);
        return Response.success(response);
    }

    /**
     * 审核店铺
     *
     * @param request 审核请求
     * @return 店铺响应
     */
    @PostMapping("/approve")
    public Response<ShopResponse> approve(@Valid @RequestBody ShopApproveRequest request) {
        ShopResponse response = shopApplicationService.approveShop(request);
        return Response.success(response);
    }

    /**
     * 暂停店铺
     *
     * @param id 店铺ID
     * @return 店铺响应
     */
    @PutMapping("/{id}/suspend")
    public Response<ShopResponse> suspend(@PathVariable Long id) {
        ShopResponse response = shopApplicationService.suspendShop(id);
        return Response.success(response);
    }

    /**
     * 恢复店铺
     *
     * @param id 店铺ID
     * @return 店铺响应
     */
    @PutMapping("/{id}/resume")
    public Response<ShopResponse> resume(@PathVariable Long id) {
        ShopResponse response = shopApplicationService.resumeShop(id);
        return Response.success(response);
    }

    /**
     * 关闭店铺
     *
     * @param id 店铺ID
     * @return 店铺响应
     */
    @PutMapping("/{id}/close")
    public Response<ShopResponse> close(@PathVariable Long id) {
        ShopResponse response = shopApplicationService.closeShop(id);
        return Response.success(response);
    }

    /**
     * 查询店铺详情
     *
     * @param id 店铺ID
     * @return 店铺响应
     */
    @GetMapping("/{id}")
    public Response<ShopResponse> getById(@PathVariable Long id) {
        ShopResponse response = shopApplicationService.getShopById(id);
        return Response.success(response);
    }

    /**
     * 根据店主ID查询店铺
     *
     * @param ownerId 店主ID
     * @return 店铺响应
     */
    @GetMapping("/owner/{ownerId}")
    public Response<ShopResponse> getByOwnerId(@PathVariable Long ownerId) {
        ShopResponse response = shopApplicationService.getShopByOwnerId(ownerId);
        return Response.success(response);
    }

    /**
     * 查询所有店铺
     *
     * @param status 状态（可选）
     * @return 店铺列表
     */
    @GetMapping
    public Response<List<ShopResponse>> getAll(@RequestParam(required = false) Integer status) {
        List<ShopResponse> responses = shopApplicationService.getAllShops(status);
        return Response.success(responses);
    }

    /**
     * 分页查询店铺
     *
     * @param current 当前页
     * @param size    每页大小
     * @param status  状态（可选）
     * @return 分页响应
     */
    @GetMapping("/page")
    public Response<IPage<ShopResponse>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status) {
        IPage<ShopResponse> page = shopApplicationService.pageShops(current, size, status);
        return Response.success(page);
    }
}
