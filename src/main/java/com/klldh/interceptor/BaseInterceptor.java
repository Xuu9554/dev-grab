package com.klldh.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

@Component
public abstract class BaseInterceptor implements HandlerInterceptor {

    /**
     * 默认拦截交易路径
     */
    private final static List<String> DEFAULT_PATH_PATTERNS = new ArrayList<>();

    /**
     * 交易路径白名单
     */
    private final static List<String> DEFAULT_INTERCEPTOR_EXCLUDE_PATH_PATTERNS = new ArrayList<>();

    static {

        // 默认所有路径都需要被拦截
        DEFAULT_PATH_PATTERNS.add("/**");
        // 当swagger页面进不去时会跳转到404页面, 因此需要放行这个页面
        DEFAULT_INTERCEPTOR_EXCLUDE_PATH_PATTERNS.add("/error");

        // 放行swagger
        DEFAULT_INTERCEPTOR_EXCLUDE_PATH_PATTERNS.add("/");
        DEFAULT_INTERCEPTOR_EXCLUDE_PATH_PATTERNS.add("/csrf");
        DEFAULT_INTERCEPTOR_EXCLUDE_PATH_PATTERNS.add("/swagger-resources/**");
        DEFAULT_INTERCEPTOR_EXCLUDE_PATH_PATTERNS.add("/webjars/**");
        DEFAULT_INTERCEPTOR_EXCLUDE_PATH_PATTERNS.add("/v2/**");
        DEFAULT_INTERCEPTOR_EXCLUDE_PATH_PATTERNS.add("/swagger-ui.html/**");
    }

    /**
     * 获取默认拦截路径
     *
     * @return {@link List}<{@link String}> 默认拦截路径
     */
    public List<String> getPathPatterns() {
        return new ArrayList<>(DEFAULT_PATH_PATTERNS);
    }

    /**
     * 获取交易放行白名单
     *
     * @return {@link List}<{@link String}> 放行白名单
     */
    public List<String> getInterceptorExcludePathPatterns() {
        List<String> interceptorExcludePathPatterns = new ArrayList<>(DEFAULT_INTERCEPTOR_EXCLUDE_PATH_PATTERNS);
        this.extendInterceptorExcludePathPatterns(interceptorExcludePathPatterns);
        return interceptorExcludePathPatterns;
    }

    /**
     * 在默认交易放行白名单的基础上添加其他的白名单交易路径
     *
     * @param interceptorExcludePathPatterns 默认交易放行白名单
     */
    public void extendInterceptorExcludePathPatterns(List<String> interceptorExcludePathPatterns) {

    }

    /**
     * 拦截器是否开启
     *
     * @return boolean 是否开启
     */
    public boolean isEnabled() {
        return true;
    }

}
