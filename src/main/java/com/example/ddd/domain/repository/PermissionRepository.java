package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.Permission;

import java.util.List;

/**
 * 权限仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface PermissionRepository extends BaseRepository<Permission> {

    /**
     * 根据权限编码查询权限
     *
     * @param permissionCode 权限编码
     * @return 权限对象
     */
    Permission findByPermissionCode(String permissionCode);

    /**
     * 检查权限编码是否存在
     *
     * @param permissionCode 权限编码
     * @return 是否存在
     */
    boolean existsByPermissionCode(String permissionCode);

    /**
     * 根据父权限ID查询子权限列表
     *
     * @param parentId 父权限ID
     * @return 子权限列表
     */
    List<Permission> findByParentId(Long parentId);

    /**
     * 查询所有权限（树形结构）
     *
     * @return 所有权限列表
     */
    List<Permission> findAll();

    /**
     * 根据权限类型查询权限列表
     *
     * @param permissionType 权限类型 1-菜单 2-按钮 3-接口
     * @return 权限列表
     */
    List<Permission> findByPermissionType(Integer permissionType);
}
