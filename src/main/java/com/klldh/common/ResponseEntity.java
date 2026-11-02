package com.klldh.common;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

import static com.klldh.common.Constant.FAIL;
import static com.klldh.common.Constant.SUCCESS;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ResponseEntity<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -5100053890711879957L;

    /**
     * 结果
     */
    private Integer result;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 描述
     */
    private String description;

    // ------------------------------------------------------------------------------------------------------------------------

    /**
     * 正常返回
     *
     * @param data 返回数据
     * @return {@link ResponseEntity}<{@link T}> 响应
     */
    public static <T> ResponseEntity<T> success(T data) {
        return of(SUCCESS, data, null);
    }

    /**
     * 返回错误对象
     *
     * @param errorMessage 错误提示语
     * @param params       错误提示语填充参数
     * @return {@link ResponseEntity}<{@link T}> 错误对象
     */
    public static <T> ResponseEntity<T> error(String errorMessage, Object... params) {
        return of(FAIL, null, StrUtil.format(errorMessage, params));
    }

}