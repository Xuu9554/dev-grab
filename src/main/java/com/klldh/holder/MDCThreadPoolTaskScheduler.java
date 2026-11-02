package com.klldh.holder;

import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.Serial;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@SuppressWarnings("NullableProblems")
public class MDCThreadPoolTaskScheduler extends ThreadPoolTaskScheduler {

    @Serial
    private static final long serialVersionUID = -3327123909000132264L;

    @Override
    public void execute(Runnable task) {
        super.execute(MDCThreadPoolTaskHolder.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(MDCThreadPoolTaskHolder.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(MDCThreadPoolTaskHolder.wrap(task, MDC.getCopyOfContextMap()));
    }

}