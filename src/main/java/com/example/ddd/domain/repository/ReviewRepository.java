package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.Review;

import java.util.List;

/**
 * 评价仓储接口
 *
 * @author DDD Demo
 */
public interface ReviewRepository extends BaseRepository<Review> {

    /**
     * 根据订单明细ID查询
     */
    Review findByOrderItemId(Long orderItemId);

    /**
     * 根据商品ID查询
     */
    List<Review> findByProductId(Long productId);

    /**
     * 根据店铺ID查询
     */
    List<Review> findByShopId(Long shopId);

    /**
     * 根据用户ID查询
     */
    List<Review> findByUserId(Long userId);

    /**
     * 统计商品评价数
     */
    long countByProductId(Long productId);

    /**
     * 统计商品好评数
     */
    long countPositiveByProductId(Long productId);
}
