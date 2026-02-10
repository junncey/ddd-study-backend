package com.example.ddd.domain.service;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 领域服务基类
 * 包含核心业务逻辑，不依赖任何外部框架
 *
 * @author DDD Demo
 */
@Slf4j
public abstract class DomainService {

    /**
     * 执行领域逻辑前的校验
     */
    protected void validate() {
        // 子类可以重写此方法实现校验逻辑
    }

    /**
     * 记录领域事件
     *
     * @param event 事件对象
     */
    protected void publishEvent(Serializable event) {
        log.info("发布领域事件: {}", event);
        // 这里可以集成事件发布机制
    }
}
