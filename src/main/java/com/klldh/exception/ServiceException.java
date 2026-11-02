package com.klldh.exception;

import cn.hutool.core.util.StrUtil;

import java.io.Serial;

public class ServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7796605938040926930L;

    /**
     * 构造ServiceException
     *
     * @param errorMessage 报错提示语
     * @param params       填充参数
     */
    public ServiceException(String errorMessage, Object... params) {
        super(StrUtil.format(errorMessage, params));
    }

}