package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联 Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}
