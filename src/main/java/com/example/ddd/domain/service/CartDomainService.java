package com.example.ddd.domain.service;

import com.example.ddd.domain.model.entity.Cart;
import com.example.ddd.domain.model.entity.CartItem;
import com.example.ddd.domain.model.entity.ProductSku;
import com.example.ddd.domain.repository.CartItemRepository;
import com.example.ddd.domain.repository.CartRepository;
import com.example.ddd.domain.repository.ProductSkuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 购物车领域服务
 * 包含购物车相关的核心业务逻辑
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartDomainService extends DomainService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductSkuRepository productSkuRepository;

    /**
     * 添加商品到购物车
     * 如果已存在则增加数量
     *
     * @param userId   用户ID
     * @param skuId    SKU ID
     * @param quantity  数量
     * @return 购物车明细
     */
    @Transactional(rollbackFor = Exception.class)
    public CartItem addItem(Long userId, Long skuId, Integer quantity) {
        validate();

        // 获取或创建购物车
        Cart cart = cartRepository.getOrCreateCart(userId);

        // 检查SKU是否存在
        ProductSku sku = productSkuRepository.findById(skuId);
        if (sku == null) {
            throw new IllegalArgumentException("商品SKU不存在");
        }

        // 检查库存
        if (!sku.hasEnoughStock(quantity)) {
            throw new IllegalArgumentException("库存不足");
        }

        // 检查是否已存在
        CartItem existingItem = cartItemRepository.findByCartIdAndSkuId(cart.getId(), skuId);
        if (existingItem != null) {
            // 增加数量
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartItemRepository.save(existingItem);
        }

        // 创建新明细
        CartItem cartItem = new CartItem();
        cartItem.setCartId(cart.getId());
        cartItem.setSkuId(skuId);
        cartItem.setQuantity(quantity);
        cartItem.setPriceSnapshot(sku.getPrice());

        return cartItemRepository.save(cartItem);
    }

    /**
     * 更新购物车明细数量
     *
     * @param cartItemId 购物车明细ID
     * @param quantity    新数量
     * @return 更新后的明细
     */
    @Transactional(rollbackFor = Exception.class)
    public CartItem updateQuantity(Long cartItemId, Integer quantity) {
        validate();

        CartItem cartItem = cartItemRepository.findById(cartItemId);
        if (cartItem == null) {
            throw new IllegalArgumentException("购物车明细不存在");
        }

        // 检查库存
        ProductSku sku = productSkuRepository.findById(cartItem.getSkuId());
        if (!sku.hasEnoughStock(quantity)) {
            throw new IllegalArgumentException("库存不足");
        }

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    /**
     * 删除购物车明细
     * 使用物理删除，不使用逻辑删除
     *
     * @param cartItemId 购物车明细ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removeItem(Long cartItemId) {
        validate();

        // 查找当前明细
        CartItem cartItem = cartItemRepository.findById(cartItemId);
        if (cartItem == null) {
            return false;
        }

        // 使用物理删除，直接从数据库删除记录
        return cartItemRepository.physicalDeleteById(cartItemId) > 0;
    }

    /**
     * 清空购物车
     * 使用物理删除，避免逻辑删除导致的唯一约束冲突
     *
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void clearCart(Long userId) {
        validate();

        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            // 使用物理删除，避免唯一约束冲突 (cart_id, sku_id, deleted)
            cartItemRepository.physicalDeleteByCartId(cart.getId());
        }
    }

    /**
     * 获取购物车明细列表
     *
     * @param userId 用户ID
     * @return 明细列表
     */
    public List<CartItem> getCartItems(Long userId) {
        validate();

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            return List.of();
        }

        return cartItemRepository.findByCartId(cart.getId());
    }
}
