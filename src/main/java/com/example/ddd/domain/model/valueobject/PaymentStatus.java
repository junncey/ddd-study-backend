package com.example.ddd.domain.model.valueobject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 支付状态枚举
 * 使用事件驱动的状态机模式管理状态转换
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

    /**
     * 事件到目标状态的映射
     */
    private static final Map<PaymentStatus, Map<PaymentEvent, PaymentStatus>> EVENT_TRANSITIONS;

    /**
     * 终态集合
     */
    private static final Set<PaymentStatus> FINAL_STATES = Set.of(FAILED, REFUNDED);

    static {
        EVENT_TRANSITIONS = new EnumMap<>(PaymentStatus.class);

        // 待支付 -> 支付成功（支付成功）、支付失败（支付失败）
        EVENT_TRANSITIONS.put(PENDING, Map.of(
            PaymentEvent.PAY_SUCCESS, SUCCESS,
            PaymentEvent.PAY_FAILED, FAILED
        ));

        // 支付成功 -> 退款中（申请退款）
        EVENT_TRANSITIONS.put(SUCCESS, Map.of(
            PaymentEvent.APPLY_REFUND, REFUNDING
        ));

        // 退款中 -> 已退款（退款成功）、支付成功（退款失败）
        EVENT_TRANSITIONS.put(REFUNDING, Map.of(
            PaymentEvent.REFUND_SUCCESS, REFUNDED,
            PaymentEvent.REFUND_FAILED, SUCCESS
        ));

        // 终态：无事件可触发
        EVENT_TRANSITIONS.put(FAILED, Map.of());
        EVENT_TRANSITIONS.put(REFUNDED, Map.of());
    }

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
     * 根据事件获取目标状态
     *
     * @param event 触发的事件
     * @return 目标状态（Optional）
     */
    public Optional<PaymentStatus> transitionBy(PaymentEvent event) {
        Map<PaymentEvent, PaymentStatus> transitions = EVENT_TRANSITIONS.get(this);
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
    public boolean canTrigger(PaymentEvent event) {
        Map<PaymentEvent, PaymentStatus> transitions = EVENT_TRANSITIONS.get(this);
        return transitions != null && transitions.containsKey(event);
    }

    /**
     * 获取当前状态下可触发的事件列表
     *
     * @return 可触发的事件集合
     */
    public Set<PaymentEvent> getAllowedEvents() {
        Map<PaymentEvent, PaymentStatus> transitions = EVENT_TRANSITIONS.get(this);
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
    public PaymentStatus transition(PaymentEvent event) {
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
    private String buildTransitionErrorMessage(PaymentEvent event) {
        if (isFinal()) {
            return String.format("支付已终态（%s），无法执行【%s】操作", description, event.getDescription());
        }

        Set<PaymentEvent> allowedEvents = getAllowedEvents();
        if (allowedEvents.isEmpty()) {
            return String.format("当前状态（%s）不允许执行任何操作", description);
        }

        String allowedStr = allowedEvents.stream()
            .map(PaymentEvent::getDescription)
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
    public boolean canTransitionTo(PaymentStatus newStatus) {
        return EVENT_TRANSITIONS.get(this).containsValue(newStatus);
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
        return canTrigger(PaymentEvent.APPLY_REFUND);
    }
}
