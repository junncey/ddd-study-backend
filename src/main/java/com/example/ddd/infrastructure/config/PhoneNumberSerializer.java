package com.example.ddd.infrastructure.config;

import com.example.ddd.domain.model.valueobject.PhoneNumber;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * PhoneNumber 值对象的 JSON 序列化器
 * 将 PhoneNumber 序列化为字符串
 *
 * @author DDD Demo
 */
public class PhoneNumberSerializer extends JsonSerializer<PhoneNumber> {

    @Override
    public void serialize(PhoneNumber phoneNumber, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (phoneNumber == null) {
            gen.writeNull();
        } else {
            gen.writeString(phoneNumber.getValue());
        }
    }
}
