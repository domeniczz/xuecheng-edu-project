package com.xuecheng.base.exception;

/**
 * @author Domenic
 * @Classname XueChengEduException
 * @Description 项目自定义异常类
 * @Created by Domenic
 */
public class XueChengEduException extends RuntimeException {

    public XueChengEduException(String errorMessage) {
        super(errorMessage);
    }

    public String getErrorMessage() {
        return getMessage();
    }

    /**
     * 抛出指定错误信息的异常
     * @param message 错误信息
     */
    public static void cast(String errorMessage) {
        throw new XueChengEduException(errorMessage);
    }

    /**
     * 抛出通用的异常
     * @param error 通用异常枚举
     */
    public static void cast(CommonException error) {
        throw new XueChengEduException(error.getErrMessage());
    }

}