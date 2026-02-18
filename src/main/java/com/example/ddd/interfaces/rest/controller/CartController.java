package com.example.ddd.interfaces.rest.controller;

import com.example.ddd.application.service.CartApplicationService;
import com.example.ddd.domain.model.entity.CartItem;
import com.example.ddd.interfaces.rest.vo.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 *
 * @author DDD Demo
 */
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartApplicationService cartApplicationService;

    /**
     * 添加商品到购物车
     */
    @PostMapping("/items")
    public Response<CartItem> addItem(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long skuId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        CartItem item = cartApplicationService.addItem(userId, skuId, quantity);
        return Response.success(item);
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/items/{itemId}")
    public Response<CartItem> updateQuantity(
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        CartItem item = cartApplicationService.updateQuantity(itemId, quantity);
        return Response.success(item);
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/items/{itemId}")
    public Response<Void> removeItem(@PathVariable Long itemId) {
        cartApplicationService.removeItem(itemId);
        return Response.success();
    }

    /**
     * 清空购物车
     */
    @DeleteMapping
    public Response<Void> clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartApplicationService.clearCart(userId);
        return Response.success();
    }

    /**
     * 获取购物车列表
     */
    @GetMapping("/my")
    public Response<List<CartItem>> getMyCart(@RequestHeader("X-User-Id") Long userId) {
        List<CartItem> items = cartApplicationService.getCartItems(userId);
        return Response.success(items);
    }
}
