package com.klldh.common;

import tools.jackson.core.type.TypeReference;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class Constant {

    /**
     * 接口运行结果：成功
     */
    public final static Integer SUCCESS = 0;

    /**
     * 接口运行结果：失败
     */
    public final static Integer FAIL = 1;

    /**
     * 默认报错提示语
     */
    public final static String DEFAULT_ERROR_MESSAGE = "服务器异常，请稍后重试";

    /**
     * 远程服务异常
     */
    public final static String REMOTE_COMMUNICATION_FAILURE = "远程服务异常，请稍后重试";

    /**
     * 项目root路径
     */
    public final static String ROOT_PATH = System.getProperty("user.dir");

    /**
     * 项目名称
     */
    public final static String SPRING_APPLICATION_NAME = "dev-grab";

    /**
     * trace_id
     */
    public final static String TRACE_ID = "trace_id";

    /**
     * 是否是整数
     */
    public final static Pattern PLAIN_INTEGER = Pattern.compile("^(?:0|-?[1-9]\\d*)$");

    /**
     * 中英文逗号分隔符
     */
    public final static Pattern COMMA_SEPARATOR = Pattern.compile("[,，]\\s*");

    /**
     * Map反序列化类型（统一复用，避免重复创建）
     */
    public final static TypeReference<LinkedHashMap<String, Object>> LINKED_HASH_MAP_TYPE = new TypeReference<>() {

    };

}
