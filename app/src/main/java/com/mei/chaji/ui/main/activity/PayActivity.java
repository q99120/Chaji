package com.mei.chaji.ui.main.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.mei.chaji.R;
import com.mei.chaji.app.Constants;
import com.mei.chaji.app.Contact;
import com.mei.chaji.base.activity.BaseActivity;
import com.mei.chaji.component.ActivityController;
import com.mei.chaji.core.bean.main.AdData;
import com.mei.chaji.core.bean.main.Mqttmessages;
import com.mei.chaji.core.bean.main.VideoInfo;
import com.mei.chaji.core.bean.main.VideoUtil;
import com.mei.chaji.core.bean.msg.InsMessage;
import com.mei.chaji.core.http.api.RetrofitServiceManager;
import com.mei.chaji.core.rxretorfit.MyObserver;
import com.mei.chaji.instruction.MainCommunicate;
import com.mei.chaji.ui.main.view.GlideImageLoader;
import com.mei.chaji.utils.CommonUtils;
import com.mei.chaji.utils.ECBAESUtils;
import com.mei.chaji.utils.IOUtils;
import com.mei.chaji.utils.LogsFileUtil;
import com.mei.chaji.utils.TTSUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vondear.rxtool.view.RxToast;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifImageView;

public class PayActivity extends BaseActivity {
    List<VideoInfo> videos = new ArrayList<>();
    int list_flag;
    @BindView(R.id.iv_youhui)
    ImageView iv_youhui;
    @BindView(R.id.viewpageree)
    Banner banner;
    //    @BindView(R.id.banner)
//    Banners banners;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.tv_sao)
    TextView tv_sao;
    String TAG = "PayActivity";
    private String filepath;
    private String imagepath;
    private String videopath;

    RxPermissions rxPermissions;
    int garway_status1, garway_status2;
    private List<String> ad_urls = new ArrayList<>();
    List<AdData> adDatas = new ArrayList<>();
    List<VideoUtil> videoUtils = new ArrayList<>();
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    int bit0, bit1, bit2, bit3, bit4, bit5, bit6, bit7, bit8, bit9, bit10, bit11, bit12, bit13, bit14, bit15, bit16, bit17,
            bit18, bit19, bit20, bit21;
    private boolean isError;
    boolean isgotea = false;
    int autoCurrIndex = 0;
    //计时操作
    private Timer mTimer; // 计时器，每1秒执行一次任务
    private long mLastActionTime; // 上一次操作时间
    List<Fragment> fragments = new ArrayList<>();
    private FragmentPagerAdapter adapter;

    VideoUtil videoUtil;
    List<String> images = new ArrayList<>();
    List<String> youhui_url = new ArrayList<>();
    @BindView(R.id.tv_cup_price)
    TextView tv_cup_price;
    @BindView(R.id.tv_cup_name)
    TextView tv_cup_name;
    @BindView(R.id.iv_face_pay)
    GifImageView iv_face_pay;
    @BindView(R.id.iv_ali_qr)
    ImageView iv_ali_qr;
    @BindView(R.id.iv_wx_qr)
    ImageView iv_wx_qr;
    @BindView(R.id.pay_time)
    TextView pay_time;
    String goods_id, name, price;
    int ch;
    String encode_name, order_no;


    /**
     * 判断茶机状态
     */
    private boolean make_pre, make_cup, make_finish, make_pick;

    /**
     * 获取茶机故障状态
     */
    private boolean water_full, cup_one_full, cup_two_full;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Map<String, String> map = new HashMap<>();
                map.put("appId", "APP0000001");
                map.put("secret", "chaji20190505");
                map.put("deviceNo", deviceNo);
                map.put("adPositionId", "2");
                String aa = CommonUtils.getGsonEsa(map);
                RetrofitServiceManager.getAPIService().getAllAdString(aa).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MyObserver<String>() {
                            @Override
                            public void onSuccess(String s) {
                                if (adDatas != null) {
                                    adDatas.clear();
                                } else {
                                    adDatas = new ArrayList<>();
                                }
                                String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, s);
                                JSONObject jsonObject1 = JSONObject.parseObject(decryStr);
                                boolean rusult = jsonObject1.getBoolean("result");
                                JSONArray array = jsonObject1.getJSONArray("rows");
                                //第三步：将字符串转成list集合
                                adDatas = JSONObject.parseArray(array.toJSONString(), AdData.class);//把字符串转换成集合
                                if (rusult) {
                                    Log.e(TAG, "获取图片个数: " + adDatas.size());
                                    if (adDatas.size() > 0) {
                                        for (int i = 0; i < adDatas.size(); i++) {
                                            if (adDatas.get(i).getAdType().equals("1")) {
                                                String url = imagepath + adDatas.get(i).getAdUrl().replace("\\", "/");
                                                String ar_qoce = imagepath + adDatas.get(i).getAdQrcode().replace("\\", "/");
                                                Log.e(TAG, "图片: " + url);
                                                images.add(url);
                                                youhui_url.add(ar_qoce);
                                            }
                                        }
                                        initBanner();
                                    }
                                }
                            }
                        });
            }
        }
    };

    private void initBanner() {
        Glide.with(PayActivity.this).load(youhui_url.get(0)).into(iv_youhui);
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(8000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.Default);
        banner.start();
        Log.e(TAG, "initBanner: " + banner.getChildCount());
        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.e(TAG, "banner: " + i);
                String yo_url = youhui_url.get(i);
                Log.e(TAG, "onPageSelected: " + yo_url);
                if (yo_url != null) {
                    iv_youhui.setVisibility(View.VISIBLE);
                    tv_sao.setVisibility(View.VISIBLE);
                    Glide.with(PayActivity.this).load(yo_url).into(iv_youhui);
                } else {
                    tv_sao.setVisibility(View.INVISIBLE);
                    iv_youhui.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * 倒计时30秒，一次1秒
     */
    CountDownTimer pay_times = new CountDownTimer(60 * 1000, 1000) {
        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
            if (pay_time != null) {
                pay_time.setText("请在" + millisUntilFinished / 1000 + "秒内完成支付");
            }
        }

        @Override
        public void onFinish() {
            gobuy();
        }
    };

    private boolean press_refill;
    private boolean rufund_result;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay;
    }


    @SuppressLint("CheckResult")
    @Override
    protected void initEventAndData() {
        ActivityController.getInstance().addActivity(this);
        rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(granted -> {
            if (granted) {
                cachedThreadPool.execute(() -> {
                    videopath = Contact.p_url + File.separator + "file" + File.separator;
                    filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "chaji" + File.separator;
                    imagepath = Contact.p_url + File.separator + "file" + File.separator;
                    Log.e(TAG, "initEventAndData: " + "执行hander之前");
                    handler.sendEmptyMessage(1);
                });
            }
        });
        RxView.clicks(iv_face_pay).subscribe((Object o) -> {
            Bundle bundle = new Bundle();
            bundle.putInt("ch_type", ch);
            bundle.putString("goods_name", encode_name);
            bundle.putString("cup_price", price);
            bundle.putString("goods_id", goods_id);
            bundle.putInt("temperature_type", 1);
            bundle.putInt("cup_num", 1);
            bundle.putString("order_no", order_no);
            bundle.putInt("face_type_flag", 1);
            Intent i = new Intent(PayActivity.this, FacePayActivity.class);
            i.putExtra("bundle", bundle);
            startActivity(i);
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gobuy();
            }
        });
    }

    private void gobuy() {
        Intent i = new Intent(PayActivity.this, BuyGoodsActivity.class);
        startActivity(i);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onResume() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        banner.stopAutoPlay();
        pay_times.cancel();
        TTSUtils.getInstance().pauseSpeech();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onPause();
    }

    @Override
    protected void initUI() {
        press_refill = false;
        cup_one_full = false;
        cup_two_full = false;
        water_full = false;

        Typeface font_heiti = Typeface.createFromAsset(getAssets(), "zhongheiti.ttf");
        tv_cup_name.setTypeface(font_heiti);
        tv_cup_price.setTypeface(font_heiti);
        pay_time.setTypeface(font_heiti);
        Intent i = getIntent();
        Bundle bundle = i.getBundleExtra("bundle");//得到从Activity传来的数据
        if (bundle != null) {
            goods_id = bundle.getString("goods_id");
            ch = bundle.getInt("ch_type");
            name = bundle.getString("goods_name");
            price = bundle.getString("cup_price");
            tv_cup_price.setText("￥" + price);
            tv_cup_name.setText(name);
        }
        initNet();
//        handler.postDelayed(exit_pay, 60000);
    }

    private void initNet() {
        cachedThreadPool.execute(() -> {
            encode_name = CommonUtils.encodeURI(name);
            order_no = CommonUtils.encodeURI(deviceNo + System.currentTimeMillis());
            Log.e(TAG, "initNet: " + goods_id + "名字" + encode_name + "编号" + deviceNo + "价格" + price);
            RetrofitServiceManager.getAPIService().getAliPayQRCode1(goods_id, encode_name,
                    deviceNo, price, order_no, "2", 1, ch, 1).
                    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver<ResponseBody>() {
                        @Override
                        public void onSuccess(ResponseBody responseBody) {
                            Log.e(TAG, "onSuccess微信: " + responseBody);
                            byte[] ss = IOUtils.get_byteimg(responseBody.byteStream());
                            Bitmap bitmap = BitmapFactory.decodeByteArray(ss, 0, ss.length);
                            iv_wx_qr.setImageBitmap(bitmap);
                        }
                    });
            RetrofitServiceManager.getAPIService().getAliPayQRCode1(goods_id, encode_name, deviceNo, price, order_no, "1"
                    , 1
                    , ch, 1).
                    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver<ResponseBody>() {
                        @Override
                        public void onSuccess(ResponseBody responseBody) {
                            Log.e(TAG, "onSuccess支付宝: " + responseBody);
                            byte[] ss = IOUtils.get_byteimg(responseBody.byteStream());
                            Bitmap bitmap = BitmapFactory.decodeByteArray(ss, 0, ss.length);
                            iv_ali_qr.setImageBitmap(bitmap);
                        }
                    });
            TTSUtils.getInstance().speak("请在一分钟之内完成支付");
            pay_times.start();
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //禁止使用返回键返回到上一页,但是可以直接退出程序**
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;//不执行父类点击事件
        }
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    //在ui线程执行 优先级100
    public void getmsg1(InsMessage msg) {
        if (msg.getInsmsg().equals("ins_success")) {

//            Log.e("=======", "getmsg1: " + "获得串口");
            String warn1 = msg.getInscontent1();
            String warn2 = msg.getInscontent2();
            String warn3 = msg.getInscontent3();
//            Log.e(TAG, "警告一的状态 " + warn1 + warn2 + warn3);
            if (warn3.substring(7, 8).equals("1")) {
                bit0 = 1;
//                Log.e(TAG, "警告三分指令: " + "忙");
            } else {
                bit0 = 0;
            }
            if (warn3.substring(0, 1).equals("1")) {
                bit7 = 1;
//                Log.e(TAG, "警告三分指令: " + "续杯加水按钮等待超时");
            } else {
                bit7 = 0;
            }
            if (warn3.substring(1, 2).equals("1")) {
                bit6 = 1;
//                Log.e(TAG, "警告三分指令: " + "茶杯未取走");
            } else {
                bit6 = 0;
            }
            if (warn3.substring(2, 3).equals("1")) {
                bit5 = 1;
//                Log.e(TAG, "警告三分指令: " + "取茶口隔离们无法关闭");
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
                if (!cup_two_full) {
                    Log.e(TAG, "警告三分指令: " + "二货道无杯");
                    cup_two_full = true;
                }
            } else {
                if (!cup_two_full) {
                    //存正常值
                    cup_two_full = true;
                }
            }
            if (warn3.substring(6, 7).equals("1")) {
                if (!cup_one_full) {
                    Log.e(TAG, "警告三分指令: " + "一货道无杯");
                    cup_one_full = true;
                }
            } else {
                if (!cup_one_full) {
                    Log.e(TAG, "警告三分指令: " + "一货道有杯");
                    cup_one_full = true;
                }
            }
            if (warn3.substring(6, 7).equals("1") && warn3.substring(5, 6).equals("1")) {
            }
            if (warn1.substring(0, 1).equals("1")) {

            } else {
            }
            if (warn1.substring(1, 2).equals("1")) {
                //1-为按下续水按键，此时界面应该显示为续水界面
//                if (!press_refill) {
//                    MainCommunicate.getInstance().normal_refill();
//                    Intent i = new Intent(PayActivity.this, RefillTeaActivity.class);
//                    startActivity(i);
//                    press_refill = true;
//                }
            }
            if (warn1.substring(2, 3).equals("1")) {
                if (!make_pick) {
                    TTSUtils.getInstance().speak("取杯完成，欢迎下次光临");
                    make_pick = true;
                }
            }
            if (warn1.substring(3, 4).equals("1")) {
                if (!make_finish) {
                    TTSUtils.getInstance().speak("送杯完成，请取杯，否则将在一段时间后丢杯");
                    make_finish = true;
                    Log.e(TAG, "警告一分指令: " + "加水完成，开始送杯");
                }
            }
            if (warn1.substring(4, 5).equals("1")) {
                if (!make_cup) {
                    TTSUtils.getInstance().speak("加水完成，开始送杯");
                    make_cup = true;
                    Log.e(TAG, "警告一分指令: " + "加水完成，开始送杯");
                }
            }
            if (warn1.substring(5, 6).equals("1")) {
                bit18 = 1;
                Log.e(TAG, "警告一分指令: " + "蓄水接杯完成");
            } else {
                bit18 = 0;
            }
            if (warn1.substring(6, 7).equals("1")) {
                bit17 = 1;
//                Log.e(TAG, "警告一分指令: " + "储物柜没关闭")   ;
            } else {
                bit17 = 0;
            }
            if (warn1.substring(7, 8).equals("1")) {

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
        Log.e(TAG, "getmsg: " + "222222");
        int curret_time = (int) (System.currentTimeMillis() / 1000);
        if (msg.getType().equals("2")) {
            //用户实际支付金额,优惠金额
            String orderAmount = msg.getOrderAmount();
            String preAmount = msg.getPreAmount();
            if (curret_time - msg.getDateTime() > 60) {
                Log.e("==============", "getmsg: " + "支付超时");
                LogsFileUtil.getInstance().addLog("微信/支付宝支付制茶超时","订单号:"+order_no+"优惠价格:"+preAmount+"实际价格:"+orderAmount);
                refund(order_no);
            } else {
                int gargoWay = msg.getGargoWay();
//                int tmp = msg.getWaterTemperature();
                Log.e(TAG, "货道: " + gargoWay);
                //出水指令
                LogsFileUtil.getInstance().addLog("微信/支付宝支付制茶成功","订单号:"+order_no+"优惠价格:"+preAmount+"实际价格:"+orderAmount);
                MainCommunicate.getInstance().normal_water(gargoWay);
//                Toast.makeText(PayActivity.this,"目标：PayActivity",Toast.LENGTH_LONG).show();
                Intent i = new Intent(PayActivity.this, MakeTeaActivity.class);
                i.putExtra("order_no", order_no);
                i.putExtra("orderAmount", orderAmount);
                i.putExtra("preAmount", preAmount);
                startActivity(i);
            }
        } else if (msg.getType().equals("3")) {
            if (curret_time - msg.getDateTime() > 60) {
                Log.e("==============", "getmsg: " + "支付超时");
                RxToast.normal("续杯超时");
            } else {

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
                        if (results) {
                            RxToast.showToastLong("出茶失败,已经自动退款");
                            TTSUtils.getInstance().speak("出茶失败,已经自动退款");
                            LogsFileUtil.getInstance().addLog("微信/支付宝扫码支付制茶退款成功","订单号:"+order_no);
                            gobuy();
                        } else {
                            rufund_result = false;
                        }
                    }

                });
        return rufund_result;
    }


}
