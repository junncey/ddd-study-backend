package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.Address;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地址 Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}
