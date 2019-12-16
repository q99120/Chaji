package com.mei.chaji.ui.main.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.zoloz.smile2pay.service.Zoloz;
import com.alipay.zoloz.smile2pay.service.ZolozCallback;
import com.google.gson.Gson;
import com.mei.chaji.R;
import com.mei.chaji.app.Constants;
import com.mei.chaji.base.activity.BaseActivity;
import com.mei.chaji.component.ActivityController;
import com.mei.chaji.core.http.api.RetrofitServiceManager;
import com.mei.chaji.core.rxretorfit.MyObserver;
import com.mei.chaji.instruction.MainCommunicate;
import com.mei.chaji.utils.ECBAESUtils;
import com.mei.chaji.utils.LogsFileUtil;
import com.mei.chaji.utils.TTSUtils;
import com.vondear.rxtool.view.RxToast;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class FacePayActivity extends BaseActivity {
    Gson gson;
    private static final String TAG = "smiletopay";
    Button mSmilePayButton;

    public static final String KEY_INIT_RESP_NAME = "zim.init.resp";
    private Zoloz zoloz;

    // 值为"1000"调用成功
    // 值为"1003"用户选择退出
    // 值为"1004"超时
    // 值为"1005"用户选用其他支付方式
    static final String CODE_SUCCESS = "1000";
    static final String CODE_EXIT = "1003";
    static final String CODE_TIMEOUT = "1004";
    static final String CODE_OTHER_PAY = "1005";

    static final String TXT_EXIT = "已退出刷脸支付";
    static final String TXT_TIMEOUT = "操作超时";
    static final String TXT_OTHER_PAY = "已退出刷脸支付";
    static final String TXT_OTHER = "抱歉未支付成功";

    //刷脸支付相关
    static final String SMILEPAY_CODE_SUCCESS = "10000";
    static final String SMILEPAY_SUBCODE_LIMIT = "ACQ.PRODUCT_AMOUNT_LIMIT_ERROR";
    static final String SMILEPAY_SUBCODE_BALANCE_NOT_ENOUGH = "ACQ.BUYER_BALANCE_NOT_ENOUGH";
    static final String SMILEPAY_SUBCODE_BANKCARD_BALANCE_NOT_ENOUGH = "ACQ.BUYER_BANKCARD_BALANCE_NOT_ENOUGH";

    static final String SMILEPAY_TXT_LIMIT = "刷脸支付超出限额，请选用其他支付方式";
    static final String SMILEPAY_TXT_EBALANCE_NOT_ENOUGH = "账户余额不足，支付失败";
    static final String SMILEPAY_TXT_BANKCARD_BALANCE_NOT_ENOUGH = "账户余额不足，支付失败";
    static final String SMILEPAY_TXT_FAIL = "抱歉未支付成功，请重新支付";
    static final String SMILEPAY_TXT_SUCCESS = "刷脸支付成功";
    String rsa_public_key;
    String ali_url;

    boolean rufund_result = true;

    //这里三个值请填写自己真实的值
    //应用的签名私钥
    public static String appKey;
    //商户id
    public static String partnerId;
    //应用的appId
    public static String appId;

    String goods_id, name, price;
    int ch, temperature_type, cup_num;
    String encode_name, order_no;
    String apdidToken, appName, appVersion, bioMetaInfo;
    int face_type_flag;

    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();


    int bit0, bit1, bit2, bit3, bit4, bit5, bit6, bit7, bit8, bit9, bit10, bit11, bit12, bit13, bit14, bit15, bit16, bit17,
            bit18, bit19, bit20, bit21;
    @BindView(R.id.back)
    Button back;
    @BindView(R.id.pre_qidong)
    TextView pre_qidong;
    @BindView(R.id.finish_qidong)
    TextView finish_qidong;


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

            }
        }
    };

    @Override
    protected void initUI() {
        //在这之前先zolozInstall初始化，该初始化务必放在app启动的时候，见MYApplication否则影响人脸的正常使用
        zoloz = com.alipay.zoloz.smile2pay.service.Zoloz.getInstance(getApplicationContext());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_facepay;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initEventAndData() {
        ActivityController.getInstance().addActivity(this);
        /**
         * 获取activiy传值的数据
         */
        Intent i = getIntent();
        Bundle bundle = i.getBundleExtra("bundle");//得到从Activity传来的数据
        if (bundle != null) {
            goods_id = bundle.getString("goods_id");
            ch = bundle.getInt("ch_type");
            temperature_type = bundle.getInt("temperature_type");
            cup_num = bundle.getInt("cup_num");
            encode_name = bundle.getString("goods_name");
            price = bundle.getString("cup_price");
            order_no = bundle.getString("order_no");
            face_type_flag = bundle.getInt("face_type_flag");
            Log.e(TAG, "initEventAndData: " + face_type_flag);
        }

        //初始化获取支付宝网关地址和appid
        gson = new Gson();
        Map<String, String> map = new HashMap<>();
        map.put("appId", "APP0000001");
        map.put("secret", "chaji20190505");
        String s_body = ECBAESUtils.encrypt(Constants.AES_KEY, gson.toJson(map));
        cachedThreadPool.execute(() -> RetrofitServiceManager.getAPIService().getaliPayConfig(s_body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>() {
                    @Override
                    public void onSuccess(String response) {
                        String success_json = ECBAESUtils.decrypt(Constants.AES_KEY, response);
                        //第一步：先获取jsonobject对象
                        com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(success_json);
                        //第二步：把对象转换成jsonArray数组
                        appKey = jsonObject1.getString("rsa_private_key");
                        appId = jsonObject1.getString("appid");
                        rsa_public_key = jsonObject1.getString("alipay_public_key");
                        partnerId = jsonObject1.getString("pid");
                        ali_url = jsonObject1.getString("url");
//                        Log.e(TAG, "onSuccess: " + appKey + appId);
//                        RxToast.normal(appKey+"爱噗噗ID"+appId);
                        SmilePay();
                    }

                    @Override
                    public void onException(ExceptionReason reason) {
                        super.onException(reason);
                        Log.e(TAG, "onException: " + reason.toString());
                        if (reason.toString().equals("CONNECT_ERROR")) {
                            RxToast.normal("网络连接失败,将在2秒后跳转..");
                            mHandler.postDelayed(gopacrun, 2000);
                        }
                    }
                }));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gopac();
            }
        });
    }

    private Runnable gopacrun = () -> gopac();

    /**
     * 获取刷脸所需的设备信息
     */
    private void SmilePay() {
        //调用接口zolozGetMetaInfo采集刷脸所需的设备信息并完成刷脸的准备工作
        cachedThreadPool.execute(() -> zoloz.zolozGetMetaInfo(mockInfo(), new ZolozCallback() {
            @Override
            // 解析zolozGetMetaInfo返回的结果，如果成功，则请求商户服务端调用人脸初始化接口
            public void response(Map smileToPayResponse) {
//                if (smileToPayResponse == null) {
//                    Log.e(TAG, "response is null");
//                    promptText(TXT_OTHER);
//                    return;
//                } else {
                if (smileToPayResponse != null) {
                    String code = (String) smileToPayResponse.get("code");
                    String metaInfo = (String) smileToPayResponse.get("metainfo");
                    Log.e(TAG, "response: " + metaInfo + "状态吗" + code);
                    if (metaInfo != null) {
                        Log.e(TAG, "response: " + metaInfo + "状态吗" + code);
//                        textView.setText(metaInfo);
                        Gson gson = new Gson();
                        com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(metaInfo);
                        //第二步：把对象转换成jsonArray数组
                        apdidToken = jsonObject1.getString("apdidToken");
                        appName = jsonObject1.getString("appName");
                        appVersion = jsonObject1.getString("appVersion");
                        bioMetaInfo = jsonObject1.getString("bioMetaInfo");
                        //获取metainfo成功
                        if (CODE_SUCCESS.equalsIgnoreCase(code)) {
                            Log.e(TAG, "metanfo is:" + metaInfo);
                            Map<String, String> map1 = new HashMap<>();
                            Map<String, Object> map = new HashMap<>();
                            map.put("appId", "APP0000001");
                            map.put("secret", "chaji20190505");
                            map.put("deviceNo", deviceNo);
                            Log.e(TAG, "超好吃超好吃: " + deviceNo);
                            map.put("goodsId", goods_id);
                            map.put("goodsName", encode_name);
                            map.put("orderAmount", price);
                            map.put("waterTemperature", String.valueOf(temperature_type));
                            map.put("gargoWay", String.valueOf(ch));
                            map.put("cupNumber", String.valueOf(cup_num));
                            map.put("orderNo", order_no);
                            map.put("type", face_type_flag);
                            Log.e(TAG, "初始化设备: " + order_no);

//                            map1.put("apdidToken", apdidToken);
//                            map1.put("appName", appName);
//                            map1.put("appVersion", appVersion);
//                            map1.put("bioMetaInfo", bioMetaInfo);
                            map.put("zimmetainfo", metaInfo);

                            Log.e("map", "initListener: " + map.toString());
                            String jsonbody = gson.toJson(map);
//                            textView1.setText("加密前的数据"+jsonbody);
                            Log.e("json", "initListener: " + jsonbody);
                            String aa = ECBAESUtils.encrypt(Constants.AES_KEY, jsonbody);
                            Log.e(TAG, "发送的json" + jsonbody);
                            Log.e(TAG, "加密数据" + aa);
                            RetrofitServiceManager.getAPIService().postAliFicePayInit(aa).
                                    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new MyObserver<String>() {
                                        @Override
                                        public void onSuccess(String response) {
//                                        RxToast.normal("请求数据成功");
                                            Log.e(TAG, "onSuccess: " + "请求数据成功");
//                                            Log.e("===========", "onSuccess: " + response);
                                            String derespone = ECBAESUtils.decrypt(Constants.AES_KEY, response);
                                            Log.e("解密成功后的数据", "onSuccess: " + "解密成功后的数据" + derespone);
                                            if (derespone != null) {
                                                com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(derespone);
                                                //第二步：把对象转换成jsonArray数组
                                                String zimId = jsonObject1.getString("zimId");
                                                String zimInitClientData = jsonObject1.getString("zimInitClientData");
                                                Log.e(TAG, "支付宝属性: " + zimId + "协议" + zimInitClientData);
                                                smile(zimId, zimInitClientData);
                                            }
                                        }
                                    });
                        } else {
                            promptText(TXT_OTHER);
                        }
                    }
                }
            }
//            }
        }));
    }


    /**
     * 发起刷脸支付请求.
     *
     * @param zimId    刷脸付token，从服务端获取，不要mock传入
     * @param protocal 刷脸付协议，从服务端获取，不要mock传入
     */
    private void smile(String zimId, String protocal) {
        pre_qidong.setVisibility(View.INVISIBLE);
        Map params = new HashMap();
        params.put(KEY_INIT_RESP_NAME, protocal);
        //通过这个方法调用人脸识别
        zoloz.zolozVerify(zimId, params, smileToPayResponse -> {
            if (smileToPayResponse == null) {
                promptText(TXT_OTHER);
                return;
            }

            String code = (String) smileToPayResponse.get("code");
            String fToken = (String) smileToPayResponse.get("ftoken");
            String subCode = (String) smileToPayResponse.get("subCode");
            String msg = (String) smileToPayResponse.get("msg");
            Log.d(TAG, "ftoken is:" + fToken);

            //刷脸成功
            if (CODE_SUCCESS.equalsIgnoreCase(code) && fToken != null) {
                //promptText("刷脸成功，返回ftoken为:" + fToken);
                //这里在Main线程，网络等耗时请求请放在异步线程中
                //后续这里可以发起支付请求
                //https://docs.open.alipay.com/api_1/alipay.trade.pay
                //需要修改两个参数
                //scene固定为security_code
                //auth_code为这里获取到的fToken值
                //支付一分钱，支付需要在服务端发起，这里只是模拟
                try {
                    if (face_type_flag == 1) {
                        pay(fToken);
                    } else {
                        refill(fToken);
                    }

                } catch (Exception e) {
                    promptText(SMILEPAY_TXT_FAIL);
                }
            } else if (CODE_EXIT.equalsIgnoreCase(code)) {
                promptText(TXT_EXIT);
                gopac();
            } else if (CODE_TIMEOUT.equalsIgnoreCase(code)) {
                promptText(TXT_TIMEOUT);
                gopac();
            } else if (CODE_OTHER_PAY.equalsIgnoreCase(code)) {
                promptText(TXT_OTHER_PAY);
                gopac();
            } else {
                String txt = TXT_OTHER;
                if (!TextUtils.isEmpty(subCode)) {
                    txt = txt + "(" + subCode + ")";
                }
                promptText(txt);
                gopac();
            }
        });
    }

    private void refill(String fToken) {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("appId", "APP0000001");
        map1.put("secret", "chaji20190505");
        map1.put("ftoken", fToken);
        map1.put("type", "2");
        String res_pay1 = ECBAESUtils.encrypt(Constants.AES_KEY, gson.toJson(map1));
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                RetrofitServiceManager.getAPIService().ficeRefillTea(res_pay1)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MyObserver<String>() {
                            @Override
                            public void onSuccess(String response) {
                                Log.e(TAG, "onSuccess: " + "收单数据成功");
                                Log.e(TAG, "收单数据解密" + ECBAESUtils.decrypt(Constants.AES_KEY, response));
                                //第一步：先获取jsonobject对象
                                com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(ECBAESUtils.decrypt(Constants.AES_KEY, response));
                                //第二步：把对象转换成jsonArray数组
                                boolean result = jsonObject1.getBoolean("result");
                                String msg = jsonObject1.getString("msg");
                                if (result) {
                                    RxToast.normal("续杯成功");
                                    TTSUtils.getInstance().speak("续杯成功");
                                    finish_qidong.setVisibility(View.VISIBLE);
                                    MainCommunicate.getInstance().normal_refill();
                                    Intent i = new Intent(FacePayActivity.this, RefillTeaActivity.class);
                                    startActivity(i);
                                } else {
                                    RxToast.normal(msg);
                                    TTSUtils.getInstance().speak(msg);
                                    Log.e(TAG, "onSuccess: " + "今日暂未交易");
                                    gopac();
                                }

                            }

                            @Override
                            public void onException(ExceptionReason reason) {
                                super.onException(reason);
                                Log.e(TAG, "onException: " + reason.toString());
                                if (reason.toString().equals("CONNECT_ERROR")) {
                                    RxToast.normal("网络连接失败,将在2秒后跳转..");
                                    mHandler.postDelayed(gopacrun, 2000);
                                }
                            }
                        });
            }
        });
    }


    /**
     * 发起刷脸支付请求.
     *
     * @param txt toast文案
     */
    void promptText(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 发起刷脸支付请求.
     *
     * @param ftoken 刷脸返回的token
     */
    private void pay(String ftoken) throws Exception {
        Log.e(TAG, "人脸支付类型: " + face_type_flag + order_no);
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("appId", "APP0000001");
        map.put("secret", "chaji20190505");
        map.put("orderNo", order_no);
        map.put("goodsName", encode_name);
        map.put("type", "1");
        map.put("ftoken", ftoken);
        String res_pay = ECBAESUtils.encrypt(Constants.AES_KEY, gson.toJson(map));
        cachedThreadPool.execute(() -> RetrofitServiceManager.getAPIService().aliFicePay(res_pay)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "onSuccess: " + "收单数据成功");
                        Log.e(TAG, "收单数据解密" + ECBAESUtils.decrypt(Constants.AES_KEY, response));
                        //第一步：先获取jsonobject对象
                        com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(ECBAESUtils.decrypt(Constants.AES_KEY, response));
                        //第二步：把对象转换成jsonArray数组
                        boolean result = jsonObject1.getBoolean("result");
                        String orderAmount = jsonObject1.getString("orderAmount");
                        String preAmount = jsonObject1.getString("preAmount");
                        if (result) {
                            TTSUtils.getInstance().speak("支付成功,即将制茶请耐心等待..");
                            finish_qidong.setVisibility(View.VISIBLE);
                            RxToast.normal("支付成功");
                            LogsFileUtil.getInstance().addLog("人脸支付成功","订单号"+order_no);
                            Log.e(TAG, "onSuccess: " + "走了这个支付成功" + temperature_type + "通道" + ch);
                            MainCommunicate.getInstance().normal_water(ch);
                            Intent i = new Intent(FacePayActivity.this, MakeTeaActivity.class);
                            i.putExtra("order_no", order_no);
                            i.putExtra("orderAmount", orderAmount);
                            i.putExtra("preAmount", preAmount);
                            startActivity(i);
                        } else {
                            LogsFileUtil.getInstance().addLog("人脸支付失败","订单号"+order_no);
                            TTSUtils.getInstance().speak("支付失败,请重新下单支付");
                            RxToast.normal("人脸支付失败,请重新下单支付");
                            gopac();
                        }

                    }

                    @Override
                    public void onException(ExceptionReason reason) {
                        super.onException(reason);
                        Log.e(TAG, "onException: " + reason.toString());
                        if (reason.toString().equals("CONNECT_ERROR")) {
                            RxToast.normal("网络连接失败,将在2秒后跳转..");
                            mHandler.postDelayed(gopacrun, 2000);
                        }
                    }
                }));
    }

    private void gopac() {
        Intent i = new Intent(FacePayActivity.this, BuyGoodsActivity.class);
        startActivity(i);
    }

    /**
     * 预留退款信息
     * /**
     * mock数据，真实商户请填写真实信息.
     */
    private Map mockInfo() {
        Map merchantInfo = new HashMap();
        //以下信息请根据真实情况填写
        //商户 Pid
        merchantInfo.put("merchantId", partnerId);
        //ISV PID
        merchantInfo.put("partnerId", partnerId);
        //添加刷脸付功能的appid
        merchantInfo.put("appId", appId);
        //机具编号，便于关联商家管理的机具
        merchantInfo.put("deviceNum", deviceNo);
        //品牌标识符(api无此参数),与支付宝约定固定参数用于支付宝日志排查
        merchantInfo.put("brandCode", "xiaochashen_tmomp");
        //商户的门店编号
//        merchantInfo.put("storeCode", "TEST");
        //支付宝门店编号
//        merchantInfo.put("alipayStoreCode", "TEST");

        return merchantInfo;
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        TTSUtils.getInstance().pauseSpeech();
        Log.e(TAG, "onPause: " + "人脸支付生命周期结束");
        super.onPause();
    }
}
