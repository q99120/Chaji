package com.mei.chaji.core.http.exception;

/**
 * 服务器没有数据返回的异常
 */
public class NoDataExceptionException extends RuntimeException {
    public NoDataExceptionException() {
        super("服务器没有返回对应的Data数据", new Throwable("Server error"));
    }
}
