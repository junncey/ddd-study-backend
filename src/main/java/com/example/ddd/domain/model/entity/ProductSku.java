package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ddd.domain.model.valueobject.Money;
import com.example.ddd.domain.model.valueobject.Quantity;
import com.example.ddd.infrastructure.persistence.handler.MoneyTypeHandler;
import com.example.ddd.infrastructure.persistence.handler.QuantityTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * 商品SKU实体
 *
 * @author DDD Demo
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_product_sku")
public class ProductSku extends BaseEntity {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * SKU名称
     */
    private String skuName;

    /**
     * 规格属性 JSON格式
     */
    private String specs;

    /**
     * 价格
     */
    @TableField(typeHandler = MoneyTypeHandler.class)
    private Money price;

    /**
     * 库存数量
     */
    @TableField(typeHandler = QuantityTypeHandler.class)
    private Quantity stock;

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
     * 获取库存值
     *
     * @return 库存值
     */
    public Integer getStockValue() {
        return stock != null ? stock.getValue() : null;
    }

    /**
     * 设置库存值
     *
     * @param stockValue 库存值
     */
    public void setStockValue(Integer stockValue) {
        this.stock = stockValue != null ? Quantity.of(stockValue) : null;
    }

    /**
     * 判断是否有库存
     *
     * @return true 如果有库存
     */
    public boolean hasStock() {
        return stock != null && stock.isPositive();
    }

    /**
     * 判断库存是否充足
     *
     * @param requiredQuantity 需要的数量
     * @return true 如果库存充足
     */
    public boolean hasEnoughStock(int requiredQuantity) {
        return stock != null && !stock.lessThan(Quantity.of(requiredQuantity));
    }
}
