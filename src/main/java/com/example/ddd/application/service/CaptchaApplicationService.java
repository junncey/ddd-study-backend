package com.example.ddd.application.service;

import com.example.ddd.application.ApplicationService;
import com.example.ddd.infrastructure.security.CaptchaUtil;
import com.example.ddd.interfaces.rest.dto.auth.CaptchaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码应用服务
 *
 * @author DDD Demo
 */
@Slf4j
@Service
public class CaptchaApplicationService extends ApplicationService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 验证码 Redis Key 前缀
     */
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    /**
     * 验证码过期时间（分钟）
     */
    private static final long CAPTCHA_EXPIRE_MINUTES = 5;

    public CaptchaApplicationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成验证码
     *
     * @return 验证码响应
     */
    public CaptchaResponse generateCaptcha() {
        // 生成验证码
        CaptchaUtil.CaptchaResult captchaResult = CaptchaUtil.generateCaptcha();

        // 生成验证码 Key
        String captchaKey = UUID.randomUUID().toString();

        // 将验证码存储到 Redis
        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        redisTemplate.opsForValue().set(
                redisKey,
                captchaResult.getCode(),
                CAPTCHA_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );

        log.debug("生成验证码: {}", captchaKey);

        try {
            // 将图片转换为 Base64
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(captchaResult.getImage(), "png", outputStream);
            String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());

            return CaptchaResponse.builder()
                    .captchaKey(captchaKey)
                    .captchaImage("data:image/png;base64," + base64Image)
                    .build();

        } catch (Exception e) {
            log.error("验证码图片生成失败", e);
            throw new RuntimeException("验证码生成失败");
        }
    }

    /**
     * 验证验证码
     *
     * @param captchaKey 验证码 Key
     * @param captchaCode 用户输入的验证码
     * @return 是否验证成功
     */
    public boolean verifyCaptcha(String captchaKey, String captchaCode) {
        if (captchaKey == null || captchaCode == null) {
            return false;
        }

        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            log.warn("验证码不存在或已过期: {}", captchaKey);
            return false;
        }

        // 验证成功后删除验证码
        redisTemplate.delete(redisKey);

        // 不区分大小写比较
        boolean isValid = storedCode.equalsIgnoreCase(captchaCode);

        if (!isValid) {
            log.warn("验证码验证失败: {} != {}", captchaCode, storedCode);
        }

        return isValid;
    }
}
