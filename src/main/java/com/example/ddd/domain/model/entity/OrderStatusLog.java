package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * 订单状态日志实体
 *
 * @author DDD Demo
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_status_log")
public class OrderStatusLog extends BaseEntity {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 原状态
     */
    private Integer oldStatus;

    /**
     * 新状态
     */
    private Integer newStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作人
     */
    private String operator;
}
