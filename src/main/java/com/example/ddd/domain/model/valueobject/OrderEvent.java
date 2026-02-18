package com.example.ddd.domain.model.valueobject;

/**
 * 订单事件枚举
 * 定义触发订单状态转换的业务事件
 *
 * @author DDD Demo
 */
public enum OrderEvent {

    /**
     * 支付事件：待支付 -> 已支付
     */
    PAY("支付"),

    /**
     * 发货事件：已支付 -> 已发货
     */
    SHIP("发货"),

    /**
     * 确认收货事件：已发货 -> 已完成
     */
    CONFIRM("确认收货"),

    /**
     * 取消事件：待支付 -> 已取消
     */
    CANCEL("取消订单"),

    /**
     * 申请退款事件：已支付/已发货/已完成 -> 退款中
     */
    APPLY_REFUND("申请退款"),

    /**
     * 退款成功事件：退款中 -> 已退款
     */
    REFUND_SUCCESS("退款成功"),

    /**
     * 退款失败事件：退款中 -> 已完成（回退）
     */
    REFUND_FAILED("退款失败");

    private final String description;

    OrderEvent(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
