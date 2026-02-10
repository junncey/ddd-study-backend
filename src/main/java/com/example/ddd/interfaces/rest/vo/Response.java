package com.example.ddd.interfaces.rest.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 *
 * @param <T> 数据类型
 * @author DDD Demo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    public Response() {
        this.timestamp = System.currentTimeMillis();
    }

    public Response(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public Response(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Response<T> success() {
        return new Response<>(200, "操作成功");
    }

    /**
     * 成功响应（带消息）
     */
    public static <T> Response<T> success(String message) {
        return new Response<>(200, message);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "操作成功", data);
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> Response<T> success(String message, T data) {
        return new Response<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> Response<T> fail(String message) {
        return new Response<>(500, message);
    }

    /**
     * 失败响应（带错误码）
     */
    public static <T> Response<T> fail(Integer code, String message) {
        return new Response<>(code, message);
    }

    /**
     * 参数校验失败
     */
    public static <T> Response<T> validationError(String message) {
        return new Response<>(400, message);
    }
}
