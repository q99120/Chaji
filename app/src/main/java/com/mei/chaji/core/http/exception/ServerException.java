package com.mei.chaji.core.http.exception;

import com.mei.chaji.core.http.base.ErrorCode;

/**
 * @author 服务器返回的异常
 * @date 2017/11/27
 */

public class ServerException extends  RuntimeException {

    private int errorCode;

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, int errCode) {
        super(ErrorCode.getErrorMessage(errCode), new Throwable(message));
        this.errorCode = errCode;
    }

    public int getCode() {
        return errorCode;
    }

    public void setCode(int code) {
        this.errorCode = code;
    }

}
