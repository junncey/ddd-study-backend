package com.example.ddd.interfaces.rest.controller;

import com.example.ddd.application.service.PaymentApplicationService;
import com.example.ddd.domain.model.entity.Payment;
import com.example.ddd.interfaces.rest.vo.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 *
 * @author DDD Demo
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    /**
     * 创建支付
     */
    @PostMapping
    public Response<Payment> create(@RequestParam Long orderId, @RequestParam Integer paymentMethod) {
        Payment payment = paymentApplicationService.createPayment(orderId, paymentMethod);
        return Response.success(payment);
    }

    /**
     * 模拟支付成功
     */
    @PostMapping("/{paymentNo}/success")
    public Response<Void> mockSuccess(@PathVariable String paymentNo) {
        String transactionId = "MOCK_" + System.currentTimeMillis();
        paymentApplicationService.handlePaymentSuccess(paymentNo, transactionId);
        return Response.success();
    }

    /**
     * 申请退款
     */
    @PostMapping("/refund")
    public Response<Void> applyRefund(@RequestParam Long orderId) {
        paymentApplicationService.applyRefund(orderId);
        return Response.success();
    }

    /**
     * 获取支付记录
     */
    @GetMapping("/{id}")
    public Response<Payment> getById(@PathVariable Long id) {
        Payment payment = paymentApplicationService.getPaymentById(id);
        return Response.success(payment);
    }

    /**
     * 根据订单ID获取支付记录
     */
    @GetMapping("/order/{orderId}")
    public Response<Payment> getByOrderId(@PathVariable Long orderId) {
        Payment payment = paymentApplicationService.getPaymentByOrderId(orderId);
        return Response.success(payment);
    }
}
