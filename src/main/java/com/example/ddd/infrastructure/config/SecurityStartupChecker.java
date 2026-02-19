package com.example.ddd.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;

/**
 * 安全启动检查器
 * 在应用启动时检查关键安全配置
 *
 * @author DDD Demo
 */
@Slf4j
@Configuration
public class SecurityStartupChecker {

    private final Environment environment;

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Value("${spring.datasource.password:}")
    private String dbPassword;

    // 已知的开发/测试用默认密钥前缀
    private static final String[] KNOWN_INSECURE_SECRETS = {
            "devSecretKey",
            "testSecretKey",
            "defaultSecret",
            "changeMe",
            "pleaseReplace",
            "ForTesting",
            "development",
            "example",
            "12345678",
            "password",
            "secret"
    };

    public SecurityStartupChecker(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void checkSecurityConfig() {
        log.info("========== 安全配置检查开始 ==========");

        boolean isProduction = isProductionEnvironment();
        boolean hasIssues = false;

        // 1. JWT密钥检查
        hasIssues |= checkJwtSecret(isProduction);

        // 2. 数据库密码检查
        hasIssues |= checkDatabasePassword(isProduction);

        // 3. Druid监控检查
        hasIssues |= checkDruidMonitor(isProduction);

        // 4. Swagger检查
        hasIssues |= checkSwagger(isProduction);

        // 5. Redis密码检查
        hasIssues |= checkRedisPassword(isProduction);

        if (hasIssues && isProduction) {
            log.error("========================================");
            log.error("检测到生产环境存在安全配置问题！");
            log.error("请修复上述问题后再启动应用");
            log.error("========================================");
            throw new SecurityException("生产环境安全配置检查失败，请检查日志并修复问题");
        }

        log.info("========== 安全配置检查完成 ==========");
    }

    /**
     * 判断是否为生产环境
     */
    private boolean isProductionEnvironment() {
        String[] activeProfiles = environment.getActiveProfiles();
        return Arrays.stream(activeProfiles)
                .anyMatch(profile -> profile.equalsIgnoreCase("prod")
                        || profile.equalsIgnoreCase("production")
                        || profile.equalsIgnoreCase("live"));
    }

    /**
     * 检查JWT密钥安全性
     */
    private boolean checkJwtSecret(boolean isProduction) {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            log.error("[安全] JWT密钥未配置！请设置环境变量 JWT_SECRET");
            return true;
        }

        // 检查密钥长度
        if (jwtSecret.length() < 64) {
            if (isProduction) {
                log.error("[安全] JWT密钥长度不足64字符，当前长度: {}", jwtSecret.length());
                return true;
            } else {
                log.warn("[安全] JWT密钥长度建议至少64字符，当前长度: {}", jwtSecret.length());
            }
        }

        // 检查是否使用已知的不安全密钥
        String lowerSecret = jwtSecret.toLowerCase();
        for (String insecure : KNOWN_INSECURE_SECRETS) {
            if (lowerSecret.contains(insecure.toLowerCase())) {
                if (isProduction) {
                    log.error("[安全] JWT密钥包含不安全的关键词: {}", insecure);
                    return true;
                } else {
                    log.warn("[安全] JWT密钥包含不安全的关键词: {}（仅警告，开发环境允许）", insecure);
                }
                break;
            }
        }

        log.info("[安全] JWT密钥检查通过");
        return false;
    }

    /**
     * 检查数据库密码
     */
    private boolean checkDatabasePassword(boolean isProduction) {
        if (dbPassword == null || dbPassword.isBlank()) {
            log.warn("[安全] 数据库密码为空");
            return false; // 仅警告，不阻止启动
        }

        String lowerPassword = dbPassword.toLowerCase();
        if (lowerPassword.equals("root") || lowerPassword.equals("password")
                || lowerPassword.equals("123456") || lowerPassword.equals("admin")) {
            if (isProduction) {
                log.error("[安全] 数据库使用了弱密码: {}", dbPassword);
                return true;
            } else {
                log.warn("[安全] 数据库使用了弱密码（仅警告，开发环境允许）");
            }
        }

        log.info("[安全] 数据库密码检查通过");
        return false;
    }

    /**
     * 检查Druid监控
     */
    private boolean checkDruidMonitor(boolean isProduction) {
        Boolean druidEnabled = environment.getProperty("spring.datasource.druid.stat-view-servlet.enabled",
                Boolean.class, false);

        if (druidEnabled && isProduction) {
            log.warn("[安全] 生产环境不建议启用Druid监控面板");
            // 仅警告，不阻止启动
        }

        return false;
    }

    /**
     * 检查Swagger
     */
    private boolean checkSwagger(boolean isProduction) {
        Boolean swaggerEnabled = environment.getProperty("springdoc.enable", Boolean.class, false);

        if (swaggerEnabled && isProduction) {
            log.error("[安全] 生产环境不应启用Swagger API文档");
            return true;
        }

        log.info("[安全] Swagger配置检查通过");
        return false;
    }

    /**
     * 检查Redis密码
     */
    private boolean checkRedisPassword(boolean isProduction) {
        String redisPassword = environment.getProperty("spring.data.redis.password", "");

        if ((redisPassword == null || redisPassword.isBlank()) && isProduction) {
            log.warn("[安全] 生产环境Redis建议设置密码");
            // 仅警告，不阻止启动
        }

        return false;
    }
}
