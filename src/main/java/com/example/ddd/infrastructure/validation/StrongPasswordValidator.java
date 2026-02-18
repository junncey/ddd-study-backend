package com.example.ddd.infrastructure.validation;

import com.example.ddd.infrastructure.security.PasswordValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 强密码验证器实现
 *
 * @author DDD Demo
 */
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        // 初始化方法，可以获取注解参数
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // 允许为空（由@NotBlank等注解处理）
        if (password == null || password.isEmpty()) {
            return true;
        }

        PasswordValidator.ValidationResult result = PasswordValidator.validate(password);

        if (!result.isValid()) {
            // 禁用默认消息，使用自定义消息
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(result.getMessage())
                    .addConstraintViolation();
        }

        return result.isValid();
    }
}
