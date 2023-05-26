package com.xuecheng.base.exception;

/**
 * @author Domenic
 * @Classname CommonError
 * @Description 通用异常枚举类
 * @Created by Domenic
 */
public enum CommonException {

    /**
     * 未知错误
     */
    UNKOWN_ERROR("Unknown Execution Error, Please Try Again"),
    /**
     * 非法传入参数
     */
    PARAMS_ERROR("Illegal Params"),
    /**
     * 对象为空
     */
    OBJECT_NULL("Object is Empty"),
    /**
     * 未找到对应的数据
     */
    QUERY_NULL("Query Result is Empty"),
    /**
     * 请求参数为空
     */
    REQUEST_NULL("Request Param is Empty");

    private final String errMessage;

    public String getErrMessage() {
        return errMessage;
    }

    private CommonException(String errMessage) {
        this.errMessage = errMessage;
    }

}