package com.example.ddd.interfaces.rest.controller;

import com.example.ddd.application.service.FavoriteApplicationService;
import com.example.ddd.domain.model.entity.Product;
import com.example.ddd.infrastructure.security.SecurityUtil;
import com.example.ddd.interfaces.rest.vo.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏控制器
 *
 * @author DDD Demo
 */
@Slf4j
@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteApplicationService favoriteApplicationService;

    /**
     * 切换收藏状态
     */
    @PostMapping("/toggle/{productId}")
    public Response<Boolean> toggleFavorite(@PathVariable Long productId) {
        Long userId = SecurityUtil.getCurrentUserId();
        boolean isFavorited = favoriteApplicationService.toggleFavorite(userId, productId);
        return Response.success(isFavorited);
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check/{productId}")
    public Response<Boolean> checkFavorite(@PathVariable Long productId) {
        Long userId = SecurityUtil.getCurrentUserId();
        boolean isFavorited = favoriteApplicationService.isFavorited(userId, productId);
        return Response.success(isFavorited);
    }

    /**
     * 获取用户收藏列表
     */
    @GetMapping("/my")
    public Response<List<Product>> getMyFavorites() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Product> products = favoriteApplicationService.getUserFavoriteProducts(userId);
        return Response.success(products);
    }

    /**
     * 获取用户收藏数量
     */
    @GetMapping("/my/count")
    public Response<Long> getMyFavoriteCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        long count = favoriteApplicationService.getUserFavoriteCount(userId);
        return Response.success(count);
    }
}
