package com.example.ddd.domain.model.valueobject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单状态枚举
 * 使用事件驱动的状态机模式管理状态转换
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

    /**
     * 事件到目标状态的映射
     * 定义每个当前状态下，各事件对应的目标状态
     */
    private static final Map<OrderStatus, Map<OrderEvent, OrderStatus>> EVENT_TRANSITIONS;

    /**
     * 终态集合
     */
    private static final Set<OrderStatus> FINAL_STATES = Set.of(CANCELLED, REFUNDED);

    static {
        EVENT_TRANSITIONS = new EnumMap<>(OrderStatus.class);

        // 待支付 -> 已支付（支付）、已取消（取消）
        EVENT_TRANSITIONS.put(PENDING, Map.of(
            OrderEvent.PAY, PAID,
            OrderEvent.CANCEL, CANCELLED
        ));

        // 已支付 -> 已发货（发货）、退款中（申请退款）
        EVENT_TRANSITIONS.put(PAID, Map.of(
            OrderEvent.SHIP, SHIPPED,
            OrderEvent.APPLY_REFUND, REFUNDING
        ));

        // 已发货 -> 已完成（确认收货）、退款中（申请退款）
        EVENT_TRANSITIONS.put(SHIPPED, Map.of(
            OrderEvent.CONFIRM, COMPLETED,
            OrderEvent.APPLY_REFUND, REFUNDING
        ));

        // 已完成 -> 退款中（申请退款）
        EVENT_TRANSITIONS.put(COMPLETED, Map.of(
            OrderEvent.APPLY_REFUND, REFUNDING
        ));

        // 退款中 -> 已退款（退款成功）、已完成（退款失败）
        EVENT_TRANSITIONS.put(REFUNDING, Map.of(
            OrderEvent.REFUND_SUCCESS, REFUNDED,
            OrderEvent.REFUND_FAILED, COMPLETED
        ));

        // 终态：无事件可触发
        EVENT_TRANSITIONS.put(CANCELLED, Map.of());
        EVENT_TRANSITIONS.put(REFUNDED, Map.of());
    }

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
     * 根据事件获取目标状态
     *
     * @param event 触发的事件
     * @return 目标状态（Optional）
     */
    public Optional<OrderStatus> transitionBy(OrderEvent event) {
        Map<OrderEvent, OrderStatus> transitions = EVENT_TRANSITIONS.get(this);
        if (transitions == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(transitions.get(event));
    }

    /**
     * 判断指定事件是否可以触发
     *
     * @param event 事件
     * @return true 如果可以触发
     */
    public boolean canTrigger(OrderEvent event) {
        Map<OrderEvent, OrderStatus> transitions = EVENT_TRANSITIONS.get(this);
        return transitions != null && transitions.containsKey(event);
    }

    /**
     * 获取当前状态下可触发的事件列表
     *
     * @return 可触发的事件集合
     */
    public Set<OrderEvent> getAllowedEvents() {
        Map<OrderEvent, OrderStatus> transitions = EVENT_TRANSITIONS.get(this);
        if (transitions == null || transitions.isEmpty()) {
            return Collections.emptySet();
        }
        return transitions.keySet();
    }

    /**
     * 执行状态转换
     *
     * @param event 触发的事件
     * @return 新状态
     * @throws IllegalStateException 如果事件不能在当前状态下触发
     */
    public OrderStatus transition(OrderEvent event) {
        return transitionBy(event)
            .orElseThrow(() -> new IllegalStateException(
                buildTransitionErrorMessage(event)
            ));
    }

    /**
     * 构建转换失败的错误信息
     *
     * @param event 尝试触发的事件
     * @return 错误信息
     */
    private String buildTransitionErrorMessage(OrderEvent event) {
        if (isFinal()) {
            return String.format("订单已终态（%s），无法执行【%s】操作", description, event.getDescription());
        }

        Set<OrderEvent> allowedEvents = getAllowedEvents();
        if (allowedEvents.isEmpty()) {
            return String.format("当前状态（%s）不允许执行任何操作", description);
        }

        String allowedStr = allowedEvents.stream()
            .map(OrderEvent::getDescription)
            .collect(Collectors.joining("、"));

        return String.format("当前状态（%s）不允许执行【%s】操作，允许的操作：%s",
            description, event.getDescription(), allowedStr);
    }

    /**
     * 判断是否为终态
     *
     * @return true 如果为终态
     */
    public boolean isFinal() {
        return FINAL_STATES.contains(this);
    }

    // ==================== 以下是兼容旧代码的便捷方法 ====================

    /**
     * 判断是否可以流转到指定状态（兼容旧代码）
     *
     * @param newStatus 新状态
     * @return true 如果可以流转
     */
    public boolean canTransitionTo(OrderStatus newStatus) {
        return EVENT_TRANSITIONS.get(this).containsValue(newStatus);
    }

    /**
     * 判断是否可以取消
     *
     * @return true 如果可以取消
     */
    public boolean canCancel() {
        return canTrigger(OrderEvent.CANCEL);
    }

    /**
     * 判断是否可以退款
     *
     * @return true 如果可以退款
     */
    public boolean canRefund() {
        return canTrigger(OrderEvent.APPLY_REFUND);
    }

    /**
     * 判断是否可以发货
     *
     * @return true 如果可以发货
     */
    public boolean canShip() {
        return canTrigger(OrderEvent.SHIP);
    }

    /**
     * 判断是否可以完成（确认收货）
     *
     * @return true 如果可以完成
     */
    public boolean canComplete() {
        return canTrigger(OrderEvent.CONFIRM);
    }

    /**
     * 判断是否可以支付
     *
     * @return true 如果可以支付
     */
    public boolean canPay() {
        return canTrigger(OrderEvent.PAY);
    }
}
