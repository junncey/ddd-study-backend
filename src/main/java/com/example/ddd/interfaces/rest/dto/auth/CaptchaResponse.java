package com.example.ddd.interfaces.rest.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码响应 VO
 *
 * @author DDD Demo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaResponse {

    /**
     * 验证码 Key
     */
    private String captchaKey;

    /**
     * 验证码图片（Base64）
     */
    private String captchaImage;
}
