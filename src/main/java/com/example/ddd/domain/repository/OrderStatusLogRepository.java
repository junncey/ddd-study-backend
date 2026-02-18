package com.example.ddd.domain.repository;

import com.example.ddd.domain.model.entity.OrderStatusLog;

import java.util.List;

/**
 * 订单状态日志仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface OrderStatusLogRepository extends BaseRepository<OrderStatusLog> {

    /**
     * 根据订单ID查询状态日志列表
     *
     * @param orderId 订单ID
     * @return 日志列表
     */
    List<OrderStatusLog> findByOrderId(Long orderId);
}
