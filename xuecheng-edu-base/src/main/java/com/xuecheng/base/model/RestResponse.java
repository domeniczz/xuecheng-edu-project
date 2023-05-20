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
    private T result;

    enum Code {
        /**
         * 成功
         */
        SUCCESS(0),
        /**
         * 失败
         */
        FAIL(-1);

        private int code;

        Code(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 默认构造成功的响应信息
     */
    public RestResponse() {
        this(Code.SUCCESS.getCode(), "success");
    }

    public RestResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 错误的响应
     * @param msg 响应信息
     * @return {@link RestResponse}
     */
    public static <T> RestResponse<T> fail(String msg) {
        RestResponse<T> resp = new RestResponse<>();
        resp.setCode(Code.FAIL.getCode());
        resp.setMsg(msg);
        return resp;
    }

    /**
     * 错误响应信息
     * @param result 响应内容 (泛型)
     * @param msg 响应信息
     * @return {@link RestResponse}
     */
    public static <T> RestResponse<T> fail(T result, String msg) {
        RestResponse<T> resp = new RestResponse<>();
        resp.setCode(Code.FAIL.getCode());
        resp.setMsg(msg);
        resp.setResult(result);
        return resp;
    }

    /**
     * 成功的响应 (包含响应数据)
     * @param result 响应内容
     * @return {@link RestResponse}
     */
    public static <T> RestResponse<T> success(T result) {
        RestResponse<T> resp = new RestResponse<>();
        resp.setCode(Code.SUCCESS.getCode());
        resp.setResult(result);
        return resp;
    }

    /**
     * 成功的响应 (包含响应数据)
     * @param result 响应内容
     * @param msg 响应信息
     * @return {@link RestResponse}
     */
    public static <T> RestResponse<T> success(T result, String msg) {
        RestResponse<T> resp = new RestResponse<>();
        resp.setCode(Code.SUCCESS.getCode());
        resp.setMsg(msg);
        resp.setResult(result);
        return resp;
    }

    /**
     * 成功的响应 (不包含响应内容)
     * @return {@link RestResponse}
     */
    public static <T> RestResponse<T> success() {
        RestResponse<T> resp = new RestResponse<>();
        resp.setCode(Code.SUCCESS.getCode());
        resp.setMsg("success");
        return resp;
    }

    /**
     * 成功的响应 (不包含响应数据)
     * @param msg 响应信息
     * @return {@link RestResponse}
     */
    public static <T> RestResponse<T> success(String msg) {
        RestResponse<T> resp = new RestResponse<>();
        resp.setCode(Code.SUCCESS.getCode());
        resp.setMsg(msg);
        return resp;
    }

    /**
     * 判断是否成功
     * @return {@code true} 为成功，{@code false} 为错误
     */
    public boolean isSuccess() {
        return this.code == Code.SUCCESS.getCode();
    }

}