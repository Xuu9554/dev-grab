package com.klldh.exception;

import java.util.function.Supplier;

public class ServiceAssert {

    /**
     * 断言
     *
     * @param condition    结论
     * @param errorMessage 错误信息
     * @param params       错误信息填充参数
     */
    public static void isTrue(boolean condition, String errorMessage, Object... params) {
        isTrue(condition, () -> new ServiceException(errorMessage, params));
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出给定的异常<br>
     *
     * <pre class="code">
     * ServiceAssert.isTrue(i &gt; 0, ServiceException::new);
     * </pre>
     *
     * @param <X>       异常类型
     * @param condition 结论
     * @param supplier  指定断言不通过时抛出的异常
     * @throws X if expression is {@code false}
     */
    public static <X extends Throwable> void isTrue(boolean condition, Supplier<? extends X> supplier) throws X {
        if (!condition) {
            throw supplier.get();
        }
    }

}
