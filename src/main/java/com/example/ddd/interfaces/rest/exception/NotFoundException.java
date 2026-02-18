package com.example.ddd.interfaces.rest.exception;

/**
 * 资源不存在异常
 * 当请求的资源不存在时抛出
 *
 * @author DDD Demo
 */
public class NotFoundException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(404, message);
    }

    public NotFoundException(String resourceType, Long id) {
        super(404, resourceType + "不存在: " + id);
    }
}
