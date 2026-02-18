package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 评价实体
 *
 * @author DDD Demo
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_review")
public class Review extends BaseEntity {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单明细ID
     */
    private Long orderItemId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 评分（1-5星）
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片（JSON数组）
     */
    private String images;

    /**
     * 是否匿名
     */
    private Boolean isAnonymous;

    /**
     * 回复列表（非数据库字段）
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private List<ReviewReply> replies;

    /**
     * 获取是否匿名
     */
    public Boolean getIsAnonymous() {
        return isAnonymous != null && isAnonymous;
    }

    /**
     * 判断是否为好评（4星及以上）
     */
    public boolean isPositive() {
        return rating != null && rating >= 4;
    }
}
