package com.klldh.holder;

import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.Serial;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@SuppressWarnings("NullableProblems")
public class MDCThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    @Serial
    private static final long serialVersionUID = 1508656694718078397L;

    @Override
    public void execute(Runnable task) {
        super.execute(MDCThreadPoolTaskHolder.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(MDCThreadPoolTaskHolder.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(MDCThreadPoolTaskHolder.wrap(task, MDC.getCopyOfContextMap()));
    }

}