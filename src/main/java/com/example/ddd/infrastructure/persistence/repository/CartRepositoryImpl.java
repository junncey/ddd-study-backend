package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.Cart;
import com.example.ddd.domain.repository.CartRepository;
import com.example.ddd.infrastructure.persistence.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 购物车仓储实现
 * 六边形架构的适配器（Adapter）
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {

    private final CartMapper cartMapper;

    @Override
    public Cart findById(Long id) {
        return cartMapper.selectById(id);
    }

    @Override
    public Cart save(Cart entity) {
        if (entity.getId() == null) {
            cartMapper.insert(entity);
        } else {
            cartMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(Cart entity) {
        return cartMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return cartMapper.deleteById(id);
    }

    @Override
    public IPage<Cart> page(Page<Cart> page) {
        return cartMapper.selectPage(page, null);
    }

    @Override
    public Cart findByUserId(Long userId) {
        return cartMapper.selectOne(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
        );
    }

    @Override
    public Cart getOrCreateCart(Long userId) {
        Cart cart = findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart = save(cart);
        }
        return cart;
    }
}
