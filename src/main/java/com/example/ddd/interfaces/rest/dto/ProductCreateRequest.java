package com.example.ddd.interfaces.rest.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品创建请求
 *
 * @author DDD Demo
 */
@Data
public class ProductCreateRequest {

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
     * 价格（用于创建默认SKU）
     */
    private BigDecimal price;

    /**
     * 库存（用于创建默认SKU）
     */
    private Integer stock;

    /**
     * 状态值
     */
    private Integer status;
}
