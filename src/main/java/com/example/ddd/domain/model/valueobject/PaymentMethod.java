package com.example.ddd.domain.model.valueobject;

/**
 * 支付方式枚举
 *
 * @author DDD Demo
 */
public enum PaymentMethod implements StatusType {

    /**
     * 支付宝
     */
    ALIPAY(1, "支付宝"),

    /**
     * 微信支付
     */
    WECHAT(2, "微信支付"),

    /**
     * 余额支付
     */
    BALANCE(3, "余额支付");

    private final Integer value;
    private final String description;

    PaymentMethod(Integer value, String description) {
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
     * @param value 支付方式值
     * @return 支付方式枚举
     */
    public static PaymentMethod fromValue(Integer value) {
        if (value == null) {
            return ALIPAY;
        }
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.getValue().equals(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("无效的支付方式值: " + value);
    }
}
