package com.example.ddd.domain.model.valueobject;

/**
 * 状态类型接口
 * 所有状态枚举都需要实现此接口
 *
 * @author DDD Demo
 */
public interface StatusType {

    /**
     * 获取状态值
     *
     * @return 状态值
     */
    Integer getValue();

    /**
     * 获取状态描述
     *
     * @return 状态描述
     */
    String getDescription();
}
