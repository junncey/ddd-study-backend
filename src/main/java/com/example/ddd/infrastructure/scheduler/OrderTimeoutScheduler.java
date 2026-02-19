package com.example.ddd.infrastructure.scheduler;

import com.example.ddd.domain.model.entity.Order;
import com.example.ddd.domain.model.entity.OrderItem;
import com.example.ddd.domain.model.valueobject.OrderEvent;
import com.example.ddd.domain.repository.OrderItemRepository;
import com.example.ddd.domain.repository.OrderRepository;
import com.example.ddd.domain.repository.ProductSkuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单超时自动取消定时任务
 * 每分钟检查一次超时未支付的订单并自动取消
 *
 * @author DDD Demo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutScheduler {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductSkuRepository productSkuRepository;

    /**
     * 订单超时时间（分钟）
     */
    private static final int ORDER_TIMEOUT_MINUTES = 30;

    /**
     * 每分钟执行一次，检查超时订单
     */
    @Scheduled(fixedRate = 60000)
    public void cancelTimeoutOrders() {
        try {
            // 计算超时时间点
            LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(ORDER_TIMEOUT_MINUTES);

            // 查询超时未支付的订单
            List<Order> timeoutOrders = orderRepository.findTimeoutPendingOrders(timeoutThreshold);

            if (timeoutOrders.isEmpty()) {
                log.debug("没有超时未支付的订单");
                return;
            }

            log.info("发现 {} 个超时未支付订单，开始自动取消", timeoutOrders.size());

            int successCount = 0;
            int failCount = 0;

            for (Order order : timeoutOrders) {
                try {
                    cancelOrder(order);
                    successCount++;
                    log.info("订单 {} 已自动取消", order.getOrderNo());
                } catch (Exception e) {
                    failCount++;
                    log.error("订单 {} 自动取消失败: {}", order.getOrderNo(), e.getMessage());
                }
            }

            log.info("订单超时取消任务完成，成功: {}，失败: {}", successCount, failCount);

        } catch (Exception e) {
            log.error("订单超时取消任务执行异常", e);
        }
    }

    /**
     * 取消单个订单（系统自动取消，无需验证用户权限）
     *
     * @param order 订单对象
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Order order) {
        // 使用事件驱动的状态转换
        try {
            order.transitionStatus(OrderEvent.CANCEL);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("订单状态不允许取消: " + e.getMessage());
        }

        // 恢复库存
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem item : items) {
            productSkuRepository.increaseStock(item.getSkuId(), item.getQuantity());
        }

        // 设置取消时间
        order.setCancelTime(LocalDateTime.now());

        // 保存订单
        orderRepository.update(order);
    }
}
