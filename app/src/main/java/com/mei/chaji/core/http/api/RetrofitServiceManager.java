package com.mei.chaji.core.http.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mei.chaji.app.Contact;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitServiceManager {
    /**
     * 默认超时时间 单位/秒
     */
    private static final int DEFAULT_TIME_OUT = 30;
    private static OkHttpClient mOkHttpClient;
    private static Retrofit mRetrofit;
    private final Gson mGsonDateFormat;

    public RetrofitServiceManager(Gson mGsonDateFormat) {
        this.mGsonDateFormat = mGsonDateFormat;
        mGsonDateFormat = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();
    }

    public static ChajiApis getAPIService() {
        return getInstance().create(ChajiApis.class);
    }

    public static Retrofit getInstance() {
        if (mRetrofit == null) {
            synchronized (RetrofitServiceManager.class) {
                if (mRetrofit == null) {
//                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
                    mRetrofit = new Retrofit.Builder()
                            .baseUrl(Contact.p_url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(getsOKHttpClient())
                            .build();
                }
            }
        }

        return mRetrofit;
    }

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
            //打印retrofit日志
            Log.e("RetrofitLog", "retrofitBack ======================= " + message);
        }
    });


    //服务器超时
    public static OkHttpClient getsOKHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (RetrofitServiceManager.class) {

                if (mOkHttpClient == null) {
                    //默认重试一次，若需要多次重试，则需要设置拦截器
                    mOkHttpClient = new OkHttpClient.Builder().retryOnConnectionFailure(true)
                            .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                            .readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                            .writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                            .build();
                }
            }
        }

        return mOkHttpClient;
    }

    /**
     * 自定义的，重试N次的拦截器
     * 通过：addInterceptor 设置
     */
    public static class Retry implements Interceptor {
        public int maxRetry;//最大重试次数
        private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

        public Retry(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            Log.e("Retry", "num:" + retryNum);
            while (!response.isSuccessful() && retryNum < maxRetry) {
                retryNum++;
                Log.e("Retry", "num:" + retryNum);
                response = chain.proceed(request);
            }
            return response;
        }
    }

}
