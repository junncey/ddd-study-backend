package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ddd.domain.model.valueobject.Money;
import com.example.ddd.infrastructure.persistence.handler.MoneyTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * 购物车明细实体
 *
 * @author DDD Demo
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_cart_item")
public class CartItem extends BaseEntity {

    /**
     * 购物车ID
     */
    private Long cartId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 价格快照（添加时的价格）
     */
    @TableField(typeHandler = MoneyTypeHandler.class)
    private Money priceSnapshot;

    /**
     * 获取价格快照值
     *
     * @return 价格值
     */
    public String getPriceSnapshotValue() {
        return priceSnapshot != null ? priceSnapshot.getValue().toString() : null;
    }

    /**
     * 设置价格快照值
     *
     * @param priceValue 价格值
     */
    public void setPriceSnapshotValue(String priceValue) {
        this.priceSnapshot = priceValue != null ? Money.of(priceValue) : null;
    }

    /**
     * 计算小计金额
     *
     * @return 小计金额
     */
    public Money getSubtotal() {
        return priceSnapshot.multiply(quantity);
    }
}
