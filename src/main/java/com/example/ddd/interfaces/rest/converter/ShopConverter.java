package com.example.ddd.interfaces.rest.converter;

import com.example.ddd.domain.model.entity.Shop;
import com.example.ddd.interfaces.rest.dto.ShopCreateRequest;
import com.example.ddd.interfaces.rest.dto.ShopResponse;
import com.example.ddd.interfaces.rest.dto.ShopUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 店铺实体与 DTO 转换器
 *
 * @author DDD Demo
 */
@Mapper(componentModel = "spring")
public interface ShopConverter {

    /**
     * 创建请求转换为实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Shop toEntity(ShopCreateRequest request);

    /**
     * 更新请求转换为实体
     */
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    Shop toEntity(ShopUpdateRequest request);

    /**
     * 实体转换为响应
     */
    @Mapping(source = "status.value", target = "status")
    @Mapping(source = "status.description", target = "statusDesc")
    ShopResponse toResponse(Shop shop);
}
