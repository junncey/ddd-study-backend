package com.example.ddd.infrastructure.config;

import com.example.ddd.infrastructure.storage.StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 配置静态资源映射
 *
 * @author DDD Demo
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final StorageProperties storageProperties;

    /**
     * 配置静态资源映射
     * 将 /uploads/** 映射到本地存储目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 只有本地存储才需要配置静态资源映射
        if ("LOCAL".equalsIgnoreCase(storageProperties.getType())) {
            String uploadPath = storageProperties.getLocal().getPath();
            String location = "file:" + uploadPath + "/";

            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(location);

            log.info("静态资源映射配置: /uploads/** -> {}", location);
        }
    }
}
