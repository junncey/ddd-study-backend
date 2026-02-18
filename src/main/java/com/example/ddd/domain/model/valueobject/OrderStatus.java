package com.example.ddd.domain.model.valueobject;

/**
 * 订单状态枚举
 *
 * @author DDD Demo
 */
public enum OrderStatus implements StatusType {

    /**
     * 待支付
     */
    PENDING(0, "待支付"),

    /**
     * 已支付
     */
    PAID(1, "已支付"),

    /**
     * 已发货
     */
    SHIPPED(2, "已发货"),

    /**
     * 已完成
     */
    COMPLETED(3, "已完成"),

    /**
     * 已取消
     */
    CANCELLED(4, "已取消"),

    /**
     * 退款中
     */
    REFUNDING(5, "退款中"),

    /**
     * 已退款
     */
    REFUNDED(6, "已退款");

    private final Integer value;
    private final String description;

    OrderStatus(Integer value, String description) {
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
     * @return 订单状态枚举
     */
    public static OrderStatus fromValue(Integer value) {
        if (value == null) {
            return PENDING;
        }
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的订单状态值: " + value);
    }

    /**
     * 判断是否可以流转到指定状态
     *
     * @param newStatus 新状态
     * @return true 如果可以流转
     */
    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == PAID || newStatus == CANCELLED;
            case PAID -> newStatus == SHIPPED || newStatus == REFUNDING;
            case SHIPPED -> newStatus == COMPLETED || newStatus == REFUNDING;
            case COMPLETED -> newStatus == REFUNDING;
            case REFUNDING -> newStatus == REFUNDED || newStatus == COMPLETED;
            case CANCELLED, REFUNDED -> false; // 终态，不能再流转
        };
    }

    /**
     * 判断是否为终态
     *
     * @return true 如果为终态
     */
    public boolean isFinal() {
        return this == CANCELLED || this == REFUNDED;
    }

    /**
     * 判断是否可以取消
     *
     * @return true 如果可以取消
     */
    public boolean canCancel() {
        return this == PENDING;
    }

    /**
     * 判断是否可以退款
     *
     * @return true 如果可以退款
     */
    public boolean canRefund() {
        return this == PAID || this == SHIPPED || this == COMPLETED;
    }

    /**
     * 判断是否可以发货
     *
     * @return true 如果可以发货
     */
    public boolean canShip() {
        return this == PAID;
    }

    /**
     * 判断是否可以完成
     *
     * @return true 如果可以完成
     */
    public boolean canComplete() {
        return this == SHIPPED;
    }
}
