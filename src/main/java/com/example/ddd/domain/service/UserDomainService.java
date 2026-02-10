package com.example.ddd.domain.service;

import com.example.ddd.domain.model.entity.User;
import com.example.ddd.domain.repository.UserRepository;
import com.example.ddd.interfaces.rest.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户领域服务
 * 包含核心业务逻辑
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDomainService extends DomainService {

    private final UserRepository userRepository;

    /**
     * 创建用户
     * 包含业务规则验证
     *
     * @param user 用户对象
     * @return 创建的用户
     */
    @Transactional(rollbackFor = Exception.class)
    public User createUser(User user) {
        // 业务规则验证
        validate();

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 保存用户
        userRepository.save(user);

        // 发布领域事件
        publishEvent("UserCreated: " + user.getId());

        return user;
    }

    /**
     * 更新用户
     *
     * @param user 用户对象
     * @return 更新的用户
     */
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user) {
        // 检查用户是否存在
        User existingUser = userRepository.findById(user.getId());
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新用户
        userRepository.update(user);

        return user;
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        // 检查用户是否存在
        User existingUser = userRepository.findById(id);
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 删除用户
        userRepository.delete(id);

        // 发布领域事件
        publishEvent("UserDeleted: " + id);
    }
}
