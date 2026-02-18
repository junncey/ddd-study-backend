package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ddd.domain.model.valueobject.ShopStatus;
import com.example.ddd.infrastructure.persistence.handler.ShopStatusTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * 店铺实体
 *
 * @author DDD Demo
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_shop")
public class Shop extends BaseEntity {

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺Logo
     */
    private String shopLogo;

    /**
     * 店铺描述
     */
    private String description;

    /**
     * 店主ID
     */
    private Long ownerId;

    /**
     * 店铺状态
     */
    @TableField(typeHandler = ShopStatusTypeHandler.class)
    private ShopStatus status;

    /**
     * 设置状态值
     *
     * @param statusInt 状态值
     */
    public void setStatusInt(Integer statusInt) {
        this.status = statusInt != null ? ShopStatus.fromValue(statusInt) : null;
    }

    /**
     * 获取状态值
     *
     * @return 状态值
     */
    public Integer getStatusInt() {
        return status != null ? status.getValue() : null;
    }

    /**
     * 判断是否可以经营
     *
     * @return true 如果可以经营
     */
    public boolean canOperate() {
        return status != null && status.canOperate();
    }

    /**
     * 判断是否为待审核状态
     *
     * @return true 如果为待审核状态
     */
    public boolean isPending() {
        return status == ShopStatus.PENDING;
    }

    /**
     * 判断是否为已审核状态
     *
     * @return true 如果为已审核状态
     */
    public boolean isApproved() {
        return status == ShopStatus.APPROVED;
    }
}
