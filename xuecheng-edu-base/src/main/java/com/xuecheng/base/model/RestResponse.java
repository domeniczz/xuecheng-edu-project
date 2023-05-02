package com.xuecheng.base.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author Domenic
 * @Classname RestResponse
 * @Description Rest 响应信息封装对象
 * @Created by Domenic
 */
@Data
@ToString
public class RestResponse<T> {

    /**
     * <p>
     * 响应编码<br/>
     * 0 表示成功，-1 表示错误
     * </p>
     */
    private int code;

    /**
     * 响应提示信息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 默认构造成功的响应信息
     */
    public RestResponse() {
        this(0, "success");
    }

    public RestResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 错误的响应
     * @param msg 响应信息
     * @return RestResponse
     */
    public static <T> RestResponse<T> fail(String msg) {
        RestResponse<T> resp = new RestResponse<T>();
        resp.setCode(-1);
        resp.setMsg(msg);
        return resp;
    }

    /**
     * 错误响应信息
     * @param data 响应内容 (泛型)
     * @param msg 响应信息
     * @return RestResponse
     */
    public static <T> RestResponse<T> fail(T data, String msg) {
        RestResponse<T> resp = new RestResponse<T>();
        resp.setCode(-1);
        resp.setMsg(msg);
        resp.setData(data);
        return resp;
    }

    /**
     * 成功的响应 (包含响应数据)
     * @param data 响应内容
     * @return RestResponse
     */
    public static <T> RestResponse<T> success(T data) {
        RestResponse<T> resp = new RestResponse<T>();
        resp.setData(data);
        return resp;
    }

    /**
     * 成功的响应 (包含响应数据)
     * @param data 响应内容
     * @param msg 响应信息
     * @return RestResponse
     */
    public static <T> RestResponse<T> success(T data, String msg) {
        RestResponse<T> resp = new RestResponse<T>();
        resp.setMsg(msg);
        resp.setData(data);
        return resp;
    }

    /**
     * 成功的响应 (不包含响应内容)
     * @return RestResponse
     */
    public static <T> RestResponse<T> success() {
        return new RestResponse<T>();
    }

    /**
     * 成功的响应 (不包含响应数据)
     * @param msg 响应信息
     * @return RestResponse
     */
    public static <T> RestResponse<T> success(String msg) {
        RestResponse<T> resp = new RestResponse<T>();
        resp.setMsg(msg);
        return resp;
    }

    /**
     * 判断是否成功
     * @return true 为成功，false 为错误
     */
    public boolean isSuccess() {
        return this.code == 0;
    }

}