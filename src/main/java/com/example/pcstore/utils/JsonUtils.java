package com.example.pcstore.utils;
import com.example.pcstore.logging.LoggingFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class JsonUtils {

    private JsonUtils() {
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static final Logger LOGGER = LoggingFactory.getLogger(JsonUtils.class);

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.error("[JSON UTILS][WRITE VALUE AS STRING] Exception: {}", e.getMessage());
            return null;
        }
    }

    public static <T> T toObject(Object string, TypeReference<T> valueTypeRef) {
        return objectMapper.convertValue(string, valueTypeRef);
    }

    public static <T> T toObject(String string, TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(string, valueTypeRef);
        } catch (JsonProcessingException e) {
            LOGGER.error("[JSON UTILS][CONVERT TO OBJECT] Exception: {}", e.getMessage());
            return null;
        }
    }

    public static <T> T toObject(String json, Class<T> tClass) {
        try {
            return objectMapper.readValue(json, tClass);
        } catch (JsonProcessingException e) {
            LOGGER.error("[JSON UTILS][CONVERT TO OBJECT] Exception: {}", e.getMessage());
            return null;
        }
    }

    public static <T> T toObject(byte[] src, Class<T> tClass) {
        try {
            return objectMapper.readValue(src, tClass);
        } catch (IOException e) {
            LOGGER.error("[JSON UTILS][CONVERT TO OBJECT] Exception: {}", e.getMessage());
            return null;
        }
    }
}

