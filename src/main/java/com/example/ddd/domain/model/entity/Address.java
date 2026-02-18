package com.example.ddd.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * 收货地址实体
 *
 * @author DDD Demo
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_address")
public class Address extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区/县
     */
    private String district;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 是否默认地址 0-否 1-是
     */
    private Integer isDefault;

    /**
     * 判断是否为默认地址
     *
     * @return true 如果为默认地址
     */
    public boolean isDefaultAddress() {
        return Integer.valueOf(1).equals(isDefault);
    }

    /**
     * 设置为默认地址
     */
    public void setAsDefault() {
        this.isDefault = 1;
    }

    /**
     * 取消默认地址
     */
    public void unsetDefault() {
        this.isDefault = 0;
    }

    /**
     * 获取完整地址
     *
     * @return 完整地址字符串
     */
    public String getFullAddress() {
        return province + city + district + detailAddress;
    }
}
