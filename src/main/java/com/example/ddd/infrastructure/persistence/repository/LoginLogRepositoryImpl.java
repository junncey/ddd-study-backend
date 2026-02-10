package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.LoginLog;
import com.example.ddd.domain.repository.LoginLogRepository;
import com.example.ddd.infrastructure.persistence.mapper.LoginLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 登录日志仓储实现
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class LoginLogRepositoryImpl implements LoginLogRepository {

    private final LoginLogMapper loginLogMapper;

    @Override
    public LoginLog findById(Long id) {
        return loginLogMapper.selectById(id);
    }

    @Override
    public LoginLog save(LoginLog entity) {
        if (entity.getId() == null) {
            loginLogMapper.insert(entity);
        } else {
            loginLogMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(LoginLog entity) {
        return loginLogMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return loginLogMapper.deleteById(id);
    }

    @Override
    public IPage<LoginLog> page(Page<LoginLog> page) {
        return loginLogMapper.selectPage(page, null);
    }
}
