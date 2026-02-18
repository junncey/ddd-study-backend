package com.example.ddd.interfaces.rest.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.ddd.application.service.AuthorizationService;
import com.example.ddd.application.service.OrderApplicationService;
import com.example.ddd.domain.model.entity.Order;
import com.example.ddd.infrastructure.security.SecurityUtil;
import com.example.ddd.interfaces.rest.dto.OrderCreateRequest;
import com.example.ddd.interfaces.rest.vo.OrderDetailVO;
import com.example.ddd.interfaces.rest.vo.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 *
 * @author DDD Demo
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderApplicationService orderApplicationService;
    private final AuthorizationService authorizationService;

    /**
     * 创建订单
     */
    @PostMapping
    public Response<Order> create(@RequestBody OrderCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderApplicationService.createOrderFromRequest(userId, request);
        return Response.success(order);
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public Response<Order> cancel(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        // 验证订单归属
        authorizationService.checkOrderOwnership(id);
        Order order = orderApplicationService.cancelOrder(id, userId);
        return Response.success(order);
    }

    /**
     * 发货（商家操作）
     */
    @PutMapping("/{id}/ship")
    public Response<Order> ship(@PathVariable Long id) {
        // 验证商家权限
        authorizationService.checkOrderMerchantAccess(id);
        Order order = orderApplicationService.shipOrder(id);
        return Response.success(order);
    }

    /**
     * 确认收货
     */
    @PutMapping("/{id}/confirm")
    public Response<Order> confirm(@PathVariable Long id) {
        // 验证订单归属
        authorizationService.checkOrderOwnership(id);
        Order order = orderApplicationService.completeOrder(id);
        return Response.success(order);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Response<OrderDetailVO> getById(@PathVariable Long id) {
        // 验证订单归属（买家或商家都可以查看）
        Long currentUserId = SecurityUtil.getCurrentUserId();
        OrderDetailVO orderDetail = orderApplicationService.getOrderDetailById(id);
        if (orderDetail == null) {
            return Response.fail(404, "订单不存在");
        }
        // 检查是否是买家或商家
        if (!orderDetail.getUserId().equals(currentUserId)) {
            // 如果不是买家，检查是否是商家
            try {
                authorizationService.checkOrderMerchantAccess(id);
            } catch (Exception e) {
                return Response.fail(403, "无权访问该订单");
            }
        }
        return Response.success(orderDetail);
    }

    /**
     * 根据订单号获取订单
     */
    @GetMapping("/no/{orderNo}")
    public Response<Order> getByOrderNo(@PathVariable String orderNo) {
        // 验证订单归属
        authorizationService.checkOrderOwnershipByOrderNo(orderNo);
        Order order = orderApplicationService.getOrderByOrderNo(orderNo);
        return Response.success(order);
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/my")
    public Response<IPage<Order>> getMyOrders(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status) {
        Long userId = SecurityUtil.getCurrentUserId();
        IPage<Order> page = orderApplicationService.pageOrdersByUserId(current, size, userId, status);
        return Response.success(page);
    }

    /**
     * 获取店铺订单列表（商家查看）
     */
    @GetMapping("/shop/{shopId}")
    public Response<IPage<Order>> getShopOrders(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status,
            @PathVariable Long shopId) {
        // 验证店铺归属
        authorizationService.checkShopOwnership(shopId);
        IPage<Order> page = orderApplicationService.pageOrdersByShopId(current, size, shopId, status);
        return Response.success(page);
    }
}
