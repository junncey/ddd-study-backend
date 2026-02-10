package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Role;
import com.example.ddd.domain.repository.RoleRepository;
import com.example.ddd.infrastructure.persistence.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 角色仓储实现
 * 六边形架构的适配器，实现领域层定义的端口
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper roleMapper;

    @Override
    public Role findById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public Role save(Role entity) {
        if (entity.getId() == null) {
            roleMapper.insert(entity);
        } else {
            roleMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(Role entity) {
        return roleMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return roleMapper.deleteById(id);
    }

    @Override
    public IPage<Role> page(Page<Role> page) {
        return roleMapper.selectPage(page, null);
    }

    @Override
    public Role findByRoleCode(String roleCode) {
        return roleMapper.selectOne(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getRoleCode, roleCode)
        );
    }

    @Override
    public boolean existsByRoleCode(String roleCode) {
        return roleMapper.selectCount(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getRoleCode, roleCode)
        ) > 0;
    }
}
