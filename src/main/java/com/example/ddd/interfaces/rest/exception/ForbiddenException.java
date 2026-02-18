package com.example.ddd.interfaces.rest.exception;

/**
 * 权限不足异常
 * 当用户尝试访问不属于自己或无权操作的资源时抛出
 *
 * @author DDD Demo
 */
public class ForbiddenException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public ForbiddenException(String message) {
        super(403, message);
    }

    public ForbiddenException() {
        super(403, "无权访问该资源");
    }
}
