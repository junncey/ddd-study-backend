package com.example.ddd.interfaces.rest.exception;

import com.example.ddd.interfaces.rest.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author DDD Demo
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(ForbiddenException.class)
    public Response<?> handleForbiddenException(ForbiddenException e) {
        log.warn("权限不足: {}", e.getMessage());
        return Response.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理未认证异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Response<?> handleUnauthorizedException(UnauthorizedException e) {
        log.warn("未认证: {}", e.getMessage());
        return Response.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理资源不存在异常
     */
    @ExceptionHandler(NotFoundException.class)
    public Response<?> handleNotFoundException(NotFoundException e) {
        log.warn("资源不存在: {}", e.getMessage());
        return Response.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Response<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Response.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return Response.validationError(message);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Response<?> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定失败: {}", message);
        return Response.validationError(message);
    }

    /**
     * 处理非法参数异常
     * 用于处理值对象验证失败的异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Response<?> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("参数验证失败: {}", e.getMessage());
        return Response.fail(400, e.getMessage());
    }

    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public Response<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Response.fail("系统繁忙，请稍后重试");
    }
}
