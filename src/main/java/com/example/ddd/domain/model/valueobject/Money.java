package com.example.ddd.domain.model.valueobject;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 金额值对象
 * 确保金额计算的精确性，防止浮点数误差
 *
 * @author DDD Demo
 */
@Getter
public class Money extends BaseValueObject {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * 金额值（保留两位小数）
     */
    private final BigDecimal value;

    private Money(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("金额不能为空");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("金额不能为负数");
        }
        this.value = value.setScale(SCALE, ROUNDING_MODE);
    }

    /**
     * 创建金额值对象
     *
     * @param value 金额值
     * @return 金额值对象
     */
    public static Money of(BigDecimal value) {
        return new Money(value);
    }

    /**
     * 创建金额值对象（从字符串）
     *
     * @param value 金额字符串
     * @return 金额值对象
     */
    public static Money of(String value) {
        return new Money(new BigDecimal(value));
    }

    /**
     * 创建金额值对象（从整数）
     *
     * @param value 金额值（分）
     * @return 金额值对象
     */
    public static Money ofCent(Long value) {
        return new Money(BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE));
    }

    /**
     * 创建零金额
     *
     * @return 零金额
     */
    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    /**
     * 金额加法
     *
     * @param other 另一个金额
     * @return 新金额
     */
    public Money add(Money other) {
        return new Money(this.value.add(other.value));
    }

    /**
     * 金额减法
     *
     * @param other 另一个金额
     * @return 新金额
     */
    public Money subtract(Money other) {
        return new Money(this.value.subtract(other.value));
    }

    /**
     * 金额乘法
     *
     * @param multiplier 乘数
     * @return 新金额
     */
    public Money multiply(int multiplier) {
        return new Money(this.value.multiply(BigDecimal.valueOf(multiplier)));
    }

    /**
     * 金额乘法（Decimal）
     *
     * @param multiplier 乘数
     * @return 新金额
     */
    public Money multiply(BigDecimal multiplier) {
        return new Money(this.value.multiply(multiplier));
    }

    /**
     * 金额除法
     *
     * @param divisor 除数
     * @return 新金额
     */
    public Money divide(int divisor) {
        return new Money(this.value.divide(BigDecimal.valueOf(divisor), SCALE, ROUNDING_MODE));
    }

    /**
     * 比较金额大小
     *
     * @param other 另一个金额
     * @return 大于返回1，等于返回0，小于返回-1
     */
    public int compareTo(Money other) {
        return this.value.compareTo(other.value);
    }

    /**
     * 判断是否大于
     *
     * @param other 另一个金额
     * @return true 如果大于
     */
    public boolean greaterThan(Money other) {
        return this.value.compareTo(other.value) > 0;
    }

    /**
     * 判断是否小于
     *
     * @param other 另一个金额
     * @return true 如果小于
     */
    public boolean lessThan(Money other) {
        return this.value.compareTo(other.value) < 0;
    }

    /**
     * 判断是否等于
     *
     * @param other 另一个金额
     * @return true 如果等于
     */
    public boolean equalsMoney(Money other) {
        return this.value.compareTo(other.value) == 0;
    }

    /**
     * 转换为分
     *
     * @return 金额（分）
     */
    public Long toCent() {
        return this.value.multiply(BigDecimal.valueOf(100)).longValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Money money = (Money) o;
        return Objects.equals(value, money.value);
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
