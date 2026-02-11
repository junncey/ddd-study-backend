package com.example.ddd.interfaces.rest.converter;

import com.example.ddd.domain.model.entity.Permission;
import com.example.ddd.domain.model.valueobject.PermissionStatus;
import com.example.ddd.domain.model.valueobject.Status;
import com.example.ddd.interfaces.rest.dto.PermissionCreateRequest;
import com.example.ddd.interfaces.rest.dto.PermissionResponse;
import com.example.ddd.interfaces.rest.dto.PermissionUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 权限实体与 DTO 转换器
 *
 * @author DDD Demo
 */
@Mapper(componentModel = "spring", uses = ValueObjectMapper.class)
public interface PermissionConverter {

    /**
     * 创建请求转换为实体
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "intToPermissionStatus")
    Permission toEntity(PermissionCreateRequest request);

    /**
     * 更新请求转换为实体
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "intToPermissionStatus")
    Permission toEntity(PermissionUpdateRequest request);

    /**
     * 实体转换为响应
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "permissionStatusToInt")
    @Mapping(target = "permissionTypeDesc", expression = "java(getPermissionTypeDesc(entity.getPermissionType()))")
    PermissionResponse toResponse(Permission entity);

    /**
     * 获取权限类型描述
     */
    default String getPermissionTypeDesc(Integer permissionType) {
        if (permissionType == null) {
            return null;
        }
        return switch (permissionType) {
            case 1 -> "菜单";
            case 2 -> "按钮";
            case 3 -> "接口";
            default -> "未知";
        };
    }
}
