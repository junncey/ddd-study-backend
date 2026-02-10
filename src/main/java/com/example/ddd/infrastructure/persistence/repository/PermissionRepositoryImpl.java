package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Permission;
import com.example.ddd.domain.repository.PermissionRepository;
import com.example.ddd.infrastructure.persistence.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 权限仓储实现
 * 六边形架构的适配器，实现领域层定义的端口
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {

    private final PermissionMapper permissionMapper;

    @Override
    public Permission findById(Long id) {
        return permissionMapper.selectById(id);
    }

    @Override
    public Permission save(Permission entity) {
        if (entity.getId() == null) {
            permissionMapper.insert(entity);
        } else {
            permissionMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(Permission entity) {
        return permissionMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return permissionMapper.deleteById(id);
    }

    @Override
    public IPage<Permission> page(Page<Permission> page) {
        return permissionMapper.selectPage(page, null);
    }

    @Override
    public Permission findByPermissionCode(String permissionCode) {
        return permissionMapper.selectOne(
                new LambdaQueryWrapper<Permission>()
                        .eq(Permission::getPermissionCode, permissionCode)
        );
    }

    @Override
    public boolean existsByPermissionCode(String permissionCode) {
        return permissionMapper.selectCount(
                new LambdaQueryWrapper<Permission>()
                        .eq(Permission::getPermissionCode, permissionCode)
        ) > 0;
    }

    @Override
    public List<Permission> findByParentId(Long parentId) {
        return permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>()
                        .eq(Permission::getParentId, parentId)
                        .orderByAsc(Permission::getSort)
        );
    }

    @Override
    public List<Permission> findAll() {
        return permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>()
                        .orderByAsc(Permission::getSort)
        );
    }

    @Override
    public List<Permission> findByPermissionType(Integer permissionType) {
        return permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>()
                        .eq(Permission::getPermissionType, permissionType)
                        .orderByAsc(Permission::getSort)
        );
    }
}
