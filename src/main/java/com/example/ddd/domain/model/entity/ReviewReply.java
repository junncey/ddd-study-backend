package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * 评价回复实体
 *
 * @author DDD Demo
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_review_reply")
public class ReviewReply extends BaseEntity {

    /**
     * 评价ID
     */
    private Long reviewId;

    /**
     * 回复类型：1-卖家回复 2-用户追评
     */
    private Integer replyType;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 回复人ID
     */
    private Long replierId;

    /**
     * 回复人名称
     */
    private String replierName;

    /**
     * 是否为卖家回复
     */
    public boolean isSellerReply() {
        return replyType != null && replyType == 1;
    }

    /**
     * 是否为用户追评
     */
    public boolean isUserAppend() {
        return replyType != null && replyType == 2;
    }
}
