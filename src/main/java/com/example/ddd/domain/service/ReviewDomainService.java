package com.example.ddd.domain.service;

import com.example.ddd.domain.model.entity.Order;
import com.example.ddd.domain.model.entity.OrderItem;
import com.example.ddd.domain.model.entity.Review;
import com.example.ddd.domain.model.entity.ReviewReply;
import com.example.ddd.domain.model.valueobject.OrderStatus;
import com.example.ddd.domain.repository.OrderItemRepository;
import com.example.ddd.domain.repository.OrderRepository;
import com.example.ddd.domain.repository.ReviewReplyRepository;
import com.example.ddd.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评价领域服务
 *
 * @author DDD Demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewDomainService extends DomainService {

    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository reviewReplyRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * 创建评价
     *
     * @param review 评价实体
     * @return 创建后的评价
     */
    @Transactional(rollbackFor = Exception.class)
    public Review createReview(Review review) {
        validate();

        // 验证订单明细是否存在
        OrderItem orderItem = orderItemRepository.findById(review.getOrderItemId());
        if (orderItem == null) {
            throw new IllegalArgumentException("订单明细不存在");
        }

        // 验证订单是否已完成
        Order order = orderRepository.findById(review.getOrderId());
        if (order == null || order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("只有已完成的订单才能评价");
        }

        // 检查是否已评价
        Review existing = reviewRepository.findByOrderItemId(review.getOrderItemId());
        if (existing != null) {
            throw new IllegalArgumentException("该商品已评价，不能重复评价");
        }

        // 设置默认值
        if (review.getIsAnonymous() == null) {
            review.setIsAnonymous(false);
        }
        if (review.getRating() == null) {
            review.setRating(5);
        }

        // 验证评分范围
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("评分必须在1-5之间");
        }

        // 设置关联信息
        review.setProductId(orderItem.getProductId());
        review.setSkuId(orderItem.getSkuId());
        review.setUserId(order.getUserId());
        review.setShopId(order.getShopId());

        return reviewRepository.save(review);
    }

    /**
     * 卖家回复评价
     *
     * @param reviewId  评价ID
     * @param content   回复内容
     * @param sellerId  卖家ID
     * @param sellerName 卖家名称
     * @return 回复实体
     */
    @Transactional(rollbackFor = Exception.class)
    public ReviewReply sellerReply(Long reviewId, String content, Long sellerId, String sellerName) {
        validate();

        Review review = reviewRepository.findById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("评价不存在");
        }

        ReviewReply reply = new ReviewReply();
        reply.setReviewId(reviewId);
        reply.setReplyType(1); // 卖家回复
        reply.setContent(content);
        reply.setReplierId(sellerId);
        reply.setReplierName(sellerName);

        return reviewReplyRepository.save(reply);
    }

    /**
     * 用户追评
     *
     * @param reviewId  评价ID
     * @param content   追评内容
     * @param userId    用户ID
     * @param userName  用户名称
     * @return 回复实体
     */
    @Transactional(rollbackFor = Exception.class)
    public ReviewReply userAppend(Long reviewId, String content, Long userId, String userName) {
        validate();

        Review review = reviewRepository.findById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("评价不存在");
        }

        // 验证是否是评价本人
        if (!review.getUserId().equals(userId)) {
            throw new IllegalArgumentException("只能追评自己的评价");
        }

        ReviewReply reply = new ReviewReply();
        reply.setReviewId(reviewId);
        reply.setReplyType(2); // 用户追评
        reply.setContent(content);
        reply.setReplierId(userId);
        reply.setReplierName(userName);

        return reviewReplyRepository.save(reply);
    }

    /**
     * 获取商品评价列表
     */
    public List<Review> getProductReviews(Long productId) {
        validate();
        return reviewRepository.findByProductId(productId);
    }

    /**
     * 计算商品好评率
     */
    public double calculatePositiveRate(Long productId) {
        validate();
        long total = reviewRepository.countByProductId(productId);
        if (total == 0) {
            return 100.0;
        }
        long positive = reviewRepository.countPositiveByProductId(productId);
        return (positive * 100.0) / total;
    }
}
