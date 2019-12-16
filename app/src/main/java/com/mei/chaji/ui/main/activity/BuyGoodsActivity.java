package com.mei.chaji.ui.main.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.NetworkUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.mei.chaji.R;
import com.mei.chaji.app.ChajiAPP;
import com.mei.chaji.app.Constants;
import com.mei.chaji.app.Contact;
import com.mei.chaji.base.activity.BaseActivity;
import com.mei.chaji.component.ActivityController;
import com.mei.chaji.core.bean.main.GoodService;
import com.mei.chaji.core.bean.main.Mqttmessages;
import com.mei.chaji.core.bean.msg.InsMessage;
import com.mei.chaji.core.http.api.RetrofitServiceManager;
import com.mei.chaji.core.rxretorfit.MyObserver;
import com.mei.chaji.instruction.MainCommunicate;
import com.mei.chaji.utils.CommonUtils;
import com.mei.chaji.utils.ECBAESUtils;
import com.mei.chaji.utils.Install;
import com.mei.chaji.utils.LogsFileUtil;
import com.mei.chaji.utils.TTSUtils;
import com.mei.chaji.utils.Utilss;
import com.vondear.rxtool.view.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BuyGoodsActivity extends BaseActivity {
    static String TAG = "BuyGoodsActiConstants.MQTT_FLAGvity";
    @BindView(R.id.iv_refill_tea)
    ImageView iv_refill_tea;
    @BindView(R.id.refill_corner)
    ImageView refill_corner;
    @BindView(R.id.tv_yunwei)
    TextView tv_yunwei;
    @BindView(R.id.price1)
    TextView price1;
    @BindView(R.id.price2)
    TextView price2;
    @BindView(R.id.iv_tea1)
    ImageView iv_tea1;
    @BindView(R.id.iv_tea2)
    ImageView iv_tea2;
    @BindView(R.id.tv_tea1_name)
    TextView tv_tea1_name;
    @BindView(R.id.tv_tea2_name)
    TextView tv_tea2_name;
    @BindView(R.id.tv_refill)
    TextView tv_refill;
    @BindView(R.id.tv_tea1_ds)
    TextView tv_tea1_ds;
    @BindView(R.id.tv_tea2_ds)
    TextView tv_tea2_ds;
    @BindView(R.id.img_qrcode)
    ImageView img_qrcode;
    int gar_flag;
    boolean canbuy = false;
    GoodService goodService;
    Gson gson;
    int garway_status1, garway_status2, bit0;
    List<GoodService> goodServices = new ArrayList<>();
    //茶杯属性
    //茶杯货道
    int Cargo_way;
    String cup_price, goods_name, goods_id, oneGargoWay, twoGargoWay, cargoWayType, isFree;
    //回调接口
    boolean open_one, open_two;
    boolean one_cup, two_cup;
    private boolean press_refill;

    String filepath;
    //计时操作
//    private Timer mTimer; // 计时器，每1秒执行一次任务
    private long mLastActionTime; // 上一次操作时间

    String err_code;
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    String d_cup_price;
    int bit1, bit2, bit3, bit4, bit5, bit6, bit7, bit8, bit9, bit10, bit11, bit12, bit13, bit14, bit15, bit16, bit17,
            bit18, bit19, bit20, bit21;
    private int face_mqtt;
    private boolean rufund_result;
    private Integer wendu_shi;
    private boolean wendu_flag;
    private boolean apprun = false;
//    private boolean shuiwen2 = false;
//    private boolean meibei2 = false;
//    private boolean shuiwen1 = false;
//    private boolean meibei1 = false;
//    private boolean zhengmang = false;
//    private boolean xushuiwendu = false;

    private CountDownTimer countDownTimer;
//    private boolean mqtt_connect = true;

    @SuppressLint("CheckResult")
    private void initListener() {
        RxView.clicks(iv_refill_tea).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o ->
                        gorefill());
        tv_yunwei.setOnLongClickListener(v -> {
            goops();
            return false;
        });
        RxView.clicks(iv_tea1).subscribe((Object o) -> {
            tea1_click();
        });
        RxView.clicks(price1).subscribe((Object o) -> {
            tea1_click();
        });
        RxView.clicks(price2).subscribe((Object o) -> {
            tea2_click();
        });
        RxView.clicks(iv_tea2).subscribe((Object o) -> {
            tea2_click();
        });

        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                stopTimer();
            }
        };
        countDownTimer.start();
    }

    private void tea2_click() {
        countDownTimer.start();
        price1.setBackgroundResource(R.mipmap.price_bg);
        price2.setBackgroundResource(R.mipmap.price_check);
        goods_id = goodServices.get(0).getGoodsId();
        cup_price = goodServices.get(0).getGoodsPrice();
        goods_name = goodServices.get(0).getGoodsName();
        Cargo_way = 1;
        if (open_one && bit1 == 0 && bit0 == 0 && bit3 == 0 && wendu_flag && bit5 == 0) {
            if (isFree.equals("0")) {
                Bundle bundle = new Bundle();
                bundle.putInt("ch_type", Cargo_way);
                bundle.putString("goods_name", goods_name);
                bundle.putString("cup_price", cup_price);
                bundle.putString("goods_id", goods_id);
                Intent i = new Intent(BuyGoodsActivity.this, PayActivity.class);
                i.putExtra("bundle", bundle);
                startActivity(i);
            } else {
                FreePay();
            }
        } else {
            RxToast.warning("商品暂时不能购买");
            TTSUtils.getInstance().pauseSpeech();
            TTSUtils.getInstance().speak("商品暂时不能购买");
            if (bit1 == 1) {
                if (!nocup_a) {
                    FaultReminder("A货道无杯");
                    FaultWx("A货道无杯");
                }
                RxToast.warning("没杯啦，不能购买");
                TTSUtils.getInstance().pauseSpeech();
                TTSUtils.getInstance().speak("没杯啦，不能购买");
            }
            if (!wendu_flag) {
                RxToast.warning("当前水温不够,请稍作等待");
                TTSUtils.getInstance().pauseSpeech();
                TTSUtils.getInstance().speak("当前水温不够,请稍作等待");
            }
        }
    }

    private void tea1_click() {
        countDownTimer.start();
        price1.setBackgroundResource(R.mipmap.price_check);
        price2.setBackgroundResource(R.mipmap.price_bg);
        goods_id = goodServices.get(1).getGoodsId();
        cup_price = goodServices.get(1).getGoodsPrice();
        goods_name = goodServices.get(1).getGoodsName();
        Cargo_way = 2;
        Log.e(TAG, "initListener: " + garway_status1 + garway_status2 + bit3 + bit4 + bit0);
        if (open_two && bit2 == 0 && bit0 == 0 && bit4 == 0 && wendu_flag && bit5 == 0) {
            if (isFree.equals("0")) {
                Bundle bundle = new Bundle();
                bundle.putInt("ch_type", Cargo_way);
                bundle.putString("goods_name", goods_name);
                bundle.putString("cup_price", cup_price);
                bundle.putString("goods_id", goods_id);
                Intent i = new Intent(BuyGoodsActivity.this, PayActivity.class);
                i.putExtra("bundle", bundle);
                startActivity(i);
            } else {
                FreePay();
            }
        } else {
            RxToast.warning("商品暂时不能购买");
            TTSUtils.getInstance().pauseSpeech();
            TTSUtils.getInstance().speak("商品暂时不能购买");
            if (bit2 == 1) {
                if (!nocup_b) {
                    FaultReminder("B货道无杯");
                    FaultWx("B货道无杯");
                }
                RxToast.warning("没杯啦，不能购买");
                TTSUtils.getInstance().pauseSpeech();
                TTSUtils.getInstance().speak("没杯啦，不能购买");
            }
            if (!wendu_flag) {
                RxToast.warning("当前水温不够,请稍作等待");
                TTSUtils.getInstance().pauseSpeech();
                TTSUtils.getInstance().speak("当前水温不够,请稍作等待");
            }
        }
    }



    private void gorefill() {
        countDownTimer.start();
        if (bit0 == 0 && wendu_flag && bit5 == 0) {
//            mTimer.cancel();
            refill_corner.setVisibility(View.VISIBLE);
            if (isFree.equals("0")) {
                Intent i = new Intent(BuyGoodsActivity.this, RefillPayActivity.class);
                startActivity(i);
            } else {
                MainCommunicate.getInstance().normal_refill();
                Intent i = new Intent(BuyGoodsActivity.this, RefillTeaActivity.class);
                startActivity(i);
            }
        } else {
            if (!wendu_flag) {
                TTSUtils.getInstance().pauseSpeech();
                TTSUtils.getInstance().speak("当前水温不够,请稍作等待");
            }
            RxToast.normal("设备正忙，请稍作等待");
            TTSUtils.getInstance().pauseSpeech();
            TTSUtils.getInstance().speak("设备正忙，请稍作等待");

//            startTimer();
        }
    }

    @Override
    protected void initUI() {
//        sp = getSharedPreferences("mc_info", MODE_PRIVATE);
//        editor = sp.edit();
        Log.e(TAG, "initUI: " + "进入界面");
        press_refill = false;
        gson = new Gson();
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "huawenhubo.ttf");
        tv_refill.setTypeface(iconfont);

        Typeface font_heiti = Typeface.createFromAsset(getAssets(), "zhongheiti.ttf");
        tv_yunwei.setTypeface(font_heiti);
        tv_tea1_ds.setTypeface(font_heiti);
        tv_tea2_ds.setTypeface(font_heiti);
        tv_tea1_name.setTypeface(font_heiti);
        tv_tea2_name.setTypeface(font_heiti);
        price1.setTypeface(font_heiti);
        price2.setTypeface(font_heiti);

        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "chaji" + File.separator;

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_product;
    }

    @Override
    protected void initEventAndData() {
        ActivityController.getInstance().addActivity(this);
        cachedThreadPool.execute(() -> {
            Map<String, String> map = new HashMap<>();
            map.put("appId", "APP0000001");
            map.put("secret", "chaji20190505");
            map.put("deviceNo", deviceNo);
            String aa = CommonUtils.getGsonEsa(map);
            RetrofitServiceManager.getAPIService().getAllGoodService(aa)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver<String>() {

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(String s) {
                            if (goodServices != null) {
                                goodServices.clear();
                            } else {
                                goodServices = new ArrayList<>();
                            }
                            String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, s);
                            Log.e("解密", "onSuccess: " + decryStr);
                            //第一步：先获取jsonobject对象
                            com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(decryStr);
                            //第二步：把对象转换成jsonArray数组
                            boolean result = jsonObject1.getBoolean("result");
                            JSONArray array = jsonObject1.getJSONArray("rows");
                            //第三步：将字符串转成list集合
                            goodServices = com.alibaba.fastjson.JSONObject.parseArray(array.toJSONString(), GoodService.class);//把字符串转换成集合
                            if (result) {
                                canbuy = true;
                                if (goodServices.size() > 0) {
                                    isFree = goodServices.get(0).getIsFree();
//                                    goods_id = goodServices.get(0).getGoodsId();
                                    if (isFree.equals("0")) {
                                        price1.setText("￥" + goodServices.get(1).getGoodsPrice());
                                        price2.setText("￥" + goodServices.get(0).getGoodsPrice());
                                    } else {
                                        price1.setText("免费");
                                        price2.setText("免费");
                                    }
                                    tv_tea1_name.setText(goodServices.get(1).getGoodsName());
                                    tv_tea2_name.setText(goodServices.get(0).getGoodsName());
                                    tv_tea1_ds.setText(goodServices.get(1).getGoodsRemark());
                                    tv_tea2_ds.setText(goodServices.get(0).getGoodsRemark());
                                    if (goodServices.get(0).getOneGargoWay().equals("1")) {
                                        open_one = true;
                                    } else {
                                        Log.e(TAG, "onSuccess: " + "一货道已经关闭");
                                        RxToast.warning("A货道已经关闭");
                                        open_one = false;
                                    }
                                    if (goodServices.get(0).getTwoGargoWay().equals("1")) {
                                        open_two = true;
                                    } else {
                                        Log.e(TAG, "onSuccess: " + "二货道已经关闭");
                                        RxToast.warning("B货道已经关闭");
                                        open_two = false;
                                    }
                                }
                            } else {
                                canbuy = false;
                                Log.e("=====", "onNext: " + "没有数据集合");
                            }
                        }
                    });
        });
        cachedThreadPool.execute(() -> {
            Map<String, String> map = new HashMap<>();
            map.put("appId", "APP0000001");
            map.put("secret", "chaji20190505");
            map.put("deviceNo", deviceNo);
            String aa = CommonUtils.getGsonEsa(map);
            RetrofitServiceManager.getAPIService().getGoodQRCode(aa)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver<String>() {

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(String s) {
                            try {
                                String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, s);
                                Log.e("解密", "onSuccess: " + decryStr);
                                //第一步：先获取jsonobject对象
                                com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(decryStr);
                                if (jsonObject1 != null) {
                                    Glide.with(BuyGoodsActivity.this).load(jsonObject1.getString("url")).into(img_qrcode);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });


        });
        initListener();
    }


    public void FreePay() {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> map = new HashMap<>();
                map.put("appId", "APP0000001");
                map.put("secret", "chaji20190505");
                map.put("deviceNo", deviceNo);
                map.put("goodsId", goods_id);
                map.put("goodsName", goods_name);
                map.put("waterTemperature", "1");
                map.put("gargoWay", Cargo_way + "");
                map.put("cupNumber", "1");
                String order_no = CommonUtils.encodeURI(deviceNo + System.currentTimeMillis());
                map.put("orderNo", order_no);
                String res_json = CommonUtils.getGsonEsa(map);
                RetrofitServiceManager.getAPIService().zeroRMBpay(res_json)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MyObserver<String>() {
                            @Override
                            public void onSuccess(String response) {
                                String de_json = ECBAESUtils.decrypt(Constants.AES_KEY, response);
                                Log.e(TAG, "返回免费茶机数据: " + de_json);
                                com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(de_json);
                                //第二步：把对象转换成jsonArray数组
                                boolean result = jsonObject1.getBoolean("result");
                                if (result) {
                                    MainCommunicate.getInstance().normal_water(Cargo_way);
//                                    Toast.makeText(BuyGoodsActivity.this,"目标：BuyGoodsActivity",Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(BuyGoodsActivity.this, MakeTeaActivity.class);
                                    startActivity(i);
                                }
                            }
                        });
            }
        });
    }

    public void goops() {
//        mTimer.cancel();
        Intent intent = new Intent(this, OpsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        startTimer();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mTimer.cancel();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        TTSUtils.getInstance().pauseSpeech();
        if (handler != null) {
            handler.removeCallbacksAndMessages(this);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    //在ui线程执行 优先级100
    public void getmsg1(InsMessage msg) {
        if (msg.getInsmsg().equals("ins_success")) {
            String s_wendu = msg.getErr_code().substring(8, 10);
            wendu_shi = Integer.valueOf(s_wendu, 16);
            int tmp = sp.getInt("hotWaterTemperature", 77);
            Log.e(TAG, "获取温度: " + wendu_shi + "存储温度" + tmp);
            if (wendu_shi < tmp) {
                wendu_flag = false;
            } else {
                wendu_flag = true;
            }
            String warn1 = msg.getInscontent1();
            String warn2 = msg.getInscontent2();
            String warn3 = msg.getInscontent3();
//            Log.e(TAG, "警告一的状态 " + warn1 + warn2 + warn3);
            if (warn3.substring(7, 8).equals("1")) {
                bit0 = 1;
            } else {
                bit0 = 0;
                if (!apprun) {
                    if (ChajiAPP.getInstance().getQueue().size() > 0) {
                        RxToast.normal("订单连续出杯");
                        apprun = true;
                        handler.sendEmptyMessageDelayed(1001, 10000);
                    }
                }
            }
            if (warn3.substring(0, 1).equals("1")) {
                bit7 = 1;
                Log.e(TAG, "警告三分指令: " + "续杯加水按钮等待超时");
            } else {
                bit7 = 0;
            }
            if (warn3.substring(1, 2).equals("1")) {
                bit6 = 1;
//                if (isIns1) {
//                    if (!isIns5) {
//                        TTSUtils.getInstance().speak("茶杯未取走");
//
//                        gomake3();
//                        isIns5 = true;
//                    }
//                }
                Log.e(TAG, "警告三分指令: " + "茶杯未取走");
            } else {
                bit6 = 0;
            }
            if (warn3.substring(2, 3).equals("1")) {
                bit5 = 1;
                Log.e(TAG, "警告三分指令: " + "取茶口隔离们无法关闭");
            } else {
                bit5 = 0;
            }
            if (warn3.substring(2, 3).equals("0")) {
//                Log.e(TAG, "getmsg1: " + bit5);
                bit5 = 0;
            } else {
                bit5 = 1;
            }
            if (warn3.substring(3, 4).equals("1")) {
                bit4 = 1;
                Log.e(TAG, "警告三分指令: " + "落杯器2卡杯故障");
            } else {
                bit4 = 0;
            }
            if (warn3.substring(4, 5).equals("1")) {
                bit3 = 1;
                Log.e(TAG, "警告三分指令: " + "落杯器1卡杯故障");
            } else {
                bit3 = 0;
            }
            if (warn3.substring(5, 6).equals("1")) {
                bit2 = 1;
                garway_status2 = 1;
                Log.e(TAG, "警告三分指令: " + "二货道无杯");
            } else {
                garway_status2 = 0;
                bit2 = 0;
            }
            if (warn3.substring(6, 7).equals("1")) {
                bit1 = 1;
                garway_status1 = 1;
            } else {
                garway_status1 = 0;
                bit1 = 0;
            }
            if (warn1.substring(1, 2).equals("1")) {
//                1-为按下续水按键，此时界面应该显示为续水界面
                if (!press_refill) {
//                    mTimer.cancel();
//                    MainCommunicate.getInstance().normal_refill();
                    if (bit0 == 0 && wendu_flag) {
                        Intent i = new Intent(BuyGoodsActivity.this, RefillPayActivity.class);
                        startActivity(i);
                    } else {
                        if (!wendu_flag) {
                            TTSUtils.getInstance().speak("当前水温不够,请稍作等待");
                        }
                        RxToast.normal("设备正忙，请稍作等待");
                    }
                    press_refill = true;
                }
            } else {
            }
            if (warn1.substring(2, 3).equals("1")) {
//                if (isIns1) {
//                    if (!isIns3) {
//                        TTSUtils.getInstance().speak("取杯完成,欢迎下次光临");
//                        Log.e(TAG, "警告一分指令: " + "取杯完成");
//                        gomake3();
//                    }
//                }
                bit21 = 1;
            } else {
                bit21 = 0;
            }
            if (warn1.substring(3, 4).equals("1")) {
//                if (isIns1) {
//                    if (!isIns4) {
//                        TTSUtils.getInstance().speak("送杯完成,请取杯，否则一段时间后将丢杯");
//                        isIns4 = true;
//                    }
//                }
                bit20 = 1;
            } else {
                bit20 = 0;
            }
            if (warn1.substring(4, 5).equals("1")) {
                bit19 = 1;
//                if (isIns1) {
//                    if (!isIns2) {
//                        TTSUtils.getInstance().speak("加水完成，开始送杯");
//                        Log.e(TAG, "警告一分指令: " + "送杯完成");
//                        if (pay_types == 1) {
//                            gomake2(2);
//                        }else {
//
//                        }
//                        isIns2 = true;
//                    }
//                }
            } else {
                bit19 = 0;
            }
            if (warn1.substring(5, 6).equals("1")) {
                bit18 = 1;
                Log.e(TAG, "getmsg1: " + "正在制作..");
                Log.e(TAG, "警告一分指令: " + "蓄水接杯完成");
            } else {
                bit18 = 0;
            }
            if (warn1.substring(6, 7).equals("1")) {
                bit17 = 1;
//                Log.e(TAG, "警告一分指令: " + "储物柜没有关闭" );
            } else {
                bit17 = 0;
            }
            if (warn1.substring(7, 8).equals("1")) {
                Log.e(TAG, "警告一分指令: " + "加热故障加热管不加热");
            } else {
                bit16 = 0;
            }

            if (warn2.substring(0, 1).equals("1")) {
                Log.e(TAG, "警告二分指令: " + "蓄水失败");
                bit15 = 1;
            } else {
                bit15 = 0;
            }
            if (warn2.substring(1, 2).equals("1")) {
                bit14 = 1;
//                Log.e(TAG, "警告二分指令: " + "停电报警");
            } else {
                bit14 = 0;
            }
            if (warn2.substring(2, 3).equals("1")) {
                Log.e(TAG, "警告二分指令: " + "切换开关" + bit13);
                bit13 = 1;
            } else {
                bit13 = 0;
            }
            if (warn2.substring(3, 4).equals("1")) {
                Log.e(TAG, "警告二分指令: " + "电子隔离们开启");
                bit12 = 1;
            } else {
                bit12 = 0;
            }
            if (warn2.substring(4, 5).equals("1")) {
                Log.e(TAG, "警告二分指令: " + "所有桶用完" + bit11);
                bit11 = 1;
            } else {
                bit11 = 0;
            }
            if (warn2.substring(5, 6).equals("1")) {
                Log.e(TAG, "警告二分指令: " + "1桶用完" + bit10);
                bit10 = 1;
            } else {
                bit10 = 0;
            }
            if (warn2.substring(6, 7).equals("1")) {
                Log.e(TAG, "警告二分指令: " + "内置冷水箱水位低" + bit9);
                bit9 = 1;
            } else {
                bit9 = 0;
            }
            if (warn2.substring(7, 8).equals("1")) {
                bit8 = 1;
                Log.e(TAG, "警告二分指令: " + "加热故障加热管不加热");
            } else {
                bit8 = 0;
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    //在ui线程执行 优先级100
    public void getmsg(Mqttmessages msg) {
        if (msg.getType() != null) {
            switch (msg.getType()) {
                case "4":
                    RxToast.normal("消息推送通知成功");
                    if (NetworkUtils.isWifiConnected()) {
                        if (NetworkUtils.isAvailableByPing()) {
                            updata(msg);
                        } else {
                            updataError();
                        }
                    }
                    if (NetworkUtils.is4G()) {
                        if (NetworkUtils.isAvailableByPing()) {
                            updata(msg);
                        } else {
                            updataError();
                        }
                    }
                    break;
                case "2":
                    String order_no = msg.getOrderNo();
                    String orderAmount = msg.getOrderAmount();
                    String preAmount = msg.getPreAmount();
                    int gargoWay = msg.getGargoWay();
                    //                int tmp = msg.getWaterTemperature();
                    Log.e(TAG, "货道: " + gargoWay);
                    //出水指令
                    MainCommunicate.getInstance().normal_water(gargoWay);
                    //            Toast.makeText(this,"目标：BuyGoodsActivity",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(this, MakeTeaActivity.class);
                    i.putExtra("order_no", order_no);
                    i.putExtra("orderAmount", orderAmount);
                    i.putExtra("preAmount", preAmount);
                    startActivity(i);

                    //            String order_no = msg.getOrderNo();
                    //            refund(order_no);
                    break;
                case "3":
                    TTSUtils.getInstance().speak("支付超时,续杯失败");
                    RxToast.normal("支付超时，续杯失败");
                    break;
                case "6":
                    RxToast.normal("系统即将在10秒钟后重启");
                    handler.sendEmptyMessageDelayed(3, 1000);
                    break;
            }
        }
    }

    /**
     * 预留退款信息
     */
    private boolean refund(String orderNo) {
        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();
        map.put("appId", "APP0000001");
        map.put("secret", "chaji20190505");
        map.put("orderNo", orderNo);
        String jsonbody = gson.toJson(map);
        String aa = ECBAESUtils.encrypt(Constants.AES_KEY, jsonbody);
        RetrofitServiceManager.getAPIService().alipayRefund(aa)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>() {

                    @Override
                    public void onSuccess(String s) {
                        String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, s);
                        //第一步：先获取jsonobject对象
                        com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(decryStr);
                        Log.e(TAG, "解码第二个" + decryStr);
                        //第二步：把对象转换成jsonArray数组
                        boolean results = jsonObject1.getBoolean("result");
                        Log.e(TAG, "退款结果: " + results);
                        if (results) {
                            TTSUtils.getInstance().speak("出茶失败,已经自动退款");
                            RxToast.normal("出茶失败,已经自动退款");
                        } else {
                            rufund_result = false;
                        }
                    }

                });
        return rufund_result;
    }


    private void stopTimer() {
        Intent i = new Intent(this, AdpicActivity.class);
        startActivity(i);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        mTimer.cancel();
//        mLastActionTime = System.currentTimeMillis();
        return super.dispatchTouchEvent(ev);
    }


    public void createDownloadTask(String path1, String adurl) {
        FileDownloader.getImpl().create(adurl)
                .setPath(path1)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        if (isContinue) {

                        } else {
                        }
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                        updateProgress(soFarBytes, totalBytes,
//                                task.getSpeed());
                        Log.e(TAG, "progress: " + soFarBytes);
//                        RxToast.normal("progress" + soFarBytes);
                        String result_far;
                        result_far = CommonUtils.fileSize((long) soFarBytes);
                        String result_total;
                        result_total = CommonUtils.fileSize((long) totalBytes);
                        RxToast.normal("程序升级中..总共需要更新" + result_total + "，已经更新" + result_far);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Log.e(TAG, "completed: " + "apk下载完成");
                        RxToast.normal("apk下载完成");
                        boolean is = Install.isRoot();
                        if (is) {
                            if (face_mqtt == 1) {
                                Install.install(filepath + "茶机.apk");
                            } else if (face_mqtt == 2) {
                                Install.install(filepath + "人脸识别.apk");
                                RxToast.normal("程序升级完成,将在10秒后重启");
                                handler.sendEmptyMessageDelayed(3, 10000);
                            }
                        } else {
                            Log.e(TAG, "completed: " + "手机没有root");
//                            RxToast.normal("手机没有root");
                        }

                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        updataError();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                }).start();
    }

    private void updataError() {
        String updateVersionId = sp.getString("updateVersionId", "");
        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();
        map.put("appId", "APP0000001");
        map.put("secret", "chaji20190505");
        map.put("deviceNo", deviceNo);
        map.put("updateVersionId", updateVersionId);
        map.put("status", "2");
        String aa = CommonUtils.getGsonEsa(map);
        RetrofitServiceManager.getAPIService().alertUpVersion(aa)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>() {
                    @Override
                    public void onSuccess(String response) {
                        String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, response);
                        com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(decryStr);
                        //第二步：把对象转换成jsonArray数组
                        boolean result = jsonObject1.getBoolean("result");
                        if (result) {
                            RxToast.normal("超时提示成功");
                        }
                    }
                });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 3:
//                    RxToast.warning("重启");
                    try {
                        Runtime.getRuntime().exec("su -c reboot");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1001:
                    try {
                        for (String value : ChajiAPP.getInstance().getQueue().values()) {
                            org.json.JSONObject jsonObject = new  org.json.JSONObject(value);
                            String orderNo_ = jsonObject.getString("orderNo");
                            int gargoWay = jsonObject.getInt("gargoWay");
                            String orderAmount = jsonObject.getString("orderAmount");
                            String preAmount = jsonObject.getString("preAmount");
                            Log.e(TAG, "货道: " + gargoWay);
                            //出水指令
                            MainCommunicate.getInstance().normal_water(gargoWay);
                            Intent i = new Intent(BuyGoodsActivity.this, MakeTeaActivity.class);
                            i.putExtra("order_no", orderNo_);
                            i.putExtra("orderAmount", orderAmount);
                            i.putExtra("preAmount", preAmount);
                            startActivity(i);
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void updata(Mqttmessages msg) {
        TTSUtils.getInstance().speak("程序升级中,现在暂时不能做购茶操作");
        iv_tea1.setEnabled(false);
        iv_tea2.setEnabled(false);
        iv_refill_tea.setEnabled(false);
        price1.setEnabled(false);
        price2.setEnabled(false);
        String updateVersionId = msg.getDevice_msg();
        Log.e(TAG, "updateVersionId: " + updateVersionId);
        editor.putString("updateVersionId", updateVersionId);
        editor.apply();
        String url = Contact.p_url + File.separator + "file" + File.separator + msg.getOrderNo().replace("\\", "/");
        if (msg.getWaterTemperature() == 1) {
            face_mqtt = 1;
            File file = new File(filepath + File.separator + "茶机.apk");
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    createDownloadTask(filepath + "茶机" + ".apk", url);
                }
            } else {
                createDownloadTask(filepath + "茶机" + ".apk", url);
            }
        } else if (msg.getWaterTemperature() == 2) {
            face_mqtt = 2;
            File file = new File(filepath + File.separator + "人脸识别.apk");
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    createDownloadTask(filepath + "人脸识别" + ".apk", url);
                }
            } else {
                createDownloadTask(filepath + "人脸识别" + ".apk", url);
            }
        }
    }





    boolean nocup_a = false;
    boolean nocup_b = false;
    private void FaultReminder(String content) {
        Map<String, String> map = new HashMap<>();
        map.put("appId", "APP0000001");
        map.put("secret", "chaji20190505");
        map.put("deviceNo", deviceNo);
        map.put("reportTime", String.valueOf(Utilss.getCurrentTimesss()));
        map.put("reportStatus", "1");
        map.put("reportContent", content);
        String aa = CommonUtils.getGsonEsa(map);
        RetrofitServiceManager.getAPIService().addDeviceState(aa)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>() {
                    @Override
                    public void onSuccess(String response) {
                        String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, response);
                        JSONObject jsonObject1 = JSONObject.parseObject(decryStr);
                        boolean results = jsonObject1.getBoolean("result");
                        if (results) {
                            if (!nocup_a && content.equals("A货道无杯")) {
                                nocup_a = true;
                            }
                            if (!nocup_b && content.equals("B货道无杯")) {
                                nocup_b = true;
                            }
                        }
                    }
                });
    }


    private void FaultWx(String wxcontent) {
        LogsFileUtil.getInstance().addLog("微信提醒",wxcontent);
        Map<String, String> map = new HashMap<>();
        map.put("appId", "APP0000001");
        map.put("secret", "chaji20190505");
        map.put("deviceNo", deviceNo);
        map.put("waterNumber", wxcontent);
        map.put("faultCause", "");
        String aa = CommonUtils.getGsonEsa(map);
        RetrofitServiceManager.getAPIService().replenishGoodsInform(aa)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>() {
                    @Override
                    public void onSuccess(String response) {
                        String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, response);
                        LogsFileUtil.getInstance().addLog("微信返回提醒",decryStr);
                        Log.e("微信服务器返回提醒","微信服务器返回提醒"+decryStr);
                        JSONObject jsonObject1 = JSONObject.parseObject(decryStr);
                        boolean results = jsonObject1.getBoolean("result");
                    }
                });
    }


}
