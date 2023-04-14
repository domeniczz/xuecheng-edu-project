package com.xuecheng.base.exception;

/**
 * @author Domenic
 * @Classname CommonError
 * @Description 通用异常枚举类
 * @Created by Domenic
 */
public enum CommonError {

    UNKOWN_ERROR("Unknown Execution Error, Please Try Again"),
    PARAMS_ERROR("Illegal Params"),
    OBJECT_NULL("Object is Empty"),
    QUERY_NULL("Query Result is Empty"),
    REQUEST_NULL("Request Param is Empty");

    private final String errMessage;

    public String getErrMessage() {
        return errMessage;
    }

    private CommonError(String errMessage) {
        this.errMessage = errMessage;
    }

}