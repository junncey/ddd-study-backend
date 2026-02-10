package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.User;

/**
 * 用户仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface UserRepository extends BaseRepository<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象
     */
    User findByEmail(String email);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
}
