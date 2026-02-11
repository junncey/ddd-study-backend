package com.example.ddd.interfaces.rest.converter;

import com.example.ddd.domain.model.entity.Role;
import com.example.ddd.domain.model.valueobject.RoleStatus;
import com.example.ddd.domain.model.valueobject.Status;
import com.example.ddd.interfaces.rest.dto.RoleCreateRequest;
import com.example.ddd.interfaces.rest.dto.RoleResponse;
import com.example.ddd.interfaces.rest.dto.RoleUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 角色实体与 DTO 转换器
 *
 * @author DDD Demo
 */
@Mapper(componentModel = "spring", uses = ValueObjectMapper.class)
public interface RoleConverter {

    /**
     * 创建请求转换为实体
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "intToRoleStatus")
    Role toEntity(RoleCreateRequest request);

    /**
     * 更新请求转换为实体
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "intToRoleStatus")
    Role toEntity(RoleUpdateRequest request);

    /**
     * 实体转换为响应
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "roleStatusToInt")
    RoleResponse toResponse(Role role);
}
