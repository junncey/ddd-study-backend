package com.example.ddd.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.Order;
import com.example.ddd.domain.model.entity.OrderItem;
import com.example.ddd.domain.repository.OrderRepository;
import com.example.ddd.domain.service.OrderDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单应用服务
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderApplicationService extends ApplicationService {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;

    /**
     * 创建订单
     */
    public Order createOrder(Order order, List<OrderItem> items, List<Long> cartItemIds) {
        beforeExecute();
        try {
            return orderDomainService.createOrder(order, items, cartItemIds);
        } finally {
            afterExecute();
        }
    }

    /**
     * 取消订单
     */
    public Order cancelOrder(Long orderId, Long userId) {
        beforeExecute();
        try {
            return orderDomainService.cancelOrder(orderId, userId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 支付订单
     */
    public Order payOrder(Long orderId) {
        beforeExecute();
        try {
            return orderDomainService.payOrder(orderId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 发货
     */
    public Order shipOrder(Long orderId) {
        beforeExecute();
        try {
            return orderDomainService.shipOrder(orderId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 确认收货
     */
    public Order completeOrder(Long orderId) {
        beforeExecute();
        try {
            return orderDomainService.completeOrder(orderId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取订单详情
     */
    public Order getOrderById(Long orderId) {
        beforeExecute();
        try {
            return orderRepository.findById(orderId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 根据订单号获取订单
     */
    public Order getOrderByOrderNo(String orderNo) {
        beforeExecute();
        try {
            return orderRepository.findByOrderNo(orderNo);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取用户订单列表
     */
    public List<Order> getOrdersByUserId(Long userId) {
        beforeExecute();
        try {
            return orderRepository.findByUserId(userId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取店铺订单列表
     */
    public List<Order> getOrdersByShopId(Long shopId) {
        beforeExecute();
        try {
            return orderRepository.findByShopId(shopId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 分页查询用户订单
     */
    public IPage<Order> pageOrdersByUserId(Long current, Long size, Long userId, Integer status) {
        beforeExecute();
        try {
            return orderRepository.pageByUserId(new Page<>(current, size), userId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 分页查询店铺订单
     */
    public IPage<Order> pageOrdersByShopId(Long current, Long size, Long shopId, Integer status) {
        beforeExecute();
        try {
            return orderRepository.pageByShopId(new Page<>(current, size), shopId);
        } finally {
            afterExecute();
        }
    }
}
