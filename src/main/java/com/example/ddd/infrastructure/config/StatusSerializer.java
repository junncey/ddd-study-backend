package com.example.ddd.infrastructure.config;

import com.example.ddd.domain.model.valueobject.Status;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Status 值对象的 JSON 序列化器
 * 将 Status 序列化为整数
 *
 * @author DDD Demo
 */
public class StatusSerializer extends JsonSerializer<Status> {

    @Override
    public void serialize(Status status, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (status == null) {
            gen.writeNull();
        } else {
            gen.writeNumber(status.getValue());
        }
    }
}
