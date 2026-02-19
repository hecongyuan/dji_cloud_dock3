package com.dji.sample.manage.model.deserializer;

import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * 自定义PayloadIndex反序列化器，处理空字符串情况
 * @author system
 * @date 2025/8/28
 */
public class PayloadIndexDeserializer extends JsonDeserializer<PayloadIndex> {

    @Override
    public PayloadIndex deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        
        // 如果是空字符串或null，返回null
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            return new PayloadIndex(value);
        } catch (Exception e) {
            // 如果解析失败，返回null而不是抛出异常
            return null;
        }
    }
}