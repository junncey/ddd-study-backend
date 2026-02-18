package com.example.ddd.domain.model.valueobject;

/**
 * 商品状态枚举
 *
 * @author DDD Demo
 */
public enum ProductStatus implements StatusType {

    /**
     * 草稿
     */
    DRAFT(0, "草稿"),

    /**
     * 在售
     */
    ON_SALE(1, "在售"),

    /**
     * 售罄
     */
    SOLD_OUT(2, "售罄"),

    /**
     * 下架
     */
    OFFLINE(3, "下架");

    private final Integer value;
    private final String description;

    ProductStatus(Integer value, String description) {
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
     * @return 商品状态枚举
     */
    public static ProductStatus fromValue(Integer value) {
        if (value == null) {
            return DRAFT;
        }
        for (ProductStatus status : ProductStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的商品状态值: " + value);
    }

    /**
     * 判断是否可以上架
     *
     * @return true 如果可以上架
     */
    public boolean canOnSale() {
        return this == DRAFT || this == OFFLINE;
    }

    /**
     * 判断是否可以下架
     *
     * @return true 如果可以下架
     */
    public boolean canOffline() {
        return this == ON_SALE || this == SOLD_OUT;
    }

    /**
     * 判断是否可以购买
     *
     * @return true 如果可以购买
     */
    public boolean canPurchase() {
        return this == ON_SALE;
    }
}
