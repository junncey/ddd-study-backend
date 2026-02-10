package com.example.ddd.application;

import lombok.extern.slf4j.Slf4j;

/**
 * 应用服务基类
 * 编排领域对象，处理用例逻辑
 *
 * @author DDD Demo
 */
@Slf4j
public abstract class ApplicationService {

    /**
     * 执行应用服务前的处理
     */
    protected void beforeExecute() {
        log.debug("开始执行应用服务: {}", this.getClass().getSimpleName());
    }

    /**
     * 执行应用服务后的处理
     */
    protected void afterExecute() {
        log.debug("执行应用服务完成: {}", this.getClass().getSimpleName());
    }
}
