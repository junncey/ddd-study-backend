package com.example.ddd.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 强密码验证注解
 * 用于验证密码是否符合安全要求
 *
 * @author DDD Demo
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
public @interface StrongPassword {

    /**
     * 错误消息
     */
    String message() default "密码强度不足，密码长度至少8位，且必须包含大写字母、小写字母、数字和特殊字符中的至少3种";

    /**
     * 验证组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     */
    Class<? extends Payload>[] payload() default {};
}
