package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.OrderStatusLog;
import com.example.ddd.domain.repository.OrderStatusLogRepository;
import com.example.ddd.infrastructure.persistence.mapper.OrderStatusLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单状态日志仓储实现
 * 六边形架构的适配器（Adapter）
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class OrderStatusLogRepositoryImpl implements OrderStatusLogRepository {

    private final OrderStatusLogMapper orderStatusLogMapper;

    @Override
    public OrderStatusLog findById(Long id) {
        return orderStatusLogMapper.selectById(id);
    }

    @Override
    public OrderStatusLog save(OrderStatusLog entity) {
        if (entity.getId() == null) {
            orderStatusLogMapper.insert(entity);
        } else {
            orderStatusLogMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(OrderStatusLog entity) {
        return orderStatusLogMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return orderStatusLogMapper.deleteById(id);
    }

    @Override
    public IPage<OrderStatusLog> page(Page<OrderStatusLog> page) {
        return orderStatusLogMapper.selectPage(page, null);
    }

    @Override
    public List<OrderStatusLog> findByOrderId(Long orderId) {
        return orderStatusLogMapper.selectList(
                new LambdaQueryWrapper<OrderStatusLog>()
                        .eq(OrderStatusLog::getOrderId, orderId)
                        .orderByDesc(OrderStatusLog::getCreateTime)
        );
    }
}
