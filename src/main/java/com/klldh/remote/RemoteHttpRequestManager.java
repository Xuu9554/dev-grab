package com.klldh.remote;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.klldh.exception.ServiceAssert;
import com.klldh.exception.ServiceException;
import com.klldh.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tools.jackson.core.type.TypeReference;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.Function;

import static com.klldh.common.Constant.REMOTE_COMMUNICATION_FAILURE;

@Slf4j
public abstract class RemoteHttpRequestManager implements Serializable {

    @Serial
    private final static long serialVersionUID = 2572398622106224660L;

    /**
     * 确定服务接口路径
     *
     * @return {@link String} 服务接口路径
     */
    public String determineRequestUrl() {
        return StrUtil.emptyIfNull(this.resolveServiceUrlPrefix()).concat(StrUtil.emptyIfNull(this.resolveServiceUrl()));
    }

    /**
     * 设置连接超时毫秒数(默认10秒)
     *
     * @return int 连接超时毫秒数
     */
    public int setupConnectTimeout() {
        return 10000;
    }

    /**
     * 服务接口路径前缀
     *
     * @return {@link String} 服务接口路径前缀
     */
    public abstract String resolveServiceUrlPrefix();

    /**
     * 服务接口路径
     *
     * @return {@link String} 服务接口路径
     */
    public abstract String resolveServiceUrl();

    /**
     * 序列化请求参数
     *
     * @return {@link String} 请求参数
     */
    public String serializeToContent() {
        return JsonHelper.toJSONString(this);
    }

    /**
     * 对已被序列化过的请求参数二次处理
     * <p>
     * <br/><br/>
     * <p>此方法允许你根据不同的业务需求对已序列化过的请求参数再进一步的处理，</p>
     * <p>无论是加密、添加签名还是其他任何形式的修改，确保请求参数符合你预期的业务逻辑</p>
     *
     * @param content 已被序列化的请求参数
     * @return {@link String} 二次处理后的请求参数
     */
    public String processThenSerializeContent(String content) {
        return content;
    }

    /**
     * 发送POST请求
     *
     * @return {@link String} 响应数据
     */
    public String executeHttpRequest() {

        long start = SystemClock.now();
        HttpRequest requestBody = this.prepareHttpRequest();
        this.afterRequestPrepared(requestBody);

        HttpResponse response;
        try {
            response = requestBody.execute();
        } catch (Exception e) {
            log.error(StrUtil.format("执行远程服务时发生错误: {}", e.getMessage()), e);
            throw new ServiceException(REMOTE_COMMUNICATION_FAILURE);
        }
        log.info("远程服务通讯成功, 响应状态码: {}, 耗时: {}ms", response.getStatus(), (SystemClock.now() - start));
        ServiceAssert.isTrue(response.isOk(), REMOTE_COMMUNICATION_FAILURE);
        return response.body();
    }

    /**
     * 自定义Http请求(默认为POST请求)
     *
     * @return {@link HttpRequest} Http请求
     */
    public HttpRequest prepareHttpRequest() {

        // 请求路径
        String requestUrl = this.determineRequestUrl();

        // 序列化请求参数
        String content = this.serializeToContent();

        log.info("接口路径: {}, 请求参数: {}", requestUrl, content);

        return HttpRequest.post(requestUrl).timeout(this.setupConnectTimeout())
                // 对已被序列化过的请求参数二次处理
                .contentType("application/json; charset=UTF-8").body(this.processThenSerializeContent(content));
    }

    /**
     * 请求构建完成后的扩展回调
     *
     * @param requestBody 请求参数
     */
    public void afterRequestPrepared(HttpRequest requestBody) {
        // do nothing...
    }

    /**
     * 执行远程服务，并将响应报文转化为你指定的数据类型
     *
     * @param responseConverter 响应报文转化为你指定的数据类型的函数
     * @return {@link T} 响应数据
     */
    @Nullable
    public <T> T executeThenDeserialize(@NotNull Function<String, T> responseConverter) {
        return responseConverter.apply(this.executeHttpRequest());
    }

    /**
     * 执行远程服务，并将响应报文转化为你指定的数据类型
     *
     * @param targetClass 响应类型
     * @return {@link Opt}<{@link T}> 反序列化后的数据
     */
    public <T> Opt<T> executeThenDeserializeAs(@NotNull Class<T> targetClass) {
        return this.executeThenDeserialize(content -> Opt.ofNullable(JsonHelper.parseObject(content, targetClass)));
    }

    /**
     * 执行远程服务，并将响应报文转化为你指定的数据类型
     *
     * @param typeResolver 响应类型解析器
     * @return {@link Opt}<{@link T}> 反序列化后的数据
     */
    public <T> Opt<T> executeThenDeserializeAs(@NotNull TypeReference<T> typeResolver) {
        return this.executeThenDeserialize(content -> Opt.ofNullable(JsonHelper.deserializeWithTypeReference(content, typeResolver)));
    }

}