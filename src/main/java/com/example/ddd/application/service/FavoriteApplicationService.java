package com.example.ddd.application.service;

import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.Favorite;
import com.example.ddd.domain.model.entity.Product;
import com.example.ddd.domain.service.FavoriteDomainService;
import com.example.ddd.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏应用服务
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteApplicationService extends ApplicationService {

    private final FavoriteDomainService favoriteDomainService;
    private final ProductRepository productRepository;

    /**
     * 切换收藏状态
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return true-已收藏，false-取消收藏
     */
    public boolean toggleFavorite(Long userId, Long productId) {
        beforeExecute();
        try {
            return favoriteDomainService.toggleFavorite(userId, productId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 检查是否已收藏
     */
    public boolean isFavorited(Long userId, Long productId) {
        beforeExecute();
        try {
            return favoriteDomainService.isFavorited(userId, productId);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取用户收藏的商品列表
     */
    public List<Product> getUserFavoriteProducts(Long userId) {
        beforeExecute();
        try {
            List<Favorite> favorites = favoriteDomainService.getUserFavorites(userId);
            List<Product> products = new ArrayList<>();
            for (Favorite favorite : favorites) {
                Product product = productRepository.findById(favorite.getProductId());
                if (product != null) {
                    products.add(product);
                }
            }
            return products;
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取用户收藏数量
     */
    public long getUserFavoriteCount(Long userId) {
        beforeExecute();
        try {
            return favoriteDomainService.getUserFavoriteCount(userId);
        } finally {
            afterExecute();
        }
    }
}
