package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.Role;

/**
 * 角色仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface RoleRepository extends BaseRepository<Role> {

    /**
     * 根据角色编码查询角色
     *
     * @param roleCode 角色编码
     * @return 角色对象
     */
    Role findByRoleCode(String roleCode);

    /**
     * 检查角色编码是否存在
     *
     * @param roleCode 角色编码
     * @return 是否存在
     */
    boolean existsByRoleCode(String roleCode);
}
