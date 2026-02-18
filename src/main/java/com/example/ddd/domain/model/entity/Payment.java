package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ddd.domain.model.valueobject.Money;
import com.example.ddd.domain.model.valueobject.PaymentEvent;
import com.example.ddd.domain.model.valueobject.PaymentMethod;
import com.example.ddd.domain.model.valueobject.PaymentStatus;
import com.example.ddd.infrastructure.persistence.handler.MoneyTypeHandler;
import com.example.ddd.infrastructure.persistence.handler.PaymentMethodTypeHandler;
import com.example.ddd.infrastructure.persistence.handler.PaymentStatusTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 支付记录实体
 *
 * @author DDD Demo
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_payment")
public class Payment extends BaseEntity {

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付方式
     */
    @TableField(typeHandler = PaymentMethodTypeHandler.class)
    private PaymentMethod paymentMethod;

    /**
     * 支付金额
     */
    @TableField(typeHandler = MoneyTypeHandler.class)
    private Money amount;

    /**
     * 支付状态
     */
    @TableField(typeHandler = PaymentStatusTypeHandler.class)
    private PaymentStatus status;

    /**
     * 第三方交易流水号
     */
    private String transactionId;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 获取状态值
     */
    public Integer getStatusInt() {
        return status != null ? status.getValue() : null;
    }

    /**
     * 设置状态值
     */
    public void setStatusInt(Integer statusInt) {
        this.status = statusInt != null ? PaymentStatus.fromValue(statusInt) : null;
    }

    /**
     * 获取支付方式值
     */
    public Integer getPaymentMethodInt() {
        return paymentMethod != null ? paymentMethod.getValue() : null;
    }

    /**
     * 设置支付方式值
     */
    public void setPaymentMethodInt(Integer methodInt) {
        this.paymentMethod = methodInt != null ? PaymentMethod.fromValue(methodInt) : null;
    }

    /**
     * 获取金额值
     */
    public String getAmountValue() {
        return amount != null ? amount.getValue().toString() : null;
    }

    /**
     * 设置金额值
     */
    public void setAmountValue(String amountValue) {
        this.amount = amountValue != null ? Money.of(amountValue) : null;
    }

    // ==================== 状态转换方法（基于事件驱动） ====================

    /**
     * 判断是否可以执行指定事件
     *
     * @param event 事件
     * @return true 如果可以执行
     */
    public boolean canTrigger(PaymentEvent event) {
        return status != null && status.canTrigger(event);
    }

    /**
     * 执行状态转换
     *
     * @param event 触发的事件
     * @return 新状态
     * @throws IllegalStateException 如果事件不能在当前状态下触发
     */
    public PaymentStatus transitionStatus(PaymentEvent event) {
        if (status == null) {
            throw new IllegalStateException("支付状态为空");
        }
        this.status = status.transition(event);
        return this.status;
    }

    // ==================== 便捷方法（委托给 PaymentStatus） ====================

    /**
     * 判断是否可以退款
     */
    public boolean canRefund() {
        return status != null && status.canRefund();
    }

    /**
     * 判断是否支付成功
     */
    public boolean isPaid() {
        return status != null && status.isPaid();
    }
}
