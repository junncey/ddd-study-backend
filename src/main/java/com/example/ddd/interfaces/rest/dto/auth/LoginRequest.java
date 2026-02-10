package com.example.ddd.interfaces.rest.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求 DTO
 *
 * @author DDD Demo
 */
@Data
public class LoginRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    private String captchaCode;

    /**
     * 验证码 Key
     */
    private String captchaKey;

    /**
     * 是否记住我
     */
    private Boolean rememberMe = false;
}
