package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ddd.domain.model.valueobject.Email;
import com.example.ddd.domain.model.valueobject.PhoneNumber;
import com.example.ddd.domain.model.valueobject.Status;
import com.example.ddd.domain.model.valueobject.UserStatus;
import com.example.ddd.infrastructure.persistence.handler.EmailTypeHandler;
import com.example.ddd.infrastructure.persistence.handler.PhoneNumberTypeHandler;
import com.example.ddd.infrastructure.persistence.handler.UserStatusTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 *
 * @author DDD Demo
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class User extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（序列化时忽略，防止泄露）
     */
    @JsonIgnore
    private String password;

    /**
     * 邮箱
     */
    @TableField(typeHandler = EmailTypeHandler.class)
    private Email email;

    /**
     * 手机号
     */
    @TableField(typeHandler = PhoneNumberTypeHandler.class)
    private PhoneNumber phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 状态 0-禁用 1-启用
     */
    @TableField(typeHandler = UserStatusTypeHandler.class)
    private Status<UserStatus> status;

    /**
     * 设置邮箱值
     *
     * @param emailString 邮箱字符串
     */
    public void setEmailString(String emailString) {
        this.email = emailString != null ? Email.of(emailString) : null;
    }

    /**
     * 获取邮箱值
     *
     * @return 邮箱字符串
     */
    public String getEmailString() {
        return email != null ? email.getValue() : null;
    }

    /**
     * 设置手机号值
     *
     * @param phoneString 手机号字符串
     */
    public void setPhoneString(String phoneString) {
        this.phone = phoneString != null ? PhoneNumber.of(phoneString) : null;
    }

    /**
     * 获取手机号值
     *
     * @return 手机号字符串
     */
    public String getPhoneString() {
        return phone != null ? phone.getValue() : null;
    }

    /**
     * 设置状态值
     *
     * @param statusInt 状态值
     */
    public void setStatusInt(Integer statusInt) {
        this.status = statusInt != null ? Status.ofUser(statusInt) : null;
    }

    /**
     * 获取状态值
     *
     * @return 状态值
     */
    public Integer getStatusInt() {
        return status != null ? status.getValue() : null;
    }
}
