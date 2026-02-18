package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.Payment;

/**
 * 支付记录仓储接口
 *
 * @author DDD Demo
 */
public interface PaymentRepository extends BaseRepository<Payment> {

    /**
     * 根据支付单号查询
     *
     * @param paymentNo 支付单号
     * @return 支付记录
     */
    Payment findByPaymentNo(String paymentNo);

    /**
     * 根据订单ID查询
     *
     * @param orderId 订单ID
     * @return 支付记录
     */
    Payment findByOrderId(Long orderId);

    /**
     * 根据订单号查询
     *
     * @param orderNo 订单号
     * @return 支付记录
     */
    Payment findByOrderNo(String orderNo);
}
