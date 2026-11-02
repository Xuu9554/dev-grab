package com.klldh.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.validation.BindException;

import java.util.function.Function;

import static com.klldh.common.Constant.DEFAULT_ERROR_MESSAGE;

@Getter
@AllArgsConstructor
public enum ExceptionAmityHintEnum {

    /**
     * 使用{@link jakarta.validation.constraints}包下的注解时，自定义的message信息将优先于业务异常抛出
     * <p>
     * <b>注意：</b> 如果想要使 {@link jakarta.validation.constraints} 包下的注解的校验生效，
     * 必须在 controller 的入参前面加上 {@link jakarta.validation.Valid}。
     * </p>
     */
    BIND_EXCEPTION(BindException.class, t -> ((BindException) t).getAllErrors().getFirst().getDefaultMessage(), false),

    /**
     * 项目自定义异常抛出
     *
     * @see ServiceException
     * @see ServiceAssert#isTrue(boolean, String, Object...)
     */
    SERVICE_NORMAL_EXCEPTION_HINT(ServiceException.class, Throwable::getMessage, false),

    CLIENT_ABORT_EXCEPTION_HINT(ClientAbortException.class, t -> "服务器连接被中断，请稍后重试", false),

    RUNTIME_EXCEPTION_HINT(RuntimeException.class, t -> "服务器运行异常，请稍后重试"),

    /**
     * 默认异常，将提示[服务器异常，请稍后重试]
     */
    EXCEPTION_HINT(Exception.class, t -> DEFAULT_ERROR_MESSAGE),

    ;

    /**
     * 异常类型
     */
    private final Class<?> rootCause;

    /**
     * 执行函数
     */
    private final Function<Throwable, String> function;

    /**
     * 是否需要打印异常日志
     */
    private final boolean needPrintStackTraceOperate;

    /**
     * 构造ExceptionAmityHintEnum
     *
     * @param rootCause 异常类型
     * @param function  执行函数
     */
    ExceptionAmityHintEnum(Class<?> rootCause, Function<Throwable, String> function) {
        this(rootCause, function, true);
    }

}
