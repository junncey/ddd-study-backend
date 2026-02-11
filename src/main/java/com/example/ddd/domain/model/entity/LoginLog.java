package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ddd.domain.model.valueobject.LoginStatus;
import com.example.ddd.domain.model.valueobject.Status;
import com.example.ddd.infrastructure.persistence.handler.LoginStatusTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 登录日志实体
 *
 * @author DDD Demo
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_login_log")
public class LoginLog extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录状态 0-失败 1-成功
     */
    @TableField(typeHandler = LoginStatusTypeHandler.class)
    private Status<LoginStatus> status;

    /**
     * 提示信息
     */
    private String message;
}
