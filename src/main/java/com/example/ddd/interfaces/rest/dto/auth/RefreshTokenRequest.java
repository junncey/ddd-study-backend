package com.example.ddd.interfaces.rest.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新 Token 请求 DTO
 *
 * @author DDD Demo
 */
@Data
public class RefreshTokenRequest {

    /**
     * 刷新 Token
     */
    @NotBlank(message = "刷新Token不能为空")
    private String refreshToken;
}
