package com.example.ddd.infrastructure.security;

import com.example.ddd.interfaces.rest.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 安全上下文工具类
 * 用于从 Spring Security 上下文获取当前认证用户的信息
 *
 * @author DDD Demo
 */
@Component
public class SecurityUtil {

    /**
     * 获取当前认证信息
     *
     * @return 认证信息
     * @throws UnauthorizedException 如果用户未认证
     */
    public static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("用户未登录");
        }
        return authentication;
    }

    /**
     * 获取当前登录用户详情
     *
     * @return 用户详情
     * @throws UnauthorizedException 如果用户未认证或认证信息无效
     */
    public static UserDetailsImpl getCurrentUser() {
        Authentication authentication = getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails;
        }

        throw new UnauthorizedException("无法获取用户信息");
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     * @throws UnauthorizedException 如果用户未认证
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名
     * @throws UnauthorizedException 如果用户未认证
     */
    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    /**
     * 获取当前用户邮箱
     *
     * @return 邮箱，可能为 null
     */
    public static String getCurrentUserEmail() {
        UserDetailsImpl user = getCurrentUser();
        return user.getEmail();
    }

    /**
     * 检查当前用户是否已认证
     *
     * @return true 如果已认证
     */
    public static boolean isAuthenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof UserDetailsImpl;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查当前用户是否拥有指定角色
     *
     * @param role 角色名称（如 "ADMIN", "MERCHANT"）
     * @return true 如果拥有该角色
     */
    public static boolean hasRole(String role) {
        return hasAuthority("ROLE_" + role);
    }

    /**
     * 检查当前用户是否拥有指定权限
     *
     * @param authority 权限名称
     * @return true 如果拥有该权限
     */
    public static boolean hasAuthority(String authority) {
        Authentication authentication = getAuthentication();
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (grantedAuthority.getAuthority().equals(authority)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前用户的所有权限
     *
     * @return 权限集合
     */
    public static Set<String> getCurrentUserAuthorities() {
        Authentication authentication = getAuthentication();
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    /**
     * 检查当前用户是否是管理员
     *
     * @return true 如果是管理员
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * 要求当前用户是管理员，否则抛出异常
     *
     * @throws UnauthorizedException 如果不是管理员
     */
    public static void requireAdmin() {
        if (!isAdmin()) {
            throw new UnauthorizedException("需要管理员权限");
        }
    }
}
