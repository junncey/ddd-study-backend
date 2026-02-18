package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.Shop;
import org.apache.ibatis.annotations.Mapper;

/**
 * 店铺 Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface ShopMapper extends BaseMapper<Shop> {
}
