package com.example.ddd.domain.service;

import com.example.ddd.domain.model.entity.User;
import com.example.ddd.domain.model.valueobject.Email;
import com.example.ddd.domain.model.valueobject.PhoneNumber;
import com.example.ddd.domain.model.valueobject.Status;
import com.example.ddd.domain.model.valueobject.UserStatus;
import com.example.ddd.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 认证领域服务
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthDomainService extends DomainService {

    private final UserRepository userRepository;

    /**
     * 验证用户登录信息
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户对象，验证失败返回 null
     */
    public User authenticateUser(String username, String password) {
        log.debug("验证用户登录信息: {}", username);

        User user = userRepository.findByUsername(username);

        if (user == null) {
            log.warn("用户不存在: {}", username);
            return null;
        }

        // 检查用户状态
        if (user.getStatus() == null || !user.getStatus().isEnabled()) {
            log.warn("用户已被禁用: {}", username);
            return null;
        }

        // 密码验证由 Spring Security 的 AuthenticationManager 处理
        // 这里只返回用户对象
        return user;
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    public boolean checkEmailExists(Email email) {
        return userRepository.findByEmail(email.getValue()) != null;
    }

    /**
     * 用户注册
     *
     * @param user 用户对象
     * @return 注册后的用户对象
     */
    public User registerUser(User user) {
        log.debug("用户注册: {}", user.getUsername());

        // 检查用户名是否已存在
        if (checkUsernameExists(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (user.getEmail() != null && checkEmailExists(user.getEmail())) {
            throw new IllegalArgumentException("邮箱已被注册");
        }

        // 设置默认状态为启用
        if (user.getStatus() == null) {
            user.setStatus(Status.ofUser(UserStatus.ENABLED));
        }

        // 保存用户
        return userRepository.save(user);
    }
}
