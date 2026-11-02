package com.klldh.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.type.TypeFactory;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;

import static cn.hutool.core.date.DatePattern.*;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.klldh.common.Constant.LINKED_HASH_MAP_TYPE;

@Slf4j
public class JsonHelper {

    /**
     * 安全地创建一个新的ObjectMapper实例
     *
     * @return {@link ObjectMapper} ObjectMapper实例
     */
    public static ObjectMapper newStandardObjectMapper() {
        return MAPPER.rebuild().build();
    }

    /**
     * 将对象序列化为JSON字符串
     *
     * @param dataSource 待序列化的Java对象
     * @return {@link String} 序列化后的JSON字符串，如果序列化失败返回空JSON
     */
    public static String toJSONString(Object dataSource) {
        return toJSONString(dataSource, false);
    }

    /**
     * 将对象序列化为JSON字符串
     *
     * @param dataSource 待序列化的Java对象
     * @return {@link String} 序列化后的JSON字符串，如果序列化失败返回空JSON
     */
    public static String toJSONString(Object dataSource, boolean isPretty) {

        if (ObjectUtil.isNull(dataSource)) {
            return "null";
        }

        try {
            return (isPretty ? PRETTY_WRITER : STANDARD_WRITER).writeValueAsString(dataSource);
        } catch (Exception e) {
            log.error(StrUtil.format("尝试序列化对象时发生错误，原因: [{}]", e.getMessage()), e);
            return "null";
        }
    }

    /**
     * 反序列化对象
     *
     * @param content     文本
     * @param targetClass 对象类型
     * @return {@link T} 序列化对象
     */
    @Nullable
    public static <T> T parseObject(String content, Class<T> targetClass) {
        return deserializeWithTypeFactory(content, factory -> factory.constructType(targetClass));
    }

    /**
     * 反序列化字节流
     *
     * @param serializedData 字节流
     * @param targetClass    对象类型
     * @return {@link T} 序列化对象
     */
    @Nullable
    public static <T> T parseObject(byte[] serializedData, Class<T> targetClass) {
        try {
            return MAPPER.readValue(serializedData, DEFAULT_TYPE_FACTORY.constructType(targetClass));
        } catch (Exception e) {
            log.error(StrUtil.format("尝试反序列化字节流时发生错误，原因: {}", e.getMessage()), e);
            return null;
        }
    }

    /**
     * 反序列化字节流
     *
     * @param serializedData 字节流
     * @param targetClass    对象类型
     * @return {@link T} 序列化对象
     */
    @Nullable
    public static <T> T parseObject(InputStream serializedData, Class<T> targetClass) {
        try {
            return MAPPER.readValue(serializedData, DEFAULT_TYPE_FACTORY.constructType(targetClass));
        } catch (Exception e) {
            log.error(StrUtil.format("尝试反序列化输入流时发生错误，原因: {}", e.getMessage()), e);
            return null;
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------

    /**
     * 反序列化列表
     *
     * @param content     文本
     * @param targetClass 列表对象类型
     * @return {@link List}<{@link T}> 列表
     */
    public static <T> List<T> parseArray(String content, Class<T> targetClass) {
        List<T> deserializedList = deserializeWithTypeFactory(content,
                factory -> factory.constructCollectionType(ArrayList.class, targetClass));
        return CollUtil.emptyIfNull(deserializedList);
    }

    /**
     * 反序列化为Map
     *
     * @param content 文本
     * @return {@link Map}<{@link String}, {@link Object}> LinkedHashMap
     */
    public static Map<String, Object> parseMap(String content) {
        return MapUtil.emptyIfNull(deserializeWithTypeReference(content, LINKED_HASH_MAP_TYPE));
    }

    /**
     * 反序列化输入流为Map
     *
     * @param serializedData 输入流
     * @return {@link Map}<{@link String}, {@link Object}> LinkedHashMap
     */
    public static Map<String, Object> parseMap(InputStream serializedData) {
        try {
            return MapUtil.emptyIfNull(MAPPER.readValue(serializedData, LINKED_HASH_MAP_TYPE));
        } catch (Exception e) {
            log.error(StrUtil.format("尝试反序列化字节流时发生错误，原因: {}", e.getMessage()), e);
            return Collections.emptyMap();
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------

    /**
     * 使用给定的类型函数来反序列化JSON字符串为你指定类型的对象
     *
     * @param content      文本
     * @param typeResolver 类型解析函数
     * @return {@link T} 反序列化后的对象；若解析失败则返回null
     */
    @Nullable
    public static <T> T deserializeWithTypeFactory(String content, Function<TypeFactory, JavaType> typeResolver) {
        try {
            return MAPPER.readValue(content, typeResolver.apply(DEFAULT_TYPE_FACTORY));
        } catch (Exception e) {
            log.error("尝试反序列化为指定类型时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 使用给定的类型函数来反序列化JSON字符串为你指定类型的对象
     *
     * @param content      文本
     * @param typeResolver 类型解析函数
     * @return {@link T} 反序列化后的对象；若解析失败则返回null
     */
    @Nullable
    public static <T> T deserializeWithTypeReference(String content, TypeReference<T> typeResolver) {
        try {
            return MAPPER.readValue(content, typeResolver);
        } catch (Exception e) {
            log.error("尝试反序列化为指定类型时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 使用给定的类型函数来反序列化JSON字符串为你指定类型的对象
     *
     * @param serializedData 字节流
     * @param typeResolver   类型解析函数
     * @return {@link T} 反序列化后的对象；若解析失败则返回null
     */
    @Nullable
    public static <T> T deserializeWithTypeReference(byte[] serializedData, TypeReference<T> typeResolver) {
        try {
            return MAPPER.readValue(serializedData, typeResolver);
        } catch (Exception e) {
            log.error("尝试反序列化为指定类型时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------

    private final static Set<JsonReadFeature> STANDARD_JSON_FEATURES = Set.of(
            // 允许在JSON中使用注释
            JsonReadFeature.ALLOW_JAVA_COMMENTS,
            // 允许JSON存在没用双引号括起来的field
            JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES,
            // 允许JSON存在使用单引号括起来的field
            JsonReadFeature.ALLOW_SINGLE_QUOTES,
            // 允许JSON存在没用引号括起来的ascii控制字符
            JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS,
            // 允许JSON[Number类型]的数存在前导0(例: 0001)
            JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS,
            // 允许JSON存在NaN, INF, -INF作为Number类型
            JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS,
            // 允许只有Key没有Value的情况
            JsonReadFeature.ALLOW_MISSING_VALUES,
            // 允许数组JSON的结尾多逗号
            JsonReadFeature.ALLOW_TRAILING_COMMA
    );

    private final static ObjectMapper MAPPER;

    private final static TypeFactory DEFAULT_TYPE_FACTORY;

    private final static ObjectWriter STANDARD_WRITER;

    private final static ObjectWriter PRETTY_WRITER;

    static {

        SimpleModule javaTimeModule = new SimpleModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(NORM_DATETIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(NORM_DATETIME_FORMATTER));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(NORM_DATE_FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(NORM_DATE_FORMATTER));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(NORM_TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(NORM_TIME_FORMATTER));

        MAPPER = JsonMapper.builder()
                .enable(STANDARD_JSON_FEATURES.toArray(new JsonReadFeature[0]))
                // 对象为空时不抛异常
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                // 允许单个数据当做数组处理
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                // 有属性不能映射的时候不报错
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // 设置时区
                .defaultTimeZone(TimeZone.getTimeZone("GMT+8"))
                .defaultDateFormat(new SimpleDateFormat(NORM_DATETIME_PATTERN))
                // 配置序列化级别
                .changeDefaultPropertyInclusion(inclusion -> JsonInclude.Value.construct(ALWAYS, ALWAYS))
                // Java时间类型格式化
                .addModule(javaTimeModule).build();

        DEFAULT_TYPE_FACTORY = MAPPER.getTypeFactory();

        STANDARD_WRITER = MAPPER.writer();
        PRETTY_WRITER = MAPPER.writerWithDefaultPrettyPrinter();
    }

}