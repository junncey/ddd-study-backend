package com.example.ddd.domain.model.valueobject;

import java.util.Objects;
import java.util.function.Function;

/**
 * 状态值对象
 * 使用泛型支持多种状态类型
 *
 * @param <T> 状态枚举类型
 * @author DDD Demo
 */
public class Status<T extends Enum<T> & StatusType> extends BaseValueObject {

    private final Integer value;
    private final String description;
    private final T enumValue;

    private Status(Integer value, String description, T enumValue) {
        this.value = value;
        this.description = description;
        this.enumValue = enumValue;
    }

    /**
     * 创建用户状态值对象
     *
     * @param value 状态值
     * @return 用户状态值对象
     */
    public static Status<UserStatus> ofUser(Integer value) {
        UserStatus status = UserStatus.fromValue(value);
        return new Status<>(value, status.getDescription(), status);
    }

    /**
     * 创建用户状态值对象（从枚举）
     *
     * @param status 用户状态枚举
     * @return 用户状态值对象
     */
    public static Status<UserStatus> ofUser(UserStatus status) {
        return new Status<>(status.getValue(), status.getDescription(), status);
    }

    /**
     * 创建角色状态值对象
     *
     * @param value 状态值
     * @return 角色状态值对象
     */
    public static Status<RoleStatus> ofRole(Integer value) {
        RoleStatus status = RoleStatus.fromValue(value);
        return new Status<>(value, status.getDescription(), status);
    }

    /**
     * 创建角色状态值对象（从枚举）
     *
     * @param status 角色状态枚举
     * @return 角色状态值对象
     */
    public static Status<RoleStatus> ofRole(RoleStatus status) {
        return new Status<>(status.getValue(), status.getDescription(), status);
    }

    /**
     * 创建权限状态值对象
     *
     * @param value 状态值
     * @return 权限状态值对象
     */
    public static Status<PermissionStatus> ofPermission(Integer value) {
        PermissionStatus status = PermissionStatus.fromValue(value);
        return new Status<>(value, status.getDescription(), status);
    }

    /**
     * 创建权限状态值对象（从枚举）
     *
     * @param status 权限状态枚举
     * @return 权限状态值对象
     */
    public static Status<PermissionStatus> ofPermission(PermissionStatus status) {
        return new Status<>(status.getValue(), status.getDescription(), status);
    }

    /**
     * 创建登录状态值对象
     *
     * @param value 状态值
     * @return 登录状态值对象
     */
    public static Status<LoginStatus> ofLogin(Integer value) {
        LoginStatus status = LoginStatus.fromValue(value);
        return new Status<>(value, status.getDescription(), status);
    }

    /**
     * 创建登录状态值对象（从枚举）
     *
     * @param status 登录状态枚举
     * @return 登录状态值对象
     */
    public static Status<LoginStatus> ofLogin(LoginStatus status) {
        return new Status<>(status.getValue(), status.getDescription(), status);
    }

    /**
     * 获取状态值
     *
     * @return 状态值
     */
    public Integer getValue() {
        return value;
    }

    /**
     * 获取状态描述
     *
     * @return 状态描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取状态枚举
     *
     * @return 状态枚举
     */
    public T getEnumValue() {
        return enumValue;
    }

    /**
     * 判断是否启用
     *
     * @return true 如果状态值为 1
     */
    public boolean isEnabled() {
        return Integer.valueOf(1).equals(value);
    }

    /**
     * 判断是否禁用
     *
     * @return true 如果状态值为 0
     */
    public boolean isDisabled() {
        return Integer.valueOf(0).equals(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Status<?> status = (Status<?>) o;
        return Objects.equals(value, status.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return description + "(" + value + ")";
    }
}
