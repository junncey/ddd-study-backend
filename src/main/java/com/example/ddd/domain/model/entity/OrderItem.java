package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ddd.domain.model.valueobject.Money;
import com.example.ddd.infrastructure.persistence.handler.MoneyTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * 订单明细实体
 *
 * @author DDD Demo
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_item")
public class OrderItem extends BaseEntity {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SKU名称
     */
    private String skuName;

    /**
     * 规格属性
     */
    private String specs;

    /**
     * 单价（快照）
     */
    @TableField(typeHandler = MoneyTypeHandler.class)
    private Money price;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 小计金额
     */
    @TableField(typeHandler = MoneyTypeHandler.class)
    private Money totalAmount;

    /**
     * 获取价格值
     *
     * @return 价格值
     */
    public String getPriceValue() {
        return price != null ? price.getValue().toString() : null;
    }

    /**
     * 设置价格值
     *
     * @param priceValue 价格值
     */
    public void setPriceValue(String priceValue) {
        this.price = priceValue != null ? Money.of(priceValue) : null;
    }

    /**
     * 获取小计金额值
     *
     * @return 小计金额值
     */
    public String getTotalAmountValue() {
        return totalAmount != null ? totalAmount.getValue().toString() : null;
    }

    /**
     * 设置小计金额值
     *
     * @param amountValue 金额值
     */
    public void setTotalAmountValue(String amountValue) {
        this.totalAmount = amountValue != null ? Money.of(amountValue) : null;
    }

    /**
     * 计算小计金额
     */
    public void calculateTotalAmount() {
        if (price != null && quantity != null) {
            this.totalAmount = price.multiply(quantity);
        }
    }
}
