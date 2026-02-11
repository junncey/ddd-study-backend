package com.example.ddd.interfaces.rest.converter;

import com.example.ddd.domain.model.entity.User;
import com.example.ddd.domain.model.valueobject.Email;
import com.example.ddd.domain.model.valueobject.PhoneNumber;
import com.example.ddd.domain.model.valueobject.Status;
import com.example.ddd.domain.model.valueobject.UserStatus;
import com.example.ddd.interfaces.rest.dto.UserCreateRequest;
import com.example.ddd.interfaces.rest.dto.UserResponse;
import com.example.ddd.interfaces.rest.dto.UserUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * 用户实体与 DTO 转换器
 *
 * @author DDD Demo
 */
@Mapper(componentModel = "spring", uses = ValueObjectMapper.class)
public interface UserConverter {

    /**
     * 创建请求转换为实体
     * UserCreateRequest 没有 status 字段，需要在 controller 中设置
     */
    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmail")
    @Mapping(target = "phone", source = "phone", qualifiedByName = "stringToPhoneNumber")
    @Mapping(target = "status", ignore = true)
    User toEntity(UserCreateRequest request);

    /**
     * 更新请求转换为实体
     */
    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmail")
    @Mapping(target = "phone", source = "phone", qualifiedByName = "stringToPhoneNumber")
    @Mapping(target = "status", source = "status", qualifiedByName = "intToUserStatus")
    User toEntity(UserUpdateRequest request);

    /**
     * 实体转换为响应
     */
    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    @Mapping(target = "phone", source = "phone", qualifiedByName = "phoneNumberToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "userStatusToInt")
    UserResponse toResponse(User user);
}
