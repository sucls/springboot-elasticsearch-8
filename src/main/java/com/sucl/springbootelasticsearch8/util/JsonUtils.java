package com.sucl.springbootelasticsearch8.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sucl.springbootelasticsearch8.document.Novel;
import jakarta.json.stream.JsonGeneratorFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucl
 * @date 2023/6/17 11:26
 * @since 1.0.0
 */
@Slf4j
public class JsonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象【{}】转换json失败:{}", obj, e.getMessage(), e);
            return null;
        }
    }

    public static <T> T toObject(String json, Class<T> type){
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            log.error("json【{}】转换对象失败:{}", json, e.getMessage(), e);
            return null;
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Novel novel = new Novel();
        novel.setTitle("XXX");
        System.out.println( toJson(novel) );

        String json = "{\"title\":\"XXX\",\"name\":\"ABC\"}";
        System.out.println( toObject(json, Novel.class) );
    }
}
