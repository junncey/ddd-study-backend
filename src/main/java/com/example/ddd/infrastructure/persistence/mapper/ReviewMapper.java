package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.Review;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评价 Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
}
