package com.example.ddd.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单仓储接口
 * 六边形架构的端口（Port）
 *
 * @author DDD Demo
 */
public interface OrderRepository extends BaseRepository<Order> {

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 订单对象
     */
    Order findByOrderNo(String orderNo);

    /**
     * 根据用户ID查询订单列表
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByUserId(Long userId);

    /**
     * 根据店铺ID查询订单列表
     *
     * @param shopId 店铺ID
     * @return 订单列表
     */
    List<Order> findByShopId(Long shopId);

    /**
     * 根据状态查询订单
     *
     * @param status 状态
     * @return 订单列表
     */
    List<Order> findByStatus(com.example.ddd.domain.model.valueobject.OrderStatus status);

    /**
     * 分页查询用户订单
     *
     * @param page   分页对象
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<Order> pageByUserId(Page<Order> page, Long userId);

    /**
     * 分页查询店铺订单
     *
     * @param page   分页对象
     * @param shopId 店铺ID
     * @return 分页结果
     */
    IPage<Order> pageByShopId(Page<Order> page, Long shopId);

    /**
     * 查询超时未支付的订单
     *
     * @param timeoutThreshold 超时时间阈值（创建时间早于此时间的待支付订单）
     * @return 超时订单列表
     */
    List<Order> findTimeoutPendingOrders(LocalDateTime timeoutThreshold);
}
