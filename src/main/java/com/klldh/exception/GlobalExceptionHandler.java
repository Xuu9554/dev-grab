package com.klldh.exception;

import com.klldh.common.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.klldh.common.Constant.DEFAULT_ERROR_MESSAGE;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 系统异常处理
     *
     * @param exception 系统异常
     * @return {@link ResponseEntity} 系统异常响应信息
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public <T> ResponseEntity<T> handleException(Exception exception) {
        for (ExceptionAmityHintEnum hintEnum : ExceptionAmityHintEnum.values()) {
            // 利用数组的有序性抛出异常
            Class<?> exceptionRootCause = hintEnum.getRootCause();
            if (exceptionRootCause.isAssignableFrom(exception.getClass())) {
                printStackTrace(exception, hintEnum.isNeedPrintStackTraceOperate());
                return ResponseEntity.error(hintEnum.getFunction().apply(exception));
            }
        }
        // 打印异常日志
        printStackTrace(exception);
        // 无法识别的异常信息，默认返回
        return ResponseEntity.error(DEFAULT_ERROR_MESSAGE);
    }

    /**
     * 输出异常堆栈信息
     *
     * @param exception 异常信息
     */
    private static void printStackTrace(Exception exception) {
        printStackTrace(exception, true);
    }

    /**
     * 输出异常堆栈信息
     *
     * @param exception           异常信息
     * @param needPrintStackTrace 是否需要打印输出异常堆栈信息
     */
    private static void printStackTrace(Exception exception, boolean needPrintStackTrace) {
        if (needPrintStackTrace) {
            log.error(exception.getMessage(), exception);
        }
    }

}
