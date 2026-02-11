package com.example.ddd.infrastructure.config;

import com.example.ddd.domain.model.valueobject.PhoneNumber;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * PhoneNumber 值对象的 JSON 反序列化器
 * 将字符串反序列化为 PhoneNumber
 *
 * @author DDD Demo
 */
public class PhoneNumberDeserializer extends JsonDeserializer<PhoneNumber> {

    @Override
    public PhoneNumber deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return PhoneNumber.of(value);
    }
}
