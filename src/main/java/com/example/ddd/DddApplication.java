package com.example.ddd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * DDD 项目启动类
 *
 * @author DDD Demo
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class DddApplication {

    public static void main(String[] args) {
        SpringApplication.run(DddApplication.class, args);
        System.out.println("DDD 项目启动成功！");
    }
}
