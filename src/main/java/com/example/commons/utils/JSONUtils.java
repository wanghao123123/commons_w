package com.example.commons.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.List;


/**
 * @author hWang
 * @date Created in 2021/3/31
 */
@Slf4j
public class JSONUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true);
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static <T> List<T> toArray(String json, Class<T> e) {
        List o = null;
        try {
            o = OBJECT_MAPPER.readValue(json, OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, e));
        } catch (JsonProcessingException jsonProcessingException) {
            log.error(jsonProcessingException.getMessage());
        }
        return o;
    }

    public static <T> List<T> toArrayThrow(String json, Class<T> e) {
        List o = null;
        try {
            o = OBJECT_MAPPER.readValue(json, OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, e));
        } catch (JsonProcessingException jsonProcessingException) {
            throw new RuntimeException(jsonProcessingException);
        }
        return o;
    }

    public static <T> T toObjectThrow(String json, Class<T> e) {
        T o = null;
        try {
            o = OBJECT_MAPPER.readValue(json, e);
        } catch (JsonProcessingException jsonProcessingException) {
            throw new RuntimeException(jsonProcessingException);
        }
        return o;
    }

    public static String ObjToString(Object obj) {
        String o = null;
        try {
            o = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException jsonProcessingException) {
            throw new RuntimeException(jsonProcessingException);
        }
        return o;
    }
}
