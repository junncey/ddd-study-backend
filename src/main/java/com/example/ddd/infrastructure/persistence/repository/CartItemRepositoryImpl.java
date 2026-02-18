package com.example.ddd.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ddd.domain.model.entity.CartItem;
import com.example.ddd.domain.repository.CartItemRepository;
import com.example.ddd.infrastructure.persistence.mapper.CartItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 购物车明细仓储实现
 * 六边形架构的适配器（Adapter）
 *
 * @author DDD Demo
 */
@Repository
@RequiredArgsConstructor
public class CartItemRepositoryImpl implements CartItemRepository {

    private final CartItemMapper cartItemMapper;

    @Override
    public CartItem findById(Long id) {
        return cartItemMapper.selectById(id);
    }

    @Override
    public CartItem save(CartItem entity) {
        if (entity.getId() == null) {
            cartItemMapper.insert(entity);
        } else {
            cartItemMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public int update(CartItem entity) {
        return cartItemMapper.updateById(entity);
    }

    @Override
    public int delete(Long id) {
        return cartItemMapper.deleteById(id);
    }

    @Override
    public IPage<CartItem> page(Page<CartItem> page) {
        return cartItemMapper.selectPage(page, null);
    }

    @Override
    public List<CartItem> findByCartId(Long cartId) {
        return cartItemMapper.selectList(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getCartId, cartId)
        );
    }

    @Override
    public CartItem findByCartIdAndSkuId(Long cartId, Long skuId) {
        return cartItemMapper.selectOne(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getCartId, cartId)
                        .eq(CartItem::getSkuId, skuId)
        );
    }

    @Override
    public int deleteByCartId(Long cartId) {
        return cartItemMapper.delete(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getCartId, cartId)
        );
    }

    @Override
    public int physicalDeleteByCartIdAndSkuId(Long cartId, Long skuId) {
        // 使用原生SQL进行物理删除，绕过MyBatis-Plus的逻辑删除
        return cartItemMapper.physicalDeleteByCartIdAndSkuId(cartId, skuId);
    }

    @Override
    public int physicalDeleteById(Long id) {
        // 使用原生SQL进行物理删除，绕过MyBatis-Plus的逻辑删除
        return cartItemMapper.physicalDeleteById(id);
    }

    @Override
    public int physicalDeleteByCartId(Long cartId) {
        // 使用原生SQL进行物理删除，绕过MyBatis-Plus的逻辑删除
        return cartItemMapper.physicalDeleteByCartId(cartId);
    }
}
