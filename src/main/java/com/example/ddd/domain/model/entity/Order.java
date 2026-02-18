package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ddd.domain.model.valueobject.Money;
import com.example.ddd.domain.model.valueobject.OrderEvent;
import com.example.ddd.domain.model.valueobject.OrderStatus;
import com.example.ddd.infrastructure.persistence.handler.MoneyTypeHandler;
import com.example.ddd.infrastructure.persistence.handler.OrderStatusTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 订单实体
 *
 * @author DDD Demo
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_order")
public class Order extends BaseEntity {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 订单总金额
     */
    @TableField(typeHandler = MoneyTypeHandler.class)
    private Money totalAmount;

    /**
     * 实付金额
     */
    @TableField(typeHandler = MoneyTypeHandler.class)
    private Money payAmount;

    /**
     * 订单状态
     */
    @TableField(typeHandler = OrderStatusTypeHandler.class)
    private OrderStatus status;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 收货地址
     */
    private String receiverAddress;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 获取状态值
     *
     * @return 状态值
     */
    public Integer getStatusInt() {
        return status != null ? status.getValue() : null;
    }

    /**
     * 设置状态值
     *
     * @param statusInt 状态值
     */
    public void setStatusInt(Integer statusInt) {
        this.status = statusInt != null ? OrderStatus.fromValue(statusInt) : null;
    }

    /**
     * 获取总金额值
     *
     * @return 总金额值
     */
    public String getTotalAmountValue() {
        return totalAmount != null ? totalAmount.getValue().toString() : null;
    }

    /**
     * 设置总金额值
     *
     * @param amountValue 金额值
     */
    public void setTotalAmountValue(String amountValue) {
        this.totalAmount = amountValue != null ? Money.of(amountValue) : null;
    }

    /**
     * 获取实付金额值
     *
     * @return 实付金额值
     */
    public String getPayAmountValue() {
        return payAmount != null ? payAmount.getValue().toString() : null;
    }

    /**
     * 设置实付金额值
     *
     * @param amountValue 金额值
     */
    public void setPayAmountValue(String amountValue) {
        this.payAmount = amountValue != null ? Money.of(amountValue) : null;
    }

    // ==================== 状态转换方法（基于事件驱动） ====================

    /**
     * 判断是否可以执行指定事件
     *
     * @param event 事件
     * @return true 如果可以执行
     */
    public boolean canTrigger(OrderEvent event) {
        return status != null && status.canTrigger(event);
    }

    /**
     * 执行状态转换
     *
     * @param event 触发的事件
     * @return 新状态
     * @throws IllegalStateException 如果事件不能在当前状态下触发
     */
    public OrderStatus transitionStatus(OrderEvent event) {
        if (status == null) {
            throw new IllegalStateException("订单状态为空");
        }
        this.status = status.transition(event);
        return this.status;
    }

    // ==================== 便捷方法（委托给 OrderStatus） ====================

    /**
     * 判断是否可以取消
     *
     * @return true 如果可以取消
     */
    public boolean canCancel() {
        return status != null && status.canCancel();
    }

    /**
     * 判断是否可以支付
     *
     * @return true 如果可以支付
     */
    public boolean canPay() {
        return status != null && status.canPay();
    }

    /**
     * 判断是否可以发货
     *
     * @return true 如果可以发货
     */
    public boolean canShip() {
        return status != null && status.canShip();
    }

    /**
     * 判断是否可以完成
     *
     * @return true 如果可以完成
     */
    public boolean canComplete() {
        return status != null && status.canComplete();
    }

    /**
     * 判断是否可以申请退款
     *
     * @return true 如果可以申请退款
     */
    public boolean canRefund() {
        return status != null && status.canRefund();
    }
}
