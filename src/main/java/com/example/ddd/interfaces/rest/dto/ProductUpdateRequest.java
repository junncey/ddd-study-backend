package com.example.ddd.interfaces.rest.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品更新请求
 *
 * @author DDD Demo
 */
@Data
public class ProductUpdateRequest {

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
     * 价格（用于更新默认SKU）
     */
    private BigDecimal price;

    /**
     * 库存（用于更新默认SKU）
     */
    private Integer stock;

    /**
     * 状态值
     */
    private Integer status;
}
