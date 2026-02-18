package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.ProductImage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品图片 Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface ProductImageMapper extends BaseMapper<ProductImage> {
}
