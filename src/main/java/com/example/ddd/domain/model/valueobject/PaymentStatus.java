package com.example.ddd.domain.model.valueobject;

/**
 * 支付状态枚举
 *
 * @author DDD Demo
 */
public enum PaymentStatus implements StatusType {

    /**
     * 待支付
     */
    PENDING(0, "待支付"),

    /**
     * 支付成功
     */
    SUCCESS(1, "支付成功"),

    /**
     * 支付失败
     */
    FAILED(2, "支付失败"),

    /**
     * 退款中
     */
    REFUNDING(3, "退款中"),

    /**
     * 已退款
     */
    REFUNDED(4, "已退款");

    private final Integer value;
    private final String description;

    PaymentStatus(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * 根据值获取枚举
     *
     * @param value 状态值
     * @return 支付状态枚举
     */
    public static PaymentStatus fromValue(Integer value) {
        if (value == null) {
            return PENDING;
        }
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的支付状态值: " + value);
    }

    /**
     * 判断是否可以流转到指定状态
     *
     * @param newStatus 新状态
     * @return true 如果可以流转
     */
    public boolean canTransitionTo(PaymentStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == SUCCESS || newStatus == FAILED;
            case SUCCESS -> newStatus == REFUNDING;
            case REFUNDING -> newStatus == REFUNDED || newStatus == SUCCESS; // 退款失败可回退
            case FAILED, REFUNDED -> false; // 终态
        };
    }

    /**
     * 判断是否支付成功
     *
     * @return true 如果支付成功
     */
    public boolean isPaid() {
        return this == SUCCESS;
    }

    /**
     * 判断是否可以退款
     *
     * @return true 如果可以退款
     */
    public boolean canRefund() {
        return this == SUCCESS;
    }
}
