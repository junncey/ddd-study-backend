package com.example.ddd.domain.model.valueobject;

/**
 * 业务类型枚举
 * 定义文件关联的业务类型
 *
 * @author DDD Demo
 */
public enum BizType {

    /**
     * 商品图片
     */
    PRODUCT_IMAGE("product_image", "商品图片"),

    /**
     * 商品详情图
     */
    PRODUCT_DETAIL("product_detail", "商品详情图"),

    /**
     * 用户头像
     */
    AVATAR("avatar", "用户头像"),

    /**
     * 店铺Logo
     */
    SHOP_LOGO("shop_logo", "店铺Logo"),

    /**
     * 分类图标
     */
    CATEGORY_ICON("category_icon", "分类图标"),

    /**
     * 支付凭证
     */
    PAYMENT_VOUCHER("payment_voucher", "支付凭证");

    private final String code;
    private final String description;

    BizType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 业务类型代码
     * @return 业务类型枚举
     */
    public static BizType fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        for (BizType type : BizType.values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的业务类型: " + code);
    }

    /**
     * 根据名称获取枚举（不区分大小写）
     *
     * @param name 业务类型名称
     * @return 业务类型枚举
     */
    public static BizType fromName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return BizType.valueOf(name.toUpperCase());
    }
}
