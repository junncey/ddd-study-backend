package com.example.ddd.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 支付创建请求
 *
 * @author DDD Demo
 */
@Data
public class PaymentCreateRequest {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 支付方式（1-支付宝，2-微信，3-银行卡）
     */
    @NotNull(message = "支付方式不能为空")
    private Integer paymentMethod;
}
