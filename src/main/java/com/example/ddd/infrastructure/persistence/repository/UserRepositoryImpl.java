package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ddd.domain.model.entity.User;
import com.example.ddd.domain.repository.UserRepository;
import com.example.ddd.infrastructure.persistence.mapper.UserMapper;
import org.springframework.stereotype.Repository;

/**
 * 用户仓储实现
 * 六边形架构的适配器，实现领域层定义的端口
 *
 * @author DDD Demo
 */
@Repository
public class UserRepositoryImpl extends ServiceImpl<UserMapper, User> implements UserRepository {
}
