package com.example.ddd.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.User;
import com.example.ddd.domain.repository.UserRepository;
import com.example.ddd.domain.service.UserDomainService;
import com.example.ddd.interfaces.rest.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 用户应用服务
 * 编排领域对象，处理用例逻辑
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserApplicationService extends ApplicationService {

    private final UserDomainService userDomainService;
    private final UserRepository userRepository;

    /**
     * 创建用户用例
     *
     * @param user 用户对象
     * @return 创建的用户
     */
    @CacheEvict(value = "users", allEntries = true)
    public User createUser(User user) {
        beforeExecute();
        try {
            return userDomainService.createUser(user);
        } finally {
            afterExecute();
        }
    }

    /**
     * 更新用户用例
     *
     * @param user 用户对象
     * @return 更新的用户
     */
    @CacheEvict(value = "user", key = "#user.id")
    public User updateUser(User user) {
        beforeExecute();
        try {
            return userDomainService.updateUser(user);
        } finally {
            afterExecute();
        }
    }

    /**
     * 删除用户用例
     *
     * @param id 用户ID
     */
    @CacheEvict(value = {"user", "users"}, allEntries = true)
    public void deleteUser(Long id) {
        beforeExecute();
        try {
            userDomainService.deleteUser(id);
        } finally {
            afterExecute();
        }
    }

    /**
     * 查询用户用例
     *
     * @param id 用户ID
     * @return 用户对象
     */
    @Cacheable(value = "user", key = "#id")
    public User getUserById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    @Cacheable(value = "user", key = "'username:' + #username")
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    /**
     * 分页查询用户
     *
     * @param current 当前页
     * @param size    每页大小
     * @return 分页结果
     */
    @Cacheable(value = "users", key = "'page:' + #current + ':' + #size")
    public IPage<User> pageUsers(Long current, Long size) {
        return userRepository.page(new Page<>(current, size));
    }
}
