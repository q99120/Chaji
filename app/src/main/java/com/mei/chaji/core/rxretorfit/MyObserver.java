package com.mei.chaji.core.rxretorfit;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.mei.chaji.core.http.exception.NoDataExceptionException;
import com.mei.chaji.core.http.exception.ServerException;
import com.vondear.rxtool.view.RxToast;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * 重写Observer
 *
 * @param <T>
 */
public abstract class MyObserver<T> implements Observer<T> {
    private static final String TAG = "MyObserver";
    private Context context;

    @Override
    public void onSubscribe(Disposable d) {
        Log.d(TAG, "onSubscribe: ");
    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete: ");
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onError: "+ e.toString());
//        RxToast.warning(e.toString());
        if (e instanceof HttpException){
         onException(ExceptionReason.BAD_NETWORK);
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {   //   连接错误
            onException(ExceptionReason.CONNECT_ERROR);
        } else if (e instanceof InterruptedIOException) {   //  连接超时
            onException(ExceptionReason.CONNECT_TIMEOUT);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {   //  解析错误
            onException(ExceptionReason.PARSE_ERROR);
        }else if(e instanceof ServerException){
            onFail(e.getMessage());
        }else if (e instanceof NoDataExceptionException){
            onSuccess(null);
        } else {
            onException(ExceptionReason.UNKNOWN_ERROR);
        }
//        onFinish();
    }

    /**
     * 请求成功
     *
     * @param response 服务器返回的数据
     */
    abstract public void onSuccess(T response);

    /**
     * 服务器返回数据，但响应码不为1000
     */
    public void onFail(String message) {
        RxToast.normal(message);
    }

    /**
     * 请求异常
     *
     * @param reason
     */
    public void onException(ExceptionReason reason) {
        switch (reason) {
            case CONNECT_ERROR:
                RxToast.normal("网络连接错误");
                break;

            case CONNECT_TIMEOUT:
                RxToast.normal("网络连接超时");
                break;

            case BAD_NETWORK:
                RxToast.normal("网络似乎出了点小问题...");
                break;

            case PARSE_ERROR:
                RxToast.normal("解析数据失败");
                break;

            case UNKNOWN_ERROR:
            default:
                RxToast.normal("未知错误");
                break;
        }
    }

    /**
     * 请求网络失败原因
     */
    public enum ExceptionReason {
        /**
         * 解析数据失败
         */
        PARSE_ERROR,
        /**
         * 网络问题
         */
        BAD_NETWORK,
        /**
         * 连接错误
         */
        CONNECT_ERROR,
        /**
         * 连接超时
         */
        CONNECT_TIMEOUT,
        /**
         * 未知错误
         */
        UNKNOWN_ERROR,
    }
}
