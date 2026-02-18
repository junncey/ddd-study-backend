package com.example.ddd.domain.model.valueobject;

import lombok.Getter;

import java.util.Objects;

/**
 * 数量值对象
 * 确保数量的有效性
 *
 * @author DDD Demo
 */
@Getter
public class Quantity extends BaseValueObject {

    /**
     * 数量值
     */
    private final Integer value;

    private Quantity(Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("数量不能为空");
        }
        if (value < 0) {
            throw new IllegalArgumentException("数量不能为负数");
        }
        this.value = value;
    }

    /**
     * 创建数量值对象
     *
     * @param value 数量值
     * @return 数量值对象
     */
    public static Quantity of(Integer value) {
        return new Quantity(value);
    }

    /**
     * 创建零数量
     *
     * @return 零数量
     */
    public static Quantity zero() {
        return new Quantity(0);
    }

    /**
     * 数量加法
     *
     * @param other 另一个数量
     * @return 新数量
     */
    public Quantity add(Quantity other) {
        return new Quantity(this.value + other.value);
    }

    /**
     * 数量减法
     *
     * @param other 另一个数量
     * @return 新数量
     */
    public Quantity subtract(Quantity other) {
        return new Quantity(this.value - other.value);
    }

    /**
     * 数量乘法
     *
     * @param multiplier 乘数
     * @return 新数量
     */
    public Quantity multiply(int multiplier) {
        return new Quantity(this.value * multiplier);
    }

    /**
     * 比较数量大小
     *
     * @param other 另一个数量
     * @return 大于返回1，等于返回0，小于返回-1
     */
    public int compareTo(Quantity other) {
        return Integer.compare(this.value, other.value);
    }

    /**
     * 判断是否大于
     *
     * @param other 另一个数量
     * @return true 如果大于
     */
    public boolean greaterThan(Quantity other) {
        return this.value > other.value;
    }

    /**
     * 判断是否小于
     *
     * @param other 另一个数量
     * @return true 如果小于
     */
    public boolean lessThan(Quantity other) {
        return this.value < other.value;
    }

    /**
     * 判断是否等于
     *
     * @param other 另一个数量
     * @return true 如果等于
     */
    public boolean equalsQuantity(Quantity other) {
        return this.value.equals(other.value);
    }

    /**
     * 判断是否为零
     *
     * @return true 如果为零
     */
    public boolean isZero() {
        return this.value == 0;
    }

    /**
     * 判断是否为正数
     *
     * @return true 如果为正数
     */
    public boolean isPositive() {
        return this.value > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Quantity quantity = (Quantity) o;
        return Objects.equals(value, quantity.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
