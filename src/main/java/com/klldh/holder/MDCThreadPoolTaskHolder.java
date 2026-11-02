package com.klldh.holder;

import cn.hutool.core.lang.Opt;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

public class MDCThreadPoolTaskHolder {

    public static Runnable wrap(Runnable task, Map<String, String> context) {
        return () -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            Opt.ofNullable(context).ifPresent(MDC::setContextMap);
            try {
                task.run();
            } finally {
                Opt.ofNullable(previous).ifPresentOrElse(MDC::setContextMap, MDC::clear);
            }
        };
    }

    public static <T> Callable<T> wrap(Callable<T> task, Map<String, String> context) {
        return () -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            Opt.ofNullable(context).ifPresent(MDC::setContextMap);
            try {
                return task.call();
            } finally {
                Opt.ofNullable(previous).ifPresentOrElse(MDC::setContextMap, MDC::clear);
            }
        };
    }

}
