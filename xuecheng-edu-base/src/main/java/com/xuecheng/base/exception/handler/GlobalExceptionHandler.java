package com.xuecheng.base.exception.handler;

import com.xuecheng.base.exception.OperationFailedException;
import com.xuecheng.base.exception.XueChengEduException;
import com.xuecheng.base.model.RestErrorResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname GlobalExceptionHandler
 * @Description 全局异常处理
 * @Created by Domenic
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 自定义异常
     * @param e {@link XueChengEduException}
     * @return {@link RestErrorResponse}
     */
    @ExceptionHandler(XueChengEduException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(XueChengEduException e) {
        log.error("系统异常, errorMsg={}", e.getErrorMessage(), e);
        return new RestErrorResponse(e.getErrorMessage());
    }

    /**
     * 参数校验异常
     * @param e {@link MethodArgumentNotValidException}
     * @return {@link RestErrorResponse}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 获取异常信息
        BindingResult bindingResult = e.getBindingResult();
        List<String> errors = new ArrayList<>();

        // 获取哪些字段校验失败
        bindingResult.getFieldErrors().forEach(field -> {
            // 获取校验失败的信息
            String defaultMessage = field.getDefaultMessage();
            // 拼接错误信息
            errors.add(defaultMessage);
        });

        String errMessage = StringUtils.join(errors, ", ");
        log.error("方法参数异常, errorMsg={}", e.getMessage());
        return new RestErrorResponse(errMessage);
    }

    /**
     * 操作失败异常
     * @param e {@link OperationFailedException}
     * @return {@link RestErrorResponse}
     */
    @ExceptionHandler(OperationFailedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public RestErrorResponse operationFailedException(OperationFailedException e) {
        return new RestErrorResponse(e.getMessage());
    }

    /**
     * HTTP 请求方法不支持异常
     * @param e {@link HttpRequestMethodNotSupportedException}
     * @return {@link RestErrorResponse}
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public RestErrorResponse httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return new RestErrorResponse(e.getMessage());
    }

    /**
     * 其他异常
     * @param e {@link Exception}
     * @return {@link RestErrorResponse}
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {
        log.error("未知异常, errorMsg={}", e.getMessage(), e);
        return new RestErrorResponse(e.getMessage());
    }

}
