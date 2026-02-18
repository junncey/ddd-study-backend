package com.example.ddd.infrastructure.config;

import com.example.ddd.domain.model.valueobject.Email;
import com.example.ddd.domain.model.valueobject.PhoneNumber;
import com.example.ddd.domain.model.valueobject.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson 配置类
 * 配置值对象的 JSON 序列化和反序列化
 *
 * @author DDD Demo
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 注册 Java 8 时间模块
        mapper.registerModule(new JavaTimeModule());
        // 禁用将日期写为时间戳
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 注册自定义模块
        SimpleModule module = new SimpleModule();
        module.addSerializer(Email.class, new EmailSerializer());
        module.addDeserializer(Email.class, new EmailDeserializer());
        module.addSerializer(PhoneNumber.class, new PhoneNumberSerializer());
        module.addDeserializer(PhoneNumber.class, new PhoneNumberDeserializer());
        module.addSerializer(Status.class, new StatusSerializer());
        module.addDeserializer(Status.class, new StatusDeserializer());

        mapper.registerModule(module);

        return mapper;
    }
}
