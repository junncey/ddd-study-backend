package com.example.ddd.interfaces.rest.converter;

import com.example.ddd.domain.model.entity.Address;
import com.example.ddd.interfaces.rest.dto.AddressCreateRequest;
import com.example.ddd.interfaces.rest.dto.AddressResponse;
import com.example.ddd.interfaces.rest.dto.AddressUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * 地址实体与 DTO 转换器
 *
 * @author DDD Demo
 */
@Mapper(componentModel = "spring")
public interface AddressConverter {

    /**
     * 创建请求转换为实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Address toEntity(AddressCreateRequest request);

    /**
     * 更新请求转换为实体
     */
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Address toEntity(AddressUpdateRequest request);

    /**
     * 实体转换为响应
     */
    @Mapping(source = "entity", target = "fullAddress", qualifiedByName = "getFullAddress")
    AddressResponse toResponse(Address entity);

    /**
     * 获取完整地址
     */
    @Named("getFullAddress")
    default String getFullAddress(Address entity) {
        return entity.getFullAddress();
    }
}
