package com.mei.chaji.core.http.api;

import com.mei.chaji.core.bean.main.UserData;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author jyx
 * @data 2019/4/19
 */

public interface ChajiApis {
    String HOST = "http://baqsng.natapp1.cc/tmomp/";

    /**
     * 获取所有设备列表
     */
    @POST("tmomp/rest/advertise/advertiseService/android/findDeviceAllAdList")
    Observable<UserData> getAllDevice(@Body RequestBody body);

    /**
     * 测试
     */
    @POST("tmomp/rest/advertise/advertiseService/android/findDeviceAllAdList")
    Observable<String> getAllAdString(@Body String body);

    /**
     * 商品
     */
    @POST("tmomp/rest/goods/goodsService/android/findDeviceGoodsInfo")
    Observable<String> getAllGoodService(@Body String body);

    /**
     * 免费茶机
     */
    @POST("tmomp/rest/pay/payService/android/zeroRMBPay")
    Observable<String> zeroRMBpay(@Body String body);


    @GET("tmomp/rest/pay/payService/android/generateAliPayQRCode")
    Observable<ResponseBody> getAliPayQRCode1(@Query("goodsId") String goodsId, @Query("goodsName") String goodsName, @Query("deviceNo") String deviceNo,
                                              @Query("orderAmount") String orderAmount, @Query("orderNo") String orderNo, @Query("paymentType") String paymentType
            , @Query("waterTemperature") int waterTemperature, @Query("gargoWay") int gargoWay, @Query("cupNumber") int cupNumber);


    /**
     * 获取支付宝appid和key
     */

    /**
     * 人脸支付获取appid等信息
     */
    @POST("tmomp/rest/pay/payService/android/getaliPayConfig")
    Observable<String> getaliPayConfig(@Body String body);

    /**
     * 人脸支付初始化设备
     */
    @POST("tmomp/rest/pay/payService/android/aliFicePayInit")
    Observable<String> postAliFicePayInit(@Body String body);

    /**
     * 人脸支付收单请求
     */
    @POST("tmomp/rest/pay/payService/android/aliFicePay")
    Observable<String> aliFicePay(@Body String body);

    /**
     * 人脸支付续杯请求
     */
    @POST("tmomp/rest/pay/payService/android/ficeRefillTea")
    Observable<String> ficeRefillTea(@Body String body);

    /**
     * 微信支付宝续杯二维码
     */
    @GET("tmomp/rest/pay/payService/android/generateRefillQRCode")
    Observable<ResponseBody> generateRefillQRCode(@Query("deviceNo") String deviceNo, @Query("paymentType") String paymentType);

    /***
     * 商品展示二维码
     */
    @POST("tmomp/rest/shop/shoppingService/android/getDeviceShopQRCode")
    Observable<String> getGoodQRCode(@Body String body);


    /**
     * 后台运维补货
     */
    @POST("tmomp/rest/device/deviceService/android/updateDeviceGoodsCount")
    Observable<String> UpdateDeviceGC(@Body String body);


    /**
     * 版本升级状态返回
     */
    @POST("tmomp/rest/upVersion/updateVersionService/android/alterUpVersion")
    Observable<String> alertUpVersion(@Body String body);

    /**
     * 退款
     */
    @POST("tmomp/rest/pay/payService/android/alipayRefund")
    Observable<String> alipayRefund(@Body String body);

    /**
     * 故障提交
     */
    @POST("tmomp/rest/device/deviceService/android/addDeviceState")
    Observable<String> addDeviceState(@Body String body);

    /**
     * 补货微信提醒
     */
    @POST("tmomp/rest/goods/goodsService/android/replenishGoodsInform")
    Observable<String> replenishGoodsInform(@Body String body);


    /**
     * 运维人员登陆接口
     */
    @POST("tmomp/rest/device/deviceService/android/deviceUserLogin")
    Observable<String> deviceUserLogin(@Body String body);

    /**
     * 获取运维人员登陆二维码
     */
    @GET("tmomp/rest/device/deviceService/android/generateWxLoginQRCode")
    Observable<ResponseBody> generateWxLoginQRCode(@Query("deviceNo") String deviceNo);


    /**
     * 后台运维设备基本参数查询
     */
    @POST("tmomp/rest/device/deviceService/android/findtDevicePreferences")
    Observable<String> findtDevicePreferences(@Body String body);

    /**
     * 后台运维设备基本参数设置
     */
    @POST("tmomp/rest/device/deviceService/android/updateDevicePreferences")
    Observable<String> updateDevicePreferences(@Body String body);


    /**
     * 后台运维设备详情查询
     */
    @POST("tmomp/rest/device/deviceService/android/showDeviceInfo")
    Observable<String> showDeviceInfo(@Body String body);


    /**
     * 补货界面默认值查询
     */
    @POST("tmomp/rest/device/deviceService/android/findDeviceGoodsCount")
    Observable<String> findDeviceGoodsCount(@Body String body);

    /**
     * 进入清洁模式
     */
    @POST("tmomp/rest/device/deviceService/android/cleaningDevice")
    Observable<String> cleaningDevice(@Body String body);

    /**
     * 设备激活
     */
    @POST("tmomp/rest/device/deviceService/android/updateDeviceActivation")
    Observable<String> updateDeviceActivation(@Body String body);


    /**
     * 设备激活
     */
    @POST("tmomp/rest/device/deviceService/android/findClearGoodsList")
    Observable<String> findClearGoodsList(@Body String body);


    /**
     * 设备绑定商品
     */
    @POST("tmomp/rest/device/deviceService/android/bindDeviceGoods")
    Observable<String> bindDeviceGoods(@Body String body);

    /**
     * 设备是否在线
     */
    @POST("tmomp/rest/device/deviceService/android/deviceOnlineStatement")
    Observable<String> deviceOnlineStatement(@Body String body);

    /**
     * 设备取消激活
     */
    @POST("tmomp/rest/device/deviceService/android/updateDeviceDeactivate")
    Observable<String> updateDeviceDeactivate(@Body String body);

    /**
     * 设备初始化时候获取当前版本是否需要升级
     */
    @POST("tmomp/rest/device/deviceService/android/findDeviceVersionInfo")
    Observable<String> findDeviceVersionInfo(@Body String body);

    /**
     * 设备初始化时候获取当前版本是否需要升级
     */
    @POST("tmomp/rest/pay/payService/android/paySucc")
    Observable<String> payService(@Body String body);
}
