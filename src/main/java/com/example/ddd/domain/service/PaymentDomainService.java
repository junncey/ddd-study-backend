package com.example.ddd.domain.service;

import com.example.ddd.domain.model.entity.Order;
import com.example.ddd.domain.model.entity.Payment;
import com.example.ddd.domain.model.valueobject.*;
import com.example.ddd.domain.repository.OrderRepository;
import com.example.ddd.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 支付领域服务
 * 处理支付相关的核心业务逻辑
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentDomainService extends DomainService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    /**
     * 创建支付记录
     *
     * @param orderId       订单ID
     * @param paymentMethod 支付方式
     * @return 支付记录
     */
    @Transactional(rollbackFor = Exception.class)
    public Payment createPayment(Long orderId, PaymentMethod paymentMethod) {
        validate();

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        if (!order.canPay()) {
            throw new IllegalArgumentException("订单当前状态不允许支付");
        }

        // 检查是否已存在支付记录
        Payment existingPayment = paymentRepository.findByOrderId(orderId);
        if (existingPayment != null) {
            throw new IllegalArgumentException("该订单已存在支付记录");
        }

        // 创建支付记录
        Payment payment = new Payment();
        payment.setPaymentNo("PAY" + System.currentTimeMillis());
        payment.setOrderId(orderId);
        payment.setOrderNo(order.getOrderNo());
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(order.getPayAmount());
        payment.setStatus(PaymentStatus.PENDING);

        return paymentRepository.save(payment);
    }

    /**
     * 处理支付成功回调（模拟）
     *
     * @param paymentNo     支付单号
     * @param transactionId 第三方交易流水号
     */
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentSuccess(String paymentNo, String transactionId) {
        validate();

        Payment payment = paymentRepository.findByPaymentNo(paymentNo);
        if (payment == null) {
            throw new IllegalArgumentException("支付记录不存在");
        }

        // 使用事件驱动的状态转换
        PaymentStatus oldStatus = payment.getStatus();
        try {
            payment.setStatus(payment.getStatus().transition(PaymentEvent.PAY_SUCCESS));
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        payment.setTransactionId(transactionId);
        payment.setPayTime(LocalDateTime.now());
        paymentRepository.update(payment);

        // 更新订单状态为已支付
        Order order = orderRepository.findById(payment.getOrderId());
        if (order != null) {
            try {
                order.transitionStatus(OrderEvent.PAY);
                order.setPayTime(LocalDateTime.now());
                orderRepository.update(order);
            } catch (IllegalStateException e) {
                log.warn("订单状态更新失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 处理支付失败回调
     *
     * @param paymentNo 支付单号
     */
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentFailed(String paymentNo) {
        validate();

        Payment payment = paymentRepository.findByPaymentNo(paymentNo);
        if (payment == null) {
            throw new IllegalArgumentException("支付记录不存在");
        }

        // 使用事件驱动的状态转换
        try {
            payment.setStatus(payment.getStatus().transition(PaymentEvent.PAY_FAILED));
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        paymentRepository.update(payment);
    }

    /**
     * 申请退款
     *
     * @param orderId 订单ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyRefund(Long orderId) {
        validate();

        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment == null) {
            throw new IllegalArgumentException("支付记录不存在");
        }

        // 使用事件驱动的状态转换
        try {
            payment.setStatus(payment.getStatus().transition(PaymentEvent.APPLY_REFUND));
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        paymentRepository.update(payment);
    }

    /**
     * 处理退款成功
     *
     * @param paymentNo 支付单号
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleRefundSuccess(String paymentNo) {
        validate();

        Payment payment = paymentRepository.findByPaymentNo(paymentNo);
        if (payment == null) {
            throw new IllegalArgumentException("支付记录不存在");
        }

        // 使用事件驱动的状态转换
        try {
            payment.setStatus(payment.getStatus().transition(PaymentEvent.REFUND_SUCCESS));
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        paymentRepository.update(payment);

        // 更新订单状态为已退款
        Order order = orderRepository.findById(payment.getOrderId());
        if (order != null) {
            try {
                order.transitionStatus(OrderEvent.REFUND_SUCCESS);
                orderRepository.update(order);
            } catch (IllegalStateException e) {
                log.warn("订单状态更新失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 处理退款失败
     *
     * @param paymentNo 支付单号
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleRefundFailed(String paymentNo) {
        validate();

        Payment payment = paymentRepository.findByPaymentNo(paymentNo);
        if (payment == null) {
            throw new IllegalArgumentException("支付记录不存在");
        }

        // 使用事件驱动的状态转换
        try {
            payment.setStatus(payment.getStatus().transition(PaymentEvent.REFUND_FAILED));
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        paymentRepository.update(payment);

        // 更新订单状态（退款失败回退到已完成）
        Order order = orderRepository.findById(payment.getOrderId());
        if (order != null) {
            try {
                order.transitionStatus(OrderEvent.REFUND_FAILED);
                orderRepository.update(order);
            } catch (IllegalStateException e) {
                log.warn("订单状态更新失败: {}", e.getMessage());
            }
        }
    }
}
