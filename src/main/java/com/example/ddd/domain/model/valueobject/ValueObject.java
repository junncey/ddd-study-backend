package com.example.ddd.domain.model.valueobject;

import java.io.Serializable;

/**
 * 值对象接口
 * 所有值对象都需要实现此接口
 * 值对象的特点：不可变性、无唯一标识、通过属性值判断相等性
 *
 * @author DDD Demo
 */
public interface ValueObject extends Serializable {

    /**
     * 判断值对象是否相同
     * 值对象的相等性基于其属性值
     *
     * @param other 另一个值对象
     * @return 如果所有属性值都相同则返回true
     */
    boolean sameValueAs(ValueObject other);
}
