package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

/**
 * 用户角色关联 Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 查询用户的所有角色编码
     *
     * @param userId 用户ID
     * @return 角色编码集合
     */
    @Select("SELECT r.role_code FROM t_user_role ur " +
            "JOIN t_role r ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    Set<String> findRoleCodesByUserId(Long userId);

    /**
     * 查询用户的所有权限编码（通过角色）
     *
     * @param userId 用户ID
     * @return 权限编码集合
     */
    @Select("SELECT DISTINCT p.permission_code FROM t_user_role ur " +
            "JOIN t_role_permission rp ON ur.role_id = rp.role_id " +
            "JOIN t_permission p ON rp.permission_id = p.id " +
            "WHERE ur.user_id = #{userId} AND p.deleted = 0 AND p.status = 1")
    Set<String> findPermissionCodesByUserId(Long userId);
}
