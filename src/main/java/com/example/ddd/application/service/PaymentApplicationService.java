package com.example.ddd.application.service;

import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.Payment;
import com.example.ddd.domain.model.valueobject.PaymentMethod;
import com.example.ddd.domain.repository.PaymentRepository;
import com.example.ddd.domain.service.PaymentDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 支付应用服务
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentApplicationService extends ApplicationService {

    private final PaymentDomainService paymentDomainService;
    private final PaymentRepository paymentRepository;

    /**
     * 创建支付
     */
    public Payment createPayment(Long orderId, Integer paymentMethod) {
        beforeExecute();
        try {
            return paymentDomainService.createPayment(orderId, PaymentMethod.fromValue(paymentMethod));
        } finally {
            afterExecute();
        }
    }

    /**
     * 处理支付成功
     */
    public void handlePaymentSuccess(String paymentNo, String transactionId) {
        beforeExecute();
        try {
            paymentDomainService.handlePaymentSuccess(paymentNo, transactionId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 处理支付失败
     */
    public void handlePaymentFailed(String paymentNo) {
        beforeExecute();
        try {
            paymentDomainService.handlePaymentFailed(paymentNo);
        } finally {
            afterExecute();
        }
    }

    /**
     * 申请退款
     */
    public void applyRefund(Long orderId) {
        beforeExecute();
        try {
            paymentDomainService.applyRefund(orderId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 处理退款成功
     */
    public void handleRefundSuccess(String paymentNo) {
        beforeExecute();
        try {
            paymentDomainService.handleRefundSuccess(paymentNo);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取支付记录
     */
    public Payment getPaymentById(Long paymentId) {
        beforeExecute();
        try {
            return paymentRepository.findById(paymentId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 根据订单ID获取支付记录
     */
    public Payment getPaymentByOrderId(Long orderId) {
        beforeExecute();
        try {
            return paymentRepository.findByOrderId(orderId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 根据支付单号获取支付记录
     */
    public Payment getPaymentByPaymentNo(String paymentNo) {
        beforeExecute();
        try {
            return paymentRepository.findByPaymentNo(paymentNo);
        } finally {
            afterExecute();
        }
    }
}
