package com.example.ddd.interfaces.rest.dto;

import lombok.Data;

/**
 * 添加到购物车请求
 *
 * @author DDD Demo
 */
@Data
public class AddToCartRequest {

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 数量
     */
    private Integer quantity;
}
