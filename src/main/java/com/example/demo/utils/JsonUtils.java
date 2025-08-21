package com.example.demo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtils {

    private final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
    }

    public <T> T fromObject(Object object, Class<T> returnType) {
        try {
            return mapper.convertValue(object, returnType);
        } catch (Exception e) {
            throw new RuntimeException("fromObject() error", e);
        }
    }
}
