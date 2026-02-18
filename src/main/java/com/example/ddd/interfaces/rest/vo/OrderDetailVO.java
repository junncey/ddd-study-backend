package com.example.ddd.interfaces.rest.vo;

import com.example.ddd.domain.model.entity.Order;
import com.example.ddd.domain.model.entity.OrderItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情VO
 * 包含订单基本信息和订单项列表
 *
 * @author DDD Demo
 */
@Data
public class OrderDetailVO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 订单总金额
     */
    private Object totalAmount;

    /**
     * 实付金额
     */
    private Object payAmount;

    /**
     * 订单状态（值）
     */
    private Integer status;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 收货地址
     */
    private String receiverAddress;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 订单项列表
     */
    private List<OrderItem> items;

    /**
     * 从Order实体创建OrderDetailVO
     *
     * @param order 订单实体
     * @param items 订单项列表
     * @return OrderDetailVO
     */
    public static OrderDetailVO from(Order order, List<OrderItem> items) {
        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setShopId(order.getShopId());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setStatus(order.getStatusInt());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setRemark(order.getRemark());
        vo.setPayTime(order.getPayTime());
        vo.setShipTime(order.getShipTime());
        vo.setCompleteTime(order.getCompleteTime());
        vo.setCreateTime(order.getCreateTime());
        vo.setItems(items);
        return vo;
    }
}
