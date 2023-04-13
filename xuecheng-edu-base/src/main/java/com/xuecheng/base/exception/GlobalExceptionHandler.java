package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Domenic
 * @Classname GlobalExceptionHandler
 * @Description 全局异常处理
 * @Date 4/13/2023 4:13 PM
 * @Created by Domenic
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 自定义异常
     * @param e XueChengEduException
     * @return RestErrorResponse
     */
    @ExceptionHandler(XueChengEduException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(XueChengEduException e) {
        log.error("系统异常{}", e.getErrMessage(), e);
        return new RestErrorResponse(e.getErrMessage());
    }

    /**
     * 其他异常
     * @param e Exception
     * @return RestErrorResponse
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {
        log.error("系统异常{}", e.getMessage(), e);
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }

}
