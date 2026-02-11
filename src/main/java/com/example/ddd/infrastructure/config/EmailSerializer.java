package com.example.ddd.infrastructure.config;

import com.example.ddd.domain.model.valueobject.Email;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Email 值对象的 JSON 序列化器
 * 将 Email 序列化为字符串
 *
 * @author DDD Demo
 */
public class EmailSerializer extends JsonSerializer<Email> {

    @Override
    public void serialize(Email email, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (email == null) {
            gen.writeNull();
        } else {
            gen.writeString(email.getValue());
        }
    }
}
