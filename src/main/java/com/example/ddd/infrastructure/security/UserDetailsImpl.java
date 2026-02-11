package com.example.ddd.infrastructure.security;

import com.example.ddd.domain.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
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
 * @author DDD Demo
 */
@Data
@AllArgsConstructor
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
     * 密码
     */
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
     * 权限列表
     */
    private Set<GrantedAuthority> authorities;

    /**
     * 从 User 实体创建 UserDetails
     */
    public static UserDetailsImpl create(User user) {
        Set<GrantedAuthority> authorities = Collections.emptySet();

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail() != null ? user.getEmail().getValue() : null,
                user.getStatus() != null ? user.getStatus().getValue() : null,
                authorities
        );
    }

    /**
     * 从 User 实体和权限编码列表创建 UserDetails
     */
    public static UserDetailsImpl createWithAuthorities(User user, Set<String> authorityCodes) {
        Set<GrantedAuthority> authorities = authorityCodes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail() != null ? user.getEmail().getValue() : null,
                user.getStatus() != null ? user.getStatus().getValue() : null,
                authorities
        );
    }

    /**
     * 从 User 实体和权限列表创建 UserDetails
     */
    public static UserDetailsImpl create(User user, Set<GrantedAuthority> authorities) {
        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail() != null ? user.getEmail().getValue() : null,
                user.getStatus() != null ? user.getStatus().getValue() : null,
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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

    /**
     * 获取用户ID
     */
    public Long getId() {
        return id;
    }
}
