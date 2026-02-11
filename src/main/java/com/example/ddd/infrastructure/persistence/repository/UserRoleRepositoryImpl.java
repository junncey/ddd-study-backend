package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.UserRole;
import com.example.ddd.domain.repository.UserRoleRepository;
import com.example.ddd.infrastructure.persistence.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 用户角色关联仓储实现
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class UserRoleRepositoryImpl implements UserRoleRepository {

    private final UserRoleMapper userRoleMapper;

    @Override
    public UserRole findById(Long id) {
        return userRoleMapper.selectById(id);
    }

    @Override
    public UserRole save(UserRole entity) {
        if (entity.getId() == null) {
            userRoleMapper.insert(entity);
        } else {
            userRoleMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(UserRole entity) {
        return userRoleMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return userRoleMapper.deleteById(id);
    }

    @Override
    public IPage<UserRole> page(Page<UserRole> page) {
        return userRoleMapper.selectPage(page, null);
    }

    @Override
    public List<UserRole> findByRoleId(Long roleId) {
        return userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getRoleId, roleId)
        );
    }

    @Override
    public List<UserRole> findByUserId(Long userId) {
        return userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
        );
    }

    @Override
    public boolean existsByRoleId(Long roleId) {
        return userRoleMapper.selectCount(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getRoleId, roleId)
        ) > 0;
    }

    @Override
    public UserRole findByUserIdAndRoleId(Long userId, Long roleId) {
        return userRoleMapper.selectOne(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
                        .eq(UserRole::getRoleId, roleId)
        );
    }

    @Override
    public Set<String> findRoleCodesByUserId(Long userId) {
        return userRoleMapper.findRoleCodesByUserId(userId);
    }

    @Override
    public Set<String> findPermissionCodesByUserId(Long userId) {
        return userRoleMapper.findPermissionCodesByUserId(userId);
    }
}
