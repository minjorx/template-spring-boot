package com.minjor.common.utils;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Jackson JSON 工具类 (基于 Jackson 3.x)
 */
public class JacksonUtil {
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    private static final JsonMapper JSON_MAPPER;

    static {
        SimpleModule customModule = new SimpleModule("CustomModule");
        customModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        customModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        customModule.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        JSON_MAPPER = JsonMapper.builder()
                .build();
    }

    /**
     * 获取全局统一配置的 JsonMapper
     */
    public static JsonMapper getJsonMapper() {
        return JSON_MAPPER;
    }

    /**
     * 将对象序列化为 JSON 字符串
     */
    public static String toJson(Object object) {
        try {
            return object == null ? null : JSON_MAPPER.writeValueAsString(object);
        } catch (JacksonException e) { // 注意：Jackson 3.x 异常为运行时异常
            throw new RuntimeException("序列化为JSON字符串失败", e);
        }
    }

    /**
     * 将对象序列化为 JSON 字节数组
     */
    public static byte[] toJsonBytes(Object object) {
        try {
            return object == null ? null : JSON_MAPPER.writeValueAsBytes(object);
        } catch (JacksonException e) {
            throw new RuntimeException("序列化为JSON字节数组失败", e);
        }
    }

    /**
     * 将对象序列化并写入文件
     */
    public static void writeToFile(File file, Object object) {
        try {
            if (object != null && file != null) {
                JSON_MAPPER.writeValue(file, object);
            }
        } catch (Exception e) { // 可能包含IO异常
            throw new RuntimeException("写入JSON到文件失败: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * 将对象序列化并写入输出流
     */
    public static void writeToStream(OutputStream out, Object object) {
        try {
            if (object != null && out != null) {
                JSON_MAPPER.writeValue(out, object);
            }
        } catch (Exception e) {
            throw new RuntimeException("写入JSON到输出流失败", e);
        }
    }

    // ==================== 反序列化方法 (JSON -> Object) ====================

    /**
     * 将 JSON 字符串反序列化为指定类型的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return json == null || json.isEmpty() ? null : JSON_MAPPER.readValue(json, clazz);
        } catch (JacksonException e) {
            throw new RuntimeException("JSON字符串反序列化为对象失败，目标类型: " + clazz.getName(), e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为泛型对象 (例如 List<User>, Map<String, Object>)
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return json == null || json.isEmpty() ? null : JSON_MAPPER.readValue(json, typeReference);
        } catch (JacksonException e) {
            throw new RuntimeException("JSON字符串反序列化为泛型对象失败", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为 List 集合
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> elementClass) {
        JavaType javaType = JSON_MAPPER.getTypeFactory()
                .constructCollectionType(List.class, elementClass);
        try {
            return json == null || json.isEmpty() ? null : JSON_MAPPER.readValue(json, javaType);
        } catch (JacksonException e) {
            throw new RuntimeException("JSON字符串反序列化为List失败，元素类型: " + elementClass.getName(), e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为 Map 集合
     */
    public static <K, V> Map<K, V> fromJsonToMap(String json, Class<K> keyClass, Class<V> valueClass) {
        JavaType javaType = JSON_MAPPER.getTypeFactory()
                .constructMapType(Map.class, keyClass, valueClass);
        try {
            return json == null || json.isEmpty() ? null : JSON_MAPPER.readValue(json, javaType);
        } catch (JacksonException e) {
            throw new RuntimeException("JSON字符串反序列化为Map失败", e);
        }
    }

    /**
     * 从文件读取 JSON 并反序列化为对象
     */
    public static <T> T fromJson(File file, Class<T> clazz) {
        try {
            return file == null || !file.exists() ? null : JSON_MAPPER.readValue(file, clazz);
        } catch (Exception e) {
            throw new RuntimeException("从文件读取JSON并反序列化失败: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * 从输入流读取 JSON 并反序列化为对象
     */
    public static <T> T fromJson(InputStream inputStream, Class<T> clazz) {
        try {
            return inputStream == null ? null : JSON_MAPPER.readValue(inputStream, clazz);
        } catch (Exception e) {
            throw new RuntimeException("从输入流读取JSON并反序列化失败", e);
        }
    }

    // ==================== 树模型与节点操作 ====================

    /**
     * 将 JSON 字符串解析为 JsonNode 树模型
     */
    public static JsonNode readTree(String json) {
        try {
            return json == null || json.isEmpty() ? null : JSON_MAPPER.readTree(json);
        } catch (JacksonException e) {
            throw new RuntimeException("解析JSON字符串为JsonNode失败", e);
        }
    }

    /**
     * 将对象转换为 JsonNode
     */
    public static JsonNode valueToTree(Object object) {
        return JSON_MAPPER.valueToTree(object);
    }

    /**
     * 将 JsonNode 转换为指定类型的对象
     */
    public static <T> T treeToValue(JsonNode node, Class<T> clazz) {
        try {
            return node == null ? null : JSON_MAPPER.treeToValue(node, clazz);
        } catch (JacksonException e) {
            throw new RuntimeException("JsonNode转换为对象失败", e);
        }
    }

    public static String writeValueAsString(Object object) {
        try {
            return object == null ? null : JSON_MAPPER.writeValueAsString(object);
        } catch (JacksonException e) {
            throw new RuntimeException("对象转换为JSON字符串失败", e);
        }
    }
}