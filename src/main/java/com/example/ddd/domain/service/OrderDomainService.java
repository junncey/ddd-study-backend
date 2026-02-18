package com.example.ddd.domain.service;

import com.example.ddd.domain.model.entity.*;
import com.example.ddd.domain.model.valueobject.Money;
import com.example.ddd.domain.model.valueobject.OrderEvent;
import com.example.ddd.domain.model.valueobject.OrderStatus;
import com.example.ddd.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单领域服务
 * 包含订单相关的核心业务逻辑（核心模块）
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDomainService extends DomainService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusLogRepository orderStatusLogRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductSkuRepository productSkuRepository;
    private final ProductRepository productRepository;

    /**
     * 创建订单
     * 核心逻辑：生成订单号、计算金额、扣减库存、保存订单、清理购物车
     *
     * @param order        订单实体
     * @param items        订单明细列表
     * @param cartItemIds 要删除的购物车明细ID列表
     * @return 创建后的订单
     */
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Order order, List<OrderItem> items, List<Long> cartItemIds) {
        validate();

        // 1. 生成订单号
        order.setOrderNo("ORD" + System.currentTimeMillis());

        // 2. 设置初始状态
        order.setStatus(OrderStatus.PENDING);

        // 3. 计算订单金额
        Money totalAmount = items.stream()
                .peek(OrderItem::calculateTotalAmount)
                .map(OrderItem::getTotalAmount)
                .reduce(Money.zero(), Money::add);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount); // 暂时没有优惠，实付金额等于总金额

        // 4. 验证商品状态
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId());
            if (product == null) {
                throw new IllegalArgumentException("商品不存在");
            }
            if (!product.canPurchase()) {
                throw new IllegalArgumentException("商品 [" + product.getProductName() + "] 不可购买");
            }
        }

        // 5. 扣减库存（乐观锁）
        for (OrderItem item : items) {
            ProductSku sku = productSkuRepository.findById(item.getSkuId());
            if (sku == null) {
                throw new IllegalArgumentException("商品SKU不存在");
            }

            int updated = productSkuRepository.decreaseStock(
                    item.getSkuId(),
                    item.getQuantity(),
                    sku.getStock().getValue()  // 乐观锁版本
            );

            if (updated == 0) {
                throw new IllegalArgumentException("商品库存不足");
            }
        }

        // 6. 保存订单
        Order savedOrder = orderRepository.save(order);

        // 7. 保存订单明细
        for (OrderItem item : items) {
            item.setOrderId(savedOrder.getId());
            item.calculateTotalAmount();
            orderItemRepository.save(item);
        }

        // 8. 记录状态日志
        recordStatusLog(savedOrder.getId(), null, OrderStatus.PENDING.getValue(), "创建订单");

        // 9. 清理购物车
        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            for (Long cartItemId : cartItemIds) {
                cartItemRepository.delete(cartItemId);
            }
        }

        return savedOrder;
    }

    /**
     * 取消订单
     * 恢复库存
     *
     * @param orderId 订单ID
     * @param userId  用户ID（用于验证权限）
     * @return 更新后的订单
     */
    @Transactional(rollbackFor = Exception.class)
    public Order cancelOrder(Long orderId, Long userId) {
        validate();

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        // 验证权限
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此订单");
        }

        // 使用事件驱动的状态转换
        OrderStatus oldStatus = order.getStatus();
        try {
            order.transitionStatus(OrderEvent.CANCEL);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        // 恢复库存
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            productSkuRepository.increaseStock(item.getSkuId(), item.getQuantity());
        }

        // 设置取消时间
        order.setCancelTime(LocalDateTime.now());
        orderRepository.update(order);

        // 记录状态日志
        recordStatusLog(orderId, oldStatus.getValue(), order.getStatus().getValue(), "取消订单");

        return order;
    }

    /**
     * 支付订单
     *
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    @Transactional(rollbackFor = Exception.class)
    public Order payOrder(Long orderId) {
        validate();

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        // 使用事件驱动的状态转换
        OrderStatus oldStatus = order.getStatus();
        try {
            order.transitionStatus(OrderEvent.PAY);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        // 设置支付时间
        order.setPayTime(LocalDateTime.now());
        orderRepository.update(order);

        // 记录状态日志
        recordStatusLog(orderId, oldStatus.getValue(), order.getStatus().getValue(), "支付成功");

        return order;
    }

    /**
     * 发货
     *
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    @Transactional(rollbackFor = Exception.class)
    public Order shipOrder(Long orderId) {
        validate();

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        // 使用事件驱动的状态转换
        OrderStatus oldStatus = order.getStatus();
        try {
            order.transitionStatus(OrderEvent.SHIP);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        // 设置发货时间
        order.setShipTime(LocalDateTime.now());
        orderRepository.update(order);

        // 记录状态日志
        recordStatusLog(orderId, oldStatus.getValue(), order.getStatus().getValue(), "发货");

        return order;
    }

    /**
     * 完成订单（确认收货）
     *
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    @Transactional(rollbackFor = Exception.class)
    public Order completeOrder(Long orderId) {
        validate();

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        // 使用事件驱动的状态转换
        OrderStatus oldStatus = order.getStatus();
        try {
            order.transitionStatus(OrderEvent.CONFIRM);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        // 设置完成时间
        order.setCompleteTime(LocalDateTime.now());
        orderRepository.update(order);

        // 记录状态日志
        recordStatusLog(orderId, oldStatus.getValue(), order.getStatus().getValue(), "确认收货");

        return order;
    }

    /**
     * 记录状态日志
     *
     * @param orderId   订单ID
     * @param oldStatus  原状态
     * @param newStatus  新状态
     * @param remark    备注
     */
    private void recordStatusLog(Long orderId, Integer oldStatus, Integer newStatus, String remark) {
        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        log.setRemark(remark);
        log.setOperator("SYSTEM");
        orderStatusLogRepository.save(log);
    }
}
