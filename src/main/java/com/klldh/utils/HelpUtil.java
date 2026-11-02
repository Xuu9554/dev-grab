package com.klldh.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.klldh.common.Constant.COMMA_SEPARATOR;
import static com.klldh.common.Constant.PLAIN_INTEGER;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@Slf4j
@SuppressWarnings("unused")
public class HelpUtil {

    /**
     * 校验是否是整数
     *
     * @param content 文本
     * @return boolean 是否是整数
     */
    public static boolean isIntegral(String content) {
        return ReUtil.isMatch(PLAIN_INTEGER, content);
    }

    /**
     * 将文本根据逗号分隔(容忍节点之间存在任意数量的空格)
     *
     * @param content 文本
     * @return {@link List}<{@link String}> 分隔结果
     */
    public static List<String> splitByComma(String content) {
        return splitBySymbol(content, COMMA_SEPARATOR.pattern());
    }

    /**
     * 将文本根据给定符号分隔
     *
     * @param content 文本
     * @param symbol  分隔规则
     * @return {@link List}<{@link String}> 处理结果
     */
    public static List<String> splitBySymbol(String content, String symbol) {

        if (StrUtil.hasBlank(content, symbol)) {
            return new ArrayList<>();
        }

        return Stream.of(content.split(symbol)).map(StrUtil::cleanBlank).filter(StrUtil::isNotBlank).collect(Collectors.toList());
    }

    /**
     * 从整数集合中过滤掉所有null元素
     *
     * @param elements 可能包含null元素的集合
     * @return {@link Set}<{@link T}> 处理结果
     */
    public static <T> Set<T> filterNonNullElements(Collection<T> elements) {
        return filterMeaningfulElements(elements, Objects::nonNull);
    }

    /**
     * 从字符串集合中过滤掉所有空字符串
     *
     * @param elements 可能包含空白元素的{@link String}集合
     * @return {@link Set}<{@link String}> 处理结果
     */
    public static Set<String> filterNonBlankElements(Collection<String> elements) {
        return filterMeaningfulElements(elements, StrUtil::isNotBlank);
    }

    /**
     * 根据指定的断言条件从集合中过滤元素的通用方法
     *
     * @param <T>       集合中元素的类型
     * @param elements  包含类型为T的元素的集合，可能包含不满足predicate条件的元素
     * @param predicate 判断条件
     * @return {@link Set}<{@link T}> 处理结果
     */
    public static <T> Set<T> filterMeaningfulElements(Collection<T> elements, Predicate<? super T> predicate) {
        return Opt.ofNullable(elements).orElseGet(ArrayList::new).stream().filter(predicate).collect(Collectors.toSet());
    }

    /**
     * 重写{@link MapUtil#valuesOfKeys(Map, Iterator)}方法，解决原方法中存在的问题并优化使用场景：
     * <ol>
     *     <li>接受键的集合作为输入，而非不常用的Iterator，以符合更常见的使用场景</li>
     *     <li>自动过滤结果集中的null值，减少调用者处理这些异常的负担</li>
     * </ol>
     *
     * @param valueMapping Map
     * @param identifiers  键
     * @return {@link List}<{@link V}> 值
     */
    public static <K, V> List<V> valuesOfKeys(Map<K, V> valueMapping, Collection<K> identifiers) {

        if (MapUtil.isEmpty(valueMapping) || CollUtil.isEmpty(identifiers)) {
            return new ArrayList<>();
        }

        return identifiers.stream().map(valueMapping::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 填充Content-Disposition
     *
     * @param response 响应
     * @param fileName 文件名(带上后缀)
     */
    public static void formatContentDisposition(HttpServletResponse response, String fileName) {

        response.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);

        // 进行URL编码，并将加号替换为%20
        String encodedFileName = StrUtil.replace(URLEncoder.encode(fileName, UTF_8), "+", "%20");
        response.setHeader(CONTENT_DISPOSITION, StrUtil.format("attachment; filename={}", encodedFileName));
    }

}