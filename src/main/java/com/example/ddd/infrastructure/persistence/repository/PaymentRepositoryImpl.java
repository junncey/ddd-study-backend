package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Payment;
import com.example.ddd.domain.repository.PaymentRepository;
import com.example.ddd.infrastructure.persistence.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 支付记录仓储实现
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentMapper paymentMapper;

    @Override
    public Payment findById(Long id) {
        return paymentMapper.selectById(id);
    }

    @Override
    public Payment save(Payment entity) {
        if (entity.getId() == null) {
            paymentMapper.insert(entity);
        } else {
            paymentMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(Payment entity) {
        return paymentMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return paymentMapper.deleteById(id);
    }

    @Override
    public IPage<Payment> page(Page<Payment> page) {
        return paymentMapper.selectPage(page, null);
    }

    @Override
    public Payment findByPaymentNo(String paymentNo) {
        return paymentMapper.selectOne(
                new LambdaQueryWrapper<Payment>()
                        .eq(Payment::getPaymentNo, paymentNo)
        );
    }

    @Override
    public Payment findByOrderId(Long orderId) {
        return paymentMapper.selectOne(
                new LambdaQueryWrapper<Payment>()
                        .eq(Payment::getOrderId, orderId)
        );
    }

    @Override
    public Payment findByOrderNo(String orderNo) {
        return paymentMapper.selectOne(
                new LambdaQueryWrapper<Payment>()
                        .eq(Payment::getOrderNo, orderNo)
        );
    }
}
