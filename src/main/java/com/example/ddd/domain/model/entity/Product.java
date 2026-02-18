package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ddd.domain.model.valueobject.Money;
import com.example.ddd.domain.model.valueobject.ProductStatus;
import com.example.ddd.infrastructure.persistence.handler.MoneyTypeHandler;
import com.example.ddd.infrastructure.persistence.handler.ProductStatusTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * 商品实体
 *
 * @author DDD Demo
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_product")
public class Product extends BaseEntity {

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品描述
     */
    private String productDesc;

    /**
     * 主图
     */
    private String mainImage;

    /**
     * 商品状态
     */
    @TableField(typeHandler = ProductStatusTypeHandler.class)
    private ProductStatus status;

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
        this.status = statusInt != null ? ProductStatus.fromValue(statusInt) : null;
    }

    /**
     * 判断是否在售
     *
     * @return true 如果在售
     */
    public boolean isOnSale() {
        return status == ProductStatus.ON_SALE;
    }

    /**
     * 判断可以购买
     *
     * @return true 如果可以购买
     */
    public boolean canPurchase() {
        return status != null && status.canPurchase();
    }
}
