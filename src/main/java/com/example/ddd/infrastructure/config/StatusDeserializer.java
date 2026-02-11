package com.example.ddd.infrastructure.config;

import com.example.ddd.domain.model.valueobject.Status;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Status 值对象的 JSON 反序列化器
 * 将整数反序列化为 Status
 *
 * @author DDD Demo
 */
public class StatusDeserializer extends JsonDeserializer<Status> {

    @Override
    public Status deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Integer value = p.getValueAsInt();
        if (value == null) {
            return null;
        }
        // 默认使用 UserStatus，如果需要其他类型，应该在具体上下文中处理
        return Status.ofUser(value);
    }
}
