package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.RolePermission;

import java.util.List;

/**
 * 角色权限关联仓储接口
 *
 * @author DDD Demo
 */
public interface RolePermissionRepository extends BaseRepository<RolePermission> {

    /**
     * 根据角色ID查询角色权限关联列表
     *
     * @param roleId 角色ID
     * @return 角色权限关联列表
     */
    List<RolePermission> findByRoleId(Long roleId);

    /**
     * 根据权限ID查询角色权限关联列表
     *
     * @param permissionId 权限ID
     * @return 角色权限关联列表
     */
    List<RolePermission> findByPermissionId(Long permissionId);

    /**
     * 检查权限是否已分配给角色
     *
     * @param permissionId 权限ID
     * @return 是否已分配
     */
    boolean existsByPermissionId(Long permissionId);

    /**
     * 根据角色ID和权限ID查询关联记录
     *
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 角色权限关联对象
     */
    RolePermission findByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
