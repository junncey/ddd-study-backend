package com.example.ddd.interfaces.rest.exception;

/**
 * 未认证异常
 * 当用户未登录或 Token 无效时抛出
 *
 * @author DDD Demo
 */
public class UnauthorizedException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
        super(401, message);
    }

    public UnauthorizedException() {
        super(401, "用户未登录或登录已过期");
    }
}
