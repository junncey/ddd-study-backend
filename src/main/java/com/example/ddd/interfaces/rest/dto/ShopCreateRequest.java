package com.example.ddd.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建店铺请求 DTO
 *
 * @author DDD Demo
 */
@Data
public class ShopCreateRequest {

    /**
     * 店铺名称
     */
    @NotBlank(message = "店铺名称不能为空")
    private String shopName;

    /**
     * 店铺Logo
     */
    private String shopLogo;

    /**
     * 店铺描述
     */
    private String description;

    /**
     * 店主ID
     */
    @NotNull(message = "店主ID不能为空")
    private Long ownerId;
}
