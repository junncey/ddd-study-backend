package com.example.ddd.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 店铺审核请求 DTO
 *
 * @author DDD Demo
 */
@Data
public class ShopApproveRequest {

    /**
     * 店铺ID
     */
    @NotNull(message = "店铺ID不能为空")
    private Long shopId;

    /**
     * 审核结果 1-通过 2-拒绝
     */
    @NotNull(message = "审核结果不能为空")
    private Integer status;
}
