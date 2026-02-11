package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.RolePermission;
import com.example.ddd.domain.repository.RolePermissionRepository;
import com.example.ddd.infrastructure.persistence.mapper.RolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色权限关联仓储实现
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class RolePermissionRepositoryImpl implements RolePermissionRepository {

    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public RolePermission findById(Long id) {
        return rolePermissionMapper.selectById(id);
    }

    @Override
    public RolePermission save(RolePermission entity) {
        if (entity.getId() == null) {
            rolePermissionMapper.insert(entity);
        } else {
            rolePermissionMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(RolePermission entity) {
        return rolePermissionMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return rolePermissionMapper.deleteById(id);
    }

    @Override
    public IPage<RolePermission> page(Page<RolePermission> page) {
        return rolePermissionMapper.selectPage(page, null);
    }

    @Override
    public List<RolePermission> findByRoleId(Long roleId) {
        return rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId, roleId)
        );
    }

    @Override
    public List<RolePermission> findByPermissionId(Long permissionId) {
        return rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getPermissionId, permissionId)
        );
    }

    @Override
    public boolean existsByPermissionId(Long permissionId) {
        return rolePermissionMapper.selectCount(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getPermissionId, permissionId)
        ) > 0;
    }

    @Override
    public RolePermission findByRoleIdAndPermissionId(Long roleId, Long permissionId) {
        return rolePermissionMapper.selectOne(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId, roleId)
                        .eq(RolePermission::getPermissionId, permissionId)
        );
    }
}
