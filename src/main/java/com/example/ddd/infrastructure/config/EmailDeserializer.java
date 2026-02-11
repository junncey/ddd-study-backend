package com.example.ddd.infrastructure.config;

import com.example.ddd.domain.model.valueobject.Email;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Email 值对象的 JSON 反序列化器
 * 将字符串反序列化为 Email
 *
 * @author DDD Demo
 */
public class EmailDeserializer extends JsonDeserializer<Email> {

    @Override
    public Email deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Email.of(value);
    }
}
