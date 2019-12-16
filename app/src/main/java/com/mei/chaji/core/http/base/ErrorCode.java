package com.mei.chaji.core.http.base;

import com.vondear.rxtool.view.RxToast;

/**
 * 错误码
 */
public class ErrorCode {

    /**
     * request success
     */
    public static final int SUCCESS = 1000;


    public static String getErrorMessage(int errorCode) {
        String message = null;
        if (errorCode == SUCCESS) {
            message = "请求数据成功";
        }
        return message;
    }
}
