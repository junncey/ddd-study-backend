package com.example.ddd.interfaces.rest.converter;

import com.example.ddd.domain.model.valueobject.Email;
import com.example.ddd.domain.model.valueobject.LoginStatus;
import com.example.ddd.domain.model.valueobject.PermissionStatus;
import com.example.ddd.domain.model.valueobject.PhoneNumber;
import com.example.ddd.domain.model.valueobject.RoleStatus;
import com.example.ddd.domain.model.valueobject.Status;
import com.example.ddd.domain.model.valueobject.UserStatus;
import org.mapstruct.Named;

/**
 * 值对象映射器
 * 定义值对象与基本类型之间的转换方法
 *
 * @author DDD Demo
 */
public interface ValueObjectMapper {

    // ========== Email 转换 ==========

    /**
     * Email 转换为字符串
     */
    @Named("emailToString")
    default String emailToString(Email email) {
        return email != null ? email.getValue() : null;
    }

    /**
     * 字符串转换为 Email
     */
    @Named("stringToEmail")
    default Email stringToEmail(String email) {
        return email != null ? Email.of(email) : null;
    }

    // ========== PhoneNumber 转换 ==========

    /**
     * PhoneNumber 转换为字符串
     */
    @Named("phoneNumberToString")
    default String phoneNumberToString(PhoneNumber phone) {
        return phone != null ? phone.getValue() : null;
    }

    /**
     * 字符串转换为 PhoneNumber
     */
    @Named("stringToPhoneNumber")
    default PhoneNumber stringToPhoneNumber(String phone) {
        return phone != null ? PhoneNumber.of(phone) : null;
    }

    // ========== UserStatus 转换 ==========

    /**
     * Status<UserStatus> 转换为整数
     */
    @Named("userStatusToInt")
    default Integer userStatusToInt(Status<UserStatus> status) {
        return status != null ? status.getValue() : null;
    }

    /**
     * 整数转换为 Status<UserStatus>
     */
    @Named("intToUserStatus")
    default Status<UserStatus> intToUserStatus(Integer value) {
        return value != null ? Status.ofUser(value) : null;
    }

    // ========== RoleStatus 转换 ==========

    /**
     * Status<RoleStatus> 转换为整数
     */
    @Named("roleStatusToInt")
    default Integer roleStatusToInt(Status<RoleStatus> status) {
        return status != null ? status.getValue() : null;
    }

    /**
     * 整数转换为 Status<RoleStatus>
     */
    @Named("intToRoleStatus")
    default Status<RoleStatus> intToRoleStatus(Integer value) {
        return value != null ? Status.ofRole(value) : null;
    }

    // ========== PermissionStatus 转换 ==========

    /**
     * Status<PermissionStatus> 转换为整数
     */
    @Named("permissionStatusToInt")
    default Integer permissionStatusToInt(Status<PermissionStatus> status) {
        return status != null ? status.getValue() : null;
    }

    /**
     * 整数转换为 Status<PermissionStatus>
     */
    @Named("intToPermissionStatus")
    default Status<PermissionStatus> intToPermissionStatus(Integer value) {
        return value != null ? Status.ofPermission(value) : null;
    }

    // ========== LoginStatus 转换 ==========

    /**
     * Status<LoginStatus> 转换为整数
     */
    @Named("loginStatusToInt")
    default Integer loginStatusToInt(Status<LoginStatus> status) {
        return status != null ? status.getValue() : null;
    }

    /**
     * 整数转换为 Status<LoginStatus>
     */
    @Named("intToLoginStatus")
    default Status<LoginStatus> intToLoginStatus(Integer value) {
        return value != null ? Status.ofLogin(value) : null;
    }
}
