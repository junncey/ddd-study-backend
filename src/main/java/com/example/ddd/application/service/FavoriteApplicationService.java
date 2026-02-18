package com.example.ddd.application.service;

import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.Favorite;
import com.example.ddd.domain.model.entity.Product;
import com.example.ddd.domain.model.entity.ProductSku;
import com.example.ddd.domain.service.FavoriteDomainService;
import com.example.ddd.domain.repository.ProductRepository;
import com.example.ddd.domain.repository.ProductSkuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private final ProductSkuRepository productSkuRepository;

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
                    fillProductPriceAndStock(product);
                    products.add(product);
                }
            }
            return products;
        } finally {
            afterExecute();
        }
    }

    /**
     * 填充商品价格和库存信息
     */
    private void fillProductPriceAndStock(Product product) {
        List<ProductSku> skus = productSkuRepository.findByProductId(product.getId());
        if (skus != null && !skus.isEmpty()) {
            // 计算最低价格
            BigDecimal minPrice = skus.stream()
                    .filter(sku -> sku.getPrice() != null)
                    .map(sku -> sku.getPrice().getValue())
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            product.setMinPrice(minPrice);

            // 计算总库存
            int totalStock = skus.stream()
                    .filter(sku -> sku.getStock() != null)
                    .mapToInt(sku -> sku.getStock().getValue())
                    .sum();
            product.setTotalStock(totalStock);
        } else {
            product.setMinPrice(BigDecimal.ZERO);
            product.setTotalStock(0);
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
