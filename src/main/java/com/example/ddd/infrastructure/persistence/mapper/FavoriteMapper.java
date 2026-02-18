package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏 Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
