package com.example.ddd.domain.model.valueobject;

/**
 * 店铺状态枚举
 *
 * @author DDD Demo
 */
public enum ShopStatus implements StatusType {

    /**
     * 待审核
     */
    PENDING(0, "待审核"),

    /**
     * 已审核
     */
    APPROVED(1, "已审核"),

    /**
     * 已拒绝
     */
    REJECTED(2, "已拒绝"),

    /**
     * 已暂停
     */
    SUSPENDED(3, "已暂停"),

    /**
     * 已关闭
     */
    CLOSED(4, "已关闭");

    private final Integer value;
    private final String description;

    ShopStatus(Integer value, String description) {
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
     * @return 店铺状态枚举
     */
    public static ShopStatus fromValue(Integer value) {
        if (value == null) {
            return PENDING;
        }
        for (ShopStatus status : ShopStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的店铺状态值: " + value);
    }

    /**
     * 判断是否可以流转到指定状态
     *
     * @param newStatus 新状态
     * @return true 如果可以流转
     */
    public boolean canTransitionTo(ShopStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == APPROVED || newStatus == REJECTED;
            case APPROVED -> newStatus == SUSPENDED || newStatus == CLOSED;
            case SUSPENDED -> newStatus == APPROVED || newStatus == CLOSED;
            case REJECTED, CLOSED -> false; // 终态，不能再流转
        };
    }

    /**
     * 判断是否可以经营
     *
     * @return true 如果可以经营
     */
    public boolean canOperate() {
        return this == APPROVED;
    }

    /**
     * 判断是否可以暂停
     *
     * @return true 如果可以暂停
     */
    public boolean canSuspend() {
        return this == APPROVED;
    }

    /**
     * 判断是否可以关闭
     *
     * @return true 如果可以关闭
     */
    public boolean canClose() {
        return this == APPROVED || this == SUSPENDED;
    }
}
