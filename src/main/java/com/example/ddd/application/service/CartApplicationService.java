package com.example.ddd.application.service;

import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.CartItem;
import com.example.ddd.domain.service.CartDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
