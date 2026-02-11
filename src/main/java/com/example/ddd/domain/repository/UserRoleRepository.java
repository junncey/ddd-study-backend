package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.UserRole;

import java.util.List;
import java.util.Set;

/**
 * 用户角色关联仓储接口
 *
 * @author DDD Demo
 */
public interface UserRoleRepository extends BaseRepository<UserRole> {

    /**
     * 根据角色ID查询用户角色关联列表
     *
     * @param roleId 角色ID
     * @return 用户角色关联列表
     */
    List<UserRole> findByRoleId(Long roleId);

    /**
     * 根据用户ID查询用户角色关联列表
     *
     * @param userId 用户ID
     * @return 用户角色关联列表
     */
    List<UserRole> findByUserId(Long userId);

    /**
     * 检查角色是否已分配给用户
     *
     * @param roleId 角色ID
     * @return 是否已分配
     */
    boolean existsByRoleId(Long roleId);

    /**
     * 根据用户ID和角色ID查询关联记录
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 用户角色关联对象
     */
    UserRole findByUserIdAndRoleId(Long userId, Long roleId);

    /**
     * 根据用户ID查询其所有角色的编码集合
     *
     * @param userId 用户ID
     * @return 角色编码集合
     */
    Set<String> findRoleCodesByUserId(Long userId);

    /**
     * 根据用户ID查询其所有权限的编码集合
     * 通过用户的角色获取所有权限
     *
     * @param userId 用户ID
     * @return 权限编码集合
     */
    Set<String> findPermissionCodesByUserId(Long userId);
}
