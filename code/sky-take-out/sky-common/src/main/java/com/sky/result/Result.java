package com.sky.result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T>
 */
@ApiModel(description = "同意返回的数据的模型")
@Data
public class Result<T> implements Serializable {

    @ApiModelProperty("成功失败的代号")
    private Integer code; //编码：1成功，0和其它数字为失败

    @ApiModelProperty("简短描述")
    private String msg; //错误信息

    @ApiModelProperty("返回的数据")
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 1;
        result.msg = "操作成功";
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 1;
        result.msg = "操作成功";
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 0;
        return result;
    }

}
