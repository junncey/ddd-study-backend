package com.example.ddd.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新店铺请求 DTO
 *
 * @author DDD Demo
 */
@Data
public class ShopUpdateRequest {

    /**
     * 店铺ID
     */
    @NotNull(message = "店铺ID不能为空")
    private Long id;

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
}
