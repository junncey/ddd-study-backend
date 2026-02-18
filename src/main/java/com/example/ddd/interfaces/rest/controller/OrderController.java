package com.example.ddd.interfaces.rest.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.ddd.application.service.OrderApplicationService;
import com.example.ddd.domain.model.entity.Order;
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

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public Response<Order> cancel(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        Order order = orderApplicationService.cancelOrder(id, userId);
        return Response.success(order);
    }

    /**
     * 发货
     */
    @PutMapping("/{id}/ship")
    public Response<Order> ship(@PathVariable Long id) {
        Order order = orderApplicationService.shipOrder(id);
        return Response.success(order);
    }

    /**
     * 确认收货
     */
    @PutMapping("/{id}/confirm")
    public Response<Order> confirm(@PathVariable Long id) {
        Order order = orderApplicationService.completeOrder(id);
        return Response.success(order);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Response<Order> getById(@PathVariable Long id) {
        Order order = orderApplicationService.getOrderById(id);
        return Response.success(order);
    }

    /**
     * 根据订单号获取订单
     */
    @GetMapping("/no/{orderNo}")
    public Response<Order> getByOrderNo(@PathVariable String orderNo) {
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
            @RequestParam(required = false) Integer status,
            @RequestHeader("X-User-Id") Long userId) {
        IPage<Order> page = orderApplicationService.pageOrdersByUserId(current, size, userId, status);
        return Response.success(page);
    }

    /**
     * 获取店铺订单列表
     */
    @GetMapping("/shop/{shopId}")
    public Response<IPage<Order>> getShopOrders(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status,
            @PathVariable Long shopId) {
        IPage<Order> page = orderApplicationService.pageOrdersByShopId(current, size, shopId, status);
        return Response.success(page);
    }
}
