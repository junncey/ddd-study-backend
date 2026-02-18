package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.OrderItem;

import java.util.List;

/**
 * 订单明细仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface OrderItemRepository extends BaseRepository<OrderItem> {

    /**
     * 根据订单ID查询明细列表
     *
     * @param orderId 订单ID
     * @return 明细列表
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * 根据订单ID删除所有明细
     *
     * @param orderId 订单ID
     * @return 影响行数
     */
    int deleteByOrderId(Long orderId);
}
