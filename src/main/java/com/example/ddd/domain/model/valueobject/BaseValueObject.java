package com.example.ddd.domain.model.valueobject;

import java.util.Objects;

/**
 * 值对象抽象基类
 * 提供基于值的 equals/hashCode 实现
 *
 * @author DDD Demo
 */
public abstract class BaseValueObject implements ValueObject {

    /**
     * 判断值对象是否相同
     * 默认实现：类型相同且 equals 返回 true
     *
     * @param other 另一个值对象
     * @return 如果类型和属性值都相同则返回 true
     */
    @Override
    public boolean sameValueAs(ValueObject other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return this.equals(other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return true; // 子类需要重写此方法
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
