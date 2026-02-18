package com.example.ddd.domain.model.valueobject;

/**
 * 支付事件枚举
 * 定义触发支付状态转换的业务事件
 *
 * @author DDD Demo
 */
public enum PaymentEvent {

    /**
     * 支付成功事件：待支付 -> 支付成功
     */
    PAY_SUCCESS("支付成功"),

    /**
     * 支付失败事件：待支付 -> 支付失败
     */
    PAY_FAILED("支付失败"),

    /**
     * 申请退款事件：支付成功 -> 退款中
     */
    APPLY_REFUND("申请退款"),

    /**
     * 退款成功事件：退款中 -> 已退款
     */
    REFUND_SUCCESS("退款成功"),

    /**
     * 退款失败事件：退款中 -> 支付成功（回退）
     */
    REFUND_FAILED("退款失败");

    private final String description;

    PaymentEvent(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
