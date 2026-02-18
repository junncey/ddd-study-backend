package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Order;
import com.example.ddd.domain.model.valueobject.OrderStatus;
import com.example.ddd.domain.repository.OrderRepository;
import com.example.ddd.infrastructure.persistence.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单仓储实现
 * 六边形架构的适配器（Adapter）
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderMapper orderMapper;

    @Override
    public Order findById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public Order save(Order entity) {
        if (entity.getId() == null) {
            orderMapper.insert(entity);
        } else {
            orderMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(Order entity) {
        return orderMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return orderMapper.deleteById(id);
    }

    @Override
    public IPage<Order> page(Page<Order> page) {
        return orderMapper.selectPage(page, null);
    }

    @Override
    public Order findByOrderNo(String orderNo) {
        return orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
        );
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreateTime)
        );
    }

    @Override
    public List<Order> findByShopId(Long shopId) {
        return orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getShopId, shopId)
                        .orderByDesc(Order::getCreateTime)
        );
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, status)
                        .orderByDesc(Order::getCreateTime)
        );
    }

    @Override
    public IPage<Order> pageByUserId(Page<Order> page, Long userId) {
        return orderMapper.selectPage(page,
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreateTime)
        );
    }

    @Override
    public IPage<Order> pageByShopId(Page<Order> page, Long shopId) {
        return orderMapper.selectPage(page,
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getShopId, shopId)
                        .orderByDesc(Order::getCreateTime)
        );
    }
}
