package com.klldh.interceptor;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.klldh.common.Constant.TRACE_ID;
import static java.lang.Integer.MAX_VALUE;

@Component
@Order(-MAX_VALUE)
@SuppressWarnings("NullableProblems")
public class TraceInterceptor extends BaseInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 在任务执行前设置traceId
        MDC.put(TRACE_ID, StrUtil.format("[{}] ", IdUtil.fastSimpleUUID()));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除MDC
        MDC.remove(TRACE_ID);
    }

}