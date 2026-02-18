package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类 Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
