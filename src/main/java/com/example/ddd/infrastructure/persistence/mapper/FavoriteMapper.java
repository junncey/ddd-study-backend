package com.example.ddd.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ddd.domain.model.entity.Favorite;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 收藏 Mapper
 *
 * @author DDD Demo
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    /**
     * 物理删除收藏记录（包括逻辑删除的）
     * 用于解决唯一约束冲突问题
     */
    @Delete("DELETE FROM t_favorite WHERE user_id = #{userId} AND product_id = #{productId}")
    int physicalDeleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}
