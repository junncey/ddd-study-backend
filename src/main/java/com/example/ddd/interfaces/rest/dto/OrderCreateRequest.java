package com.example.ddd.interfaces.rest.dto;

import lombok.Data;

import java.util.List;

/**
 * 创建订单请求
 *
 * @author DDD Demo
 */
@Data
public class OrderCreateRequest {

    /**
     * 收货地址ID
     */
    private Long addressId;

    /**
     * 购物车项ID列表
     */
    private List<Long> cartItemIds;

    /**
     * 订单备注
     */
    private String remark;
}
