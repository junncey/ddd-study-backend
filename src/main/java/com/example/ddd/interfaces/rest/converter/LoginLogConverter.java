package com.example.ddd.interfaces.rest.converter;

import com.example.ddd.domain.model.entity.LoginLog;
import com.example.ddd.domain.model.valueobject.LoginStatus;
import com.example.ddd.domain.model.valueobject.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 登录日志实体与 DTO 转换器
 *
 * @author DDD Demo
 */
@Mapper(componentModel = "spring", uses = ValueObjectMapper.class)
public interface LoginLogConverter {

    /**
     * 实体转换为响应
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "loginStatusToInt")
    LoginLogResponse toResponse(LoginLog entity);

    /**
     * 登录日志响应 DTO（内部类）
     */
    class LoginLogResponse {
        private Long id;
        private Long userId;
        private String username;
        private String loginIp;
        private String loginLocation;
        private String browser;
        private String os;
        private Integer status;
        private String message;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getLoginIp() {
            return loginIp;
        }

        public void setLoginIp(String loginIp) {
            this.loginIp = loginIp;
        }

        public String getLoginLocation() {
            return loginLocation;
        }

        public void setLoginLocation(String loginLocation) {
            this.loginLocation = loginLocation;
        }

        public String getBrowser() {
            return browser;
        }

        public void setBrowser(String browser) {
            this.browser = browser;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
