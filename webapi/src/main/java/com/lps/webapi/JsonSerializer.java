package com.lps.webapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by user on 07.08.2015.
 */
public class JsonSerializer {
    public static <T> String serialize(T object) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        //mapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
        return mapper.writeValueAsString(object);

    }

    public static <T> String serialize(List<T> object) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        //mapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
        return mapper.writeValueAsString(object);

    }

    public static <T> byte[] serializeAsByteArray(T object) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        //mapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, object);
        return out.toByteArray();

    }

    public static <T> T deserialize(String object, Class<T> valueType) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(object, valueType);
    }

    public static <T> T deserialize(byte[] object, Class<T> valueType) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        //mapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
        String str = new String(object, StandardCharsets.UTF_8);
        return mapper.readValue(object, valueType);
    }

    public static <T> List<T> deserializeList(String in, Class<T> valueType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        //mapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
        JavaType jt = mapper.getTypeFactory().constructCollectionType(List.class, valueType);
        return mapper.readValue(in, jt);
    }
}
