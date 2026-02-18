package com.example.ddd.infrastructure.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.example.ddd.infrastructure.security.UserDetailsImpl;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 配置类
 *
 * @author DDD Demo
 */
@Configuration
@MapperScan("com.example.ddd.infrastructure.persistence.mapper")
public class MybatisPlusConfig {

    /**
     * MyBatis Plus 拦截器
     * 配置分页、乐观锁、防止全表更新删除
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setMaxLimit(1000L);  // 单页最大数量限制
        paginationInterceptor.setOverflow(false);   // 溢出总页数后是否进行处理
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 防止全表更新和删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }

    /**
     * 自动填充配置
     * 自动填充创建时间、更新时间、创建人、更新人
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {

            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "createBy", String.class, getCurrentUser());
                this.strictInsertFill(metaObject, "updateBy", String.class, getCurrentUser());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                this.strictUpdateFill(metaObject, "updateBy", String.class, getCurrentUser());
            }

            /**
             * 获取当前用户
             * 从 Spring Security 安全上下文中获取当前登录用户
             * 返回格式：用户ID-用户名，例如 "1-admin"
             * 如果未获取到用户信息，则返回 "noLogin"
             */
            private String getCurrentUser() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()
                        && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
                    // 返回格式：用户ID-用户名
                    return userDetails.getId() + "-" + userDetails.getUsername();
                }
                // 如果是匿名用户或未认证，返回默认值
                return "noLogin";
            }
        };
    }
}
