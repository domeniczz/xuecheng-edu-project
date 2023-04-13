package com.xuecheng.base.exception;

import java.io.Serializable;

/**
 * @author Domenic
 * @Classname RestErrorResponse
 * @Description REST API 异常响应
 * @Date 4/13/2023 4:03 PM
 * @Created by Domenic
 */
public class RestErrorResponse implements Serializable {

    public static final long serialVersionUID = 1L;

    private String errMessage;

    public RestErrorResponse(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

}
