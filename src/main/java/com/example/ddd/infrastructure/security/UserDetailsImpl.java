package com.example.ddd.infrastructure.security;

import com.example.ddd.domain.model.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security 用户详情实现
 *
 * 支持 Redis 缓存序列化/反序列化
 *
 * @author DDD Demo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetailsImpl implements UserDetails {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（JSON 序列化时忽略）
     */
    @JsonIgnore
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 权限编码列表（用于序列化）
     * 注意：不使用 @JsonIgnore，而是使用自定义序列化方式
     */
    private Set<String> authorityCodes;

    /**
     * 获取权限列表（实现 UserDetails 接口）
     */
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorityCodes == null || authorityCodes.isEmpty()) {
            return Collections.emptySet();
        }
        return authorityCodes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    /**
     * 设置权限列表（用于反序列化后的处理）
     */
    public void setAuthorities(Set<GrantedAuthority> authorities) {
        this.authorityCodes = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    /**
     * 从 User 实体创建 UserDetails
     */
    public static UserDetailsImpl create(User user) {
        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail() != null ? user.getEmail().getValue() : null,
                user.getStatus() != null ? user.getStatus().getValue() : null,
                Collections.emptySet()
        );
    }

    /**
     * 从 User 实体和权限编码列表创建 UserDetails
     */
    public static UserDetailsImpl createWithAuthorities(User user, Set<String> authorityCodes) {
        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail() != null ? user.getEmail().getValue() : null,
                user.getStatus() != null ? user.getStatus().getValue() : null,
                authorityCodes
        );
    }

    /**
     * 从 User 实体和权限列表创建 UserDetails
     */
    public static UserDetailsImpl create(User user, Set<GrantedAuthority> authorities) {
        Set<String> authorityCodes = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail() != null ? user.getEmail().getValue() : null,
                user.getStatus() != null ? user.getStatus().getValue() : null,
                authorityCodes
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}
