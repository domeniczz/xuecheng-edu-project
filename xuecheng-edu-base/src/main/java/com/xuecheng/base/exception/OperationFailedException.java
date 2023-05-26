package com.xuecheng.base.exception;

/**
 * @author Domenic
 * @Classname OperationFailedException
 * @Description 操作失败异常
 * @Created by Domenic
 */
public class OperationFailedException extends RuntimeException {

    public OperationFailedException(String taskName) {
        super(taskName + " Operation Failed!");
    }

    public OperationFailedException(String taskName, Throwable cause) {
        super(taskName + " Operation Failed!", cause);
    }

    public String getErrMessage() {
        return getMessage() + " Cause: " + getCause().getMessage();
    }

}
