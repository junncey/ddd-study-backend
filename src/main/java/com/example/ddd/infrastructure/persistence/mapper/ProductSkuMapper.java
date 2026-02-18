package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.ProductSku;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品SKU Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface ProductSkuMapper extends BaseMapper<ProductSku> {
}
