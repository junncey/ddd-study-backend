package com.example.ddd.application.service;

import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.User;
import com.example.ddd.domain.service.AuthDomainService;
import com.example.ddd.domain.service.LoginLogDomainService;
import com.example.ddd.infrastructure.security.JwtUtil;
import com.example.ddd.infrastructure.security.UserDetailsImpl;
import com.example.ddd.interfaces.rest.dto.auth.LoginResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证应用服务
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthApplicationService extends ApplicationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthDomainService authDomainService;
    private final LoginLogDomainService loginLogDomainService;

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @param loginIp 登录IP
     * @param userAgent 用户代理
     * @return 登录响应
     */
    public LoginResponse login(String username, String password, String loginIp, String userAgent) {
        log.info("用户登录: {}", username);

        try {
            // 使用 Spring Security 进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // 获取用户信息
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = authDomainService.authenticateUser(username, password);

            // 生成 Token
            String accessToken = jwtUtil.generateToken(username);
            String refreshToken = jwtUtil.generateRefreshToken(username);

            // 获取过期时间
            Long expiresIn = jwtUtil.getTokenRemainingTime(accessToken);

            // 构建用户信息
            LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                    .id(userDetails.getId())
                    .username(userDetails.getUsername())
                    .email(userDetails.getEmail())
                    .phone(user != null ? user.getPhone() : null)
                    .nickname(user != null ? user.getNickname() : null)
                    .build();

            // 记录登录成功日志
            loginLogDomainService.recordLoginSuccess(
                    userDetails.getId(),
                    username,
                    loginIp,
                    userAgent,
                    userAgent
            );

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(expiresIn)
                    .userInfo(userInfo)
                    .build();

        } catch (AuthenticationException e) {
            log.error("用户登录失败: {}", username, e);

            // 记录登录失败日志
            loginLogDomainService.recordLoginFailure(username, loginIp, "用户名或密码错误");

            throw new IllegalArgumentException("用户名或密码错误");
        }
    }

    /**
     * 用户注册
     *
     * @param user 用户对象（包含明文密码）
     * @return 注册后的用户信息
     */
    public User register(User user) {
        log.info("用户注册: {}", user.getUsername());

        // 调用领域服务进行注册
        return authDomainService.registerUser(user);
    }

    /**
     * 刷新 Token
     *
     * @param refreshToken 刷新 Token
     * @return 新的 Token
     */
    public LoginResponse refreshToken(String refreshToken) {
        log.debug("刷新 Token");

        try {
            // 验证 refresh token
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new IllegalArgumentException("刷新Token无效或已过期");
            }

            // 检查是否为 refresh token
            Claims claims = jwtUtil.getAllClaimsFromToken(refreshToken);
            String type = (String) claims.get("type");
            if (!"refresh".equals(type)) {
                throw new IllegalArgumentException("Token类型错误");
            }

            // 获取用户名
            String username = jwtUtil.getUsernameFromToken(refreshToken);

            // 生成新的 access token 和 refresh token
            String newAccessToken = jwtUtil.generateToken(username);
            String newRefreshToken = jwtUtil.generateRefreshToken(username);

            Long expiresIn = jwtUtil.getTokenRemainingTime(newAccessToken);

            return LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(expiresIn)
                    .build();

        } catch (Exception e) {
            log.error("刷新Token失败", e);
            throw new IllegalArgumentException("刷新Token失败: " + e.getMessage());
        }
    }

    /**
     * 用户登出
     *
     * @param username 用户名
     */
    public void logout(String username) {
        log.info("用户登出: {}", username);

        // JWT 是无状态的，登出操作通常由客户端处理（删除 token）
        // 如果需要实现强制登出，可以使用 Redis 存储黑名单
        // 这里只记录日志
    }

    /**
     * 获取当前登录用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    public User getCurrentUser(String username) {
        return authDomainService.authenticateUser(username, null);
    }
}
