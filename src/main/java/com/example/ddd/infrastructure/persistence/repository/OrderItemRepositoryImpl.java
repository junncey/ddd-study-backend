package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.OrderItem;
import com.example.ddd.domain.repository.OrderItemRepository;
import com.example.ddd.infrastructure.persistence.mapper.OrderItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单明细仓储实现
 * 六边形架构的适配器（Adapter）
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderItem findById(Long id) {
        return orderItemMapper.selectById(id);
    }

    @Override
    public OrderItem save(OrderItem entity) {
        if (entity.getId() == null) {
            orderItemMapper.insert(entity);
        } else {
            orderItemMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(OrderItem entity) {
        return orderItemMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return orderItemMapper.deleteById(id);
    }

    @Override
    public IPage<OrderItem> page(Page<OrderItem> page) {
        return orderItemMapper.selectPage(page, null);
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        return orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId)
        );
    }

    @Override
    public int deleteByOrderId(Long orderId) {
        return orderItemMapper.delete(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId)
        );
    }
}
