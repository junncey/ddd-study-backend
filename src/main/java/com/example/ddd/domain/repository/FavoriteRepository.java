package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.Favorite;

import java.util.List;

/**
 * 收藏仓储接口
 *
 * @author DDD Demo
 */
public interface FavoriteRepository extends BaseRepository<Favorite> {

    /**
     * 根据用户ID和商品ID查询
     */
    Favorite findByUserIdAndProductId(Long userId, Long productId);

    /**
     * 根据用户ID查询收藏列表
     */
    List<Favorite> findByUserId(Long userId);

    /**
     * 根据店铺ID查询收藏列表
     */
    List<Favorite> findByShopId(Long shopId);

    /**
     * 统计用户收藏数
     */
    long countByUserId(Long userId);

    /**
     * 检查是否已收藏
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    /**
     * 删除收藏
     */
    int deleteByUserIdAndProductId(Long userId, Long productId);
}
