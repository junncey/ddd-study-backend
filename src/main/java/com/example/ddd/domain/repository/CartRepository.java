package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.Cart;

/**
 * 购物车仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface CartRepository extends BaseRepository<Cart> {

    /**
     * 根据用户ID查询购物车
     *
     * @param userId 用户ID
     * @return 购物车对象
     */
    Cart findByUserId(Long userId);

    /**
     * 获取或创建用户的购物车
     *
     * @param userId 用户ID
     * @return 购物车对象
     */
    Cart getOrCreateCart(Long userId);
}
