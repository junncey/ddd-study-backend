package com.example.ddd.domain.service;

import com.example.ddd.domain.model.entity.Order;
import com.example.ddd.domain.model.entity.Payment;
import com.example.ddd.domain.model.valueobject.Money;
import com.example.ddd.domain.model.valueobject.PaymentMethod;
import com.example.ddd.domain.model.valueobject.PaymentStatus;
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

        if (!payment.getStatus().canTransitionTo(PaymentStatus.SUCCESS)) {
            throw new IllegalArgumentException("当前状态不允许支付成功");
        }

        // 更新支付状态
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(transactionId);
        payment.setPayTime(LocalDateTime.now());
        paymentRepository.update(payment);

        // 更新订单状态为已支付
        Order order = orderRepository.findById(payment.getOrderId());
        if (order != null && order.canPay()) {
            order.setStatus(com.example.ddd.domain.model.valueobject.OrderStatus.PAID);
            order.setPayTime(LocalDateTime.now());
            orderRepository.update(order);
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

        if (!payment.getStatus().canTransitionTo(PaymentStatus.FAILED)) {
            throw new IllegalArgumentException("当前状态不允许支付失败");
        }

        payment.setStatus(PaymentStatus.FAILED);
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

        if (!payment.canRefund()) {
            throw new IllegalArgumentException("当前状态不允许退款");
        }

        payment.setStatus(PaymentStatus.REFUNDING);
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

        if (!payment.getStatus().canTransitionTo(PaymentStatus.REFUNDED)) {
            throw new IllegalArgumentException("当前状态不允许退款完成");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.update(payment);

        // 更新订单状态为已退款
        Order order = orderRepository.findById(payment.getOrderId());
        if (order != null) {
            order.setStatus(com.example.ddd.domain.model.valueobject.OrderStatus.REFUNDED);
            orderRepository.update(order);
        }
    }
}
