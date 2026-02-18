package com.example.ddd.interfaces.rest.controller;

import com.example.ddd.application.service.ReviewApplicationService;
import com.example.ddd.domain.model.entity.Review;
import com.example.ddd.domain.model.entity.ReviewReply;
import com.example.ddd.interfaces.rest.vo.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 评价控制器
 *
 * @author DDD Demo
 */
@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewApplicationService reviewApplicationService;

    /**
     * 创建评价（用户操作）
     */
    @PostMapping
    public Response<Review> create(@RequestBody Review review) {
        Review created = reviewApplicationService.createReview(review);
        return Response.success(created);
    }

    /**
     * 获取商品评价列表（公开）
     */
    @GetMapping("/product/{productId}")
    public Response<List<Review>> getProductReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewApplicationService.getProductReviews(productId);
        return Response.success(reviews);
    }

    /**
     * 获取商品评价统计（公开）
     */
    @GetMapping("/product/{productId}/stats")
    public Response<Map<String, Object>> getReviewStats(@PathVariable Long productId) {
        Map<String, Object> stats = reviewApplicationService.getReviewStats(productId);
        return Response.success(stats);
    }

    /**
     * 卖家回复评价（商家操作）
     */
    @PostMapping("/{reviewId}/reply")
    public Response<ReviewReply> sellerReply(
            @PathVariable Long reviewId,
            @RequestBody Map<String, String> body) {
        String content = body.get("content");
        ReviewReply reply = reviewApplicationService.sellerReply(reviewId, content);
        return Response.success(reply);
    }

    /**
     * 用户追评（用户操作）
     */
    @PostMapping("/{reviewId}/append")
    public Response<ReviewReply> userAppend(
            @PathVariable Long reviewId,
            @RequestBody Map<String, String> body) {
        String content = body.get("content");
        ReviewReply reply = reviewApplicationService.userAppend(reviewId, content);
        return Response.success(reply);
    }

    /**
     * 检查订单项是否已评价（用户操作）
     */
    @GetMapping("/check/{orderItemId}")
    public Response<Boolean> hasReviewed(@PathVariable Long orderItemId) {
        boolean reviewed = reviewApplicationService.hasReviewed(orderItemId);
        return Response.success(reviewed);
    }
}
