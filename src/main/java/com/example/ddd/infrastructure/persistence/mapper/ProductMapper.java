package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品 Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
