package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限 Mapper
 * 六边形架构的适配器
 *
 * @author DDD Demo
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}
