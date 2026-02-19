package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ddd.domain.model.valueobject.ProductStatus;
import com.example.ddd.infrastructure.persistence.handler.ProductStatusTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品实体
 * 图片信息统一使用 t_file 表管理，通过 fileRepository 查询关联图片
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
     * 商品状态
     */
    @TableField(typeHandler = ProductStatusTypeHandler.class)
    private ProductStatus status;

    // ==================== 非持久化字段（用于前端显示）====================

    /**
     * 最低价格（从SKU聚合，非持久化）
     */
    @TableField(exist = false)
    private BigDecimal minPrice;

    /**
     * 总库存（从SKU聚合，非持久化）
     */
    @TableField(exist = false)
    private Integer totalStock;

    /**
     * 分类名称（非持久化，用于显示）
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 商品主图URL（非持久化，从文件服务获取）
     */
    @TableField(exist = false)
    private String mainImage;

    /**
     * 商品图片列表（非持久化，从 t_file 表查询）
     */
    @TableField(exist = false)
    private List<String> imageFileKeys;

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
