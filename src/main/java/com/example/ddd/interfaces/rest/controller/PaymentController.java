package com.example.ddd.interfaces.rest.controller;

import com.example.ddd.application.service.AuthorizationService;
import com.example.ddd.application.service.PaymentApplicationService;
import com.example.ddd.domain.model.entity.Payment;
import com.example.ddd.interfaces.rest.dto.PaymentCreateRequest;
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
    private final AuthorizationService authorizationService;

    /**
     * 创建支付
     */
    @PostMapping
    public Response<Payment> create(@RequestBody PaymentCreateRequest request) {
        // 验证订单归属（只有订单所有者才能支付）
        authorizationService.checkPaymentPermission(request.getOrderId());
        Payment payment = paymentApplicationService.createPayment(request.getOrderId(), request.getPaymentMethod());
        return Response.success(payment);
    }

    /**
     * 模拟支付成功
     */
    @PostMapping("/{paymentNo}/success")
    public Response<Void> mockSuccess(@PathVariable String paymentNo) {
        // 模拟支付成功，生产环境应该通过支付回调或内部调用
        String transactionId = "MOCK_" + System.currentTimeMillis();
        paymentApplicationService.handlePaymentSuccess(paymentNo, transactionId);
        return Response.success();
    }

    /**
     * 申请退款
     */
    @PostMapping("/refund")
    public Response<Void> applyRefund(@RequestParam Long orderId) {
        // 验证订单归属（只有订单所有者才能申请退款）
        authorizationService.checkOrderOwnership(orderId);
        paymentApplicationService.applyRefund(orderId);
        return Response.success();
    }

    /**
     * 获取支付记录
     */
    @GetMapping("/{id}")
    public Response<Payment> getById(@PathVariable Long id) {
        // 验证支付记录归属
        authorizationService.checkPaymentOwnership(id);
        Payment payment = paymentApplicationService.getPaymentById(id);
        return Response.success(payment);
    }

    /**
     * 根据订单ID获取支付记录
     */
    @GetMapping("/order/{orderId}")
    public Response<Payment> getByOrderId(@PathVariable Long orderId) {
        // 验证订单归属
        authorizationService.checkOrderOwnership(orderId);
        Payment payment = paymentApplicationService.getPaymentByOrderId(orderId);
        return Response.success(payment);
    }
}
