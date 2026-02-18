package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车明细 Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
}
