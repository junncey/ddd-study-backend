package com.example.ddd.infrastructure.security;

import com.example.ddd.domain.model.entity.User;
import com.example.ddd.domain.repository.UserRepository;
import com.example.ddd.domain.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
/**
 * Spring Security 用户详情服务实现
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("加载用户信息: {}", username);

        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 检查用户状态
        if (user.getStatus() == null || !user.getStatus().isEnabled()) {
            log.error("用户已被禁用: {}", username);
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 加载用户的角色和权限
        Set<String> roleCodes = userRoleRepository.findRoleCodesByUserId(user.getId());
        Set<String> permissionCodes = userRoleRepository.findPermissionCodesByUserId(user.getId());

        // 合并角色和权限
        Set<String> authorities = new java.util.HashSet<>();
        authorities.addAll(roleCodes);
        authorities.addAll(permissionCodes);

        log.debug("用户 {} 的权限: {}", username, authorities);

        return UserDetailsImpl.createWithAuthorities(user, authorities);
    }
}
