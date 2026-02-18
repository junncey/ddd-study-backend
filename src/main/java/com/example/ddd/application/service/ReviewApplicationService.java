package com.example.ddd.application.service;

import com.example.ddd.application.ApplicationService;
import com.example.ddd.domain.model.entity.Review;
import com.example.ddd.domain.model.entity.ReviewReply;
import com.example.ddd.domain.repository.ReviewReplyRepository;
import com.example.ddd.domain.repository.ReviewRepository;
import com.example.ddd.domain.service.ReviewDomainService;
import com.example.ddd.infrastructure.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评价应用服务
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewApplicationService extends ApplicationService {

    private final ReviewDomainService reviewDomainService;
    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository reviewReplyRepository;

    /**
     * 创建评价
     */
    public Review createReview(Review review) {
        beforeExecute();
        try {
            return reviewDomainService.createReview(review);
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取商品评价列表
     */
    public List<Review> getProductReviews(Long productId) {
        beforeExecute();
        try {
            List<Review> reviews = reviewDomainService.getProductReviews(productId);
            // 填充回复信息
            for (Review review : reviews) {
                List<ReviewReply> replies = reviewReplyRepository.findByReviewId(review.getId());
                review.setReplies(replies);
            }
            return reviews;
        } finally {
            afterExecute();
        }
    }

    /**
     * 获取商品评价统计
     */
    public Map<String, Object> getReviewStats(Long productId) {
        beforeExecute();
        try {
            Map<String, Object> stats = new HashMap<>();

            long total = reviewRepository.countByProductId(productId);
            long positive = reviewRepository.countPositiveByProductId(productId);
            double positiveRate = reviewDomainService.calculatePositiveRate(productId);

            stats.put("total", total);
            stats.put("positive", positive);
            stats.put("positiveRate", positiveRate);

            return stats;
        } finally {
            afterExecute();
        }
    }

    /**
     * 卖家回复评价
     */
    public ReviewReply sellerReply(Long reviewId, String content) {
        beforeExecute();
        try {
            Long sellerId = SecurityUtil.getCurrentUserId();
            String sellerName = SecurityUtil.getCurrentUsername();
            return reviewDomainService.sellerReply(reviewId, content, sellerId, sellerName);
        } finally {
            afterExecute();
        }
    }

    /**
     * 用户追评
     */
    public ReviewReply userAppend(Long reviewId, String content) {
        beforeExecute();
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            String userName = SecurityUtil.getCurrentUsername();
            return reviewDomainService.userAppend(reviewId, content, userId, userName);
        } finally {
            afterExecute();
        }
    }

    /**
     * 检查订单项是否已评价
     */
    public boolean hasReviewed(Long orderItemId) {
        beforeExecute();
        try {
            Review review = reviewRepository.findByOrderItemId(orderItemId);
            return review != null;
        } finally {
            afterExecute();
        }
    }
}
