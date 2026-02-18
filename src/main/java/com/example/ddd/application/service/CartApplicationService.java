package com.example.ddd.application.service;

import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.CartItem;
import com.example.ddd.domain.model.entity.Product;
import com.example.ddd.domain.model.entity.ProductSku;
import com.example.ddd.domain.repository.ProductRepository;
import com.example.ddd.domain.repository.ProductSkuRepository;
import com.example.ddd.domain.service.CartDomainService;
import com.example.ddd.interfaces.rest.vo.CartItemVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车应用服务
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartApplicationService extends ApplicationService {

    private final CartDomainService cartDomainService;
    private final ProductSkuRepository productSkuRepository;
    private final ProductRepository productRepository;

    /**
     * 添加商品到购物车
     */
    public CartItem addItem(Long userId, Long skuId, Integer quantity) {
        beforeExecute();
        try {
            return cartDomainService.addItem(userId, skuId, quantity);
        } finally {
            afterExecute();
        }
    }

    /**
     * 更新购物车商品数量
     */
    public CartItem updateQuantity(Long cartItemId, Integer quantity) {
        beforeExecute();
        try {
            return cartDomainService.updateQuantity(cartItemId, quantity);
        } finally {
            afterExecute();
        }
    }

    /**
     * 删除购物车商品
     */
    public boolean removeItem(Long cartItemId) {
        beforeExecute();
        try {
            return cartDomainService.removeItem(cartItemId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 清空购物车
     */
    public void clearCart(Long userId) {
        beforeExecute();
        try {
            cartDomainService.clearCart(userId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取购物车列表
     */
    public List<CartItem> getCartItems(Long userId) {
        beforeExecute();
        try {
            return cartDomainService.getCartItems(userId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取购物车列表（包含商品信息）
     */
    public List<CartItemVO> getCartItemsWithProductInfo(Long userId) {
        beforeExecute();
        try {
            List<CartItem> cartItems = cartDomainService.getCartItems(userId);
            List<CartItemVO> result = new ArrayList<>();

            for (CartItem item : cartItems) {
                // 获取SKU信息
                ProductSku sku = productSkuRepository.findById(item.getSkuId());

                // 获取商品信息
                Product product = null;
                if (sku != null) {
                    product = productRepository.findById(sku.getProductId());
                }

                // 构建VO
                CartItemVO vo = CartItemVO.from(item, sku, product);
                result.add(vo);
            }

            return result;
        } finally {
            afterExecute();
        }
    }
}
