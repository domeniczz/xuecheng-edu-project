package com.xuecheng.base.exception;

/**
 * @author Domenic
 * @Classname XueChengEduException
 * @Description 项目自定义异常类
 * @Created by Domenic
 */
public class XueChengEduException extends RuntimeException {

    public XueChengEduException(String errMessage) {
        super(errMessage);
    }

    public String getErrMessage() {
        return getMessage();
    }

    /**
     * 抛出指定错误信息的异常
     * @param message 错误信息
     */
    public static void cast(String message) {
        throw new XueChengEduException(message);
    }

    /**
     * 抛出通用的异常
     * @param error 通用异常枚举
     */
    public static void cast(CommonError error) {
        throw new XueChengEduException(error.getErrMessage());
    }

}