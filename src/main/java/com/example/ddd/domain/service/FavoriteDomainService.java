package com.example.ddd.domain.service;

import com.example.ddd.domain.model.entity.Favorite;
import com.example.ddd.domain.model.entity.Product;
import com.example.ddd.domain.repository.FavoriteRepository;
import com.example.ddd.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收藏领域服务
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteDomainService extends DomainService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;

    /**
     * 添加收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 收藏实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Favorite addFavorite(Long userId, Long productId) {
        validate();

        // 检查商品是否存在
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }

        // 检查是否已收藏
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalArgumentException("您已收藏过该商品");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setShopId(product.getShopId());

        return favoriteRepository.save(favorite);
    }

    /**
     * 取消收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(Long userId, Long productId) {
        validate();

        int deleted = favoriteRepository.deleteByUserIdAndProductId(userId, productId);
        if (deleted == 0) {
            throw new IllegalArgumentException("未找到收藏记录");
        }
    }

    /**
     * 检查是否已收藏
     */
    public boolean isFavorited(Long userId, Long productId) {
        validate();
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    /**
     * 获取用户收藏列表
     */
    public List<Favorite> getUserFavorites(Long userId) {
        validate();
        return favoriteRepository.findByUserId(userId);
    }

    /**
     * 获取用户收藏数量
     */
    public long getUserFavoriteCount(Long userId) {
        validate();
        return favoriteRepository.countByUserId(userId);
    }

    /**
     * 切换收藏状态
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return true-已收藏，false-未收藏
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleFavorite(Long userId, Long productId) {
        validate();

        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            favoriteRepository.deleteByUserIdAndProductId(userId, productId);
            return false;
        } else {
            addFavorite(userId, productId);
            return true;
        }
    }
}
