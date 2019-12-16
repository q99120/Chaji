package com.mei.chaji.ui.main.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.mei.chaji.R;
import com.mei.chaji.app.ChajiAPP;
import com.mei.chaji.app.Constants;
import com.mei.chaji.app.Contact;
import com.mei.chaji.base.activity.BaseActivity;
import com.mei.chaji.component.ActivityController;
import com.mei.chaji.core.bean.main.AdData;
import com.mei.chaji.core.bean.main.Mqttmessages;
import com.mei.chaji.core.bean.main.VideoUtil;
import com.mei.chaji.core.bean.msg.InsMessage;
import com.mei.chaji.core.http.api.RetrofitServiceManager;
import com.mei.chaji.core.rxretorfit.MyObserver;
import com.mei.chaji.instruction.MainCommunicate;
import com.mei.chaji.service.AlarmResetBroadcast;
import com.mei.chaji.service.INetEvent;
import com.mei.chaji.service.MQTTService;
import com.mei.chaji.service.MqttServiceConnection;
import com.mei.chaji.ui.main.adapter.FragmentAdapter;
import com.mei.chaji.ui.main.fragment.Fragment1;
import com.mei.chaji.utils.CommonUtils;
import com.mei.chaji.utils.ECBAESUtils;
import com.mei.chaji.utils.Install;
import com.mei.chaji.utils.LogsFileUtil;
import com.mei.chaji.utils.NetUtils;
import com.mei.chaji.utils.SpUtils;
import com.mei.chaji.utils.TTSUtils;
import com.mei.chaji.utils.Utilss;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vondear.rxtool.view.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AdpicActivity extends BaseActivity implements INetEvent {
    @BindView(R.id.btn_click_me)
    ImageView btn_click_me;
    @BindView(R.id.iv_youhui)
    ImageView iv_youhui;
    @BindView(R.id.tv_sao)
    TextView tv_sao;
    private VideoUtil videoUtil;
    private String TAG = "AdpicActivity";
    private String filepath;
    private String imagepath;
    private String videopath;
    RxPermissions rxPermissions;
    int garway_status1, garway_status2;
    private List<String> ad_urls = new ArrayList<>();
    List<AdData> adDatas = new ArrayList<>();
    static List<VideoUtil> videoUtils = new ArrayList<>();
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    int bit0, bit1, bit2, bit3, bit4, bit5, bit6, bit7, bit8, bit9, bit10, bit11, bit12, bit13, bit14, bit15, bit16, bit17,
            bit18, bit19, bit20, bit21;
    private boolean isError;
    boolean isgotea;
    static int autoCurrIndex = 0;
    private boolean press_refill;
    @BindView(R.id.viewpageree)
    ViewPager viewpageree;
    StringBuilder sb_tishi;
    public static INetEvent mINetEvent;
    private MQTTService mqttService;
    private MqttServiceConnection serviceConnection;
    int connect_request = 0;
    AlarmManager am;
    PendingIntent pendingIntent;
    StringBuilder fault_wx_sb = new StringBuilder();
    StringBuilder fault_reminder_sb = new StringBuilder();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    int face_mqtt;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Gson gson = new Gson();
                    if (adDatas != null) {
                        adDatas.clear();
                    } else {
                        adDatas = new ArrayList<>();
                    }
                    if (videoUtils != null) {
                        videoUtils.clear();
                    } else {
                        videoUtils = new ArrayList<>();
                    }
                    Log.e(TAG, "handleMessage: " + "执行线程池1");
                    Map<String, String> map = new HashMap<>();
                    map.put("appId", "APP0000001");
                    map.put("secret", "chaji20190505");
                    map.put("deviceNo", deviceNo);
                    map.put("adPositionId", "1");
                    String aa = CommonUtils.getGsonEsa(map);
                    RetrofitServiceManager.getAPIService().getAllAdString(aa).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new MyObserver<String>() {
                                @Override
                                public void onSuccess(String response) {
                                    String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, response);
                                    Log.e(TAG, "onSuccessss: "+decryStr );
                                    //第一步：先获取jsonobject对象
                                    com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(decryStr);
                                    //第二步：把对象转换成jsonArray数组
                                    boolean result = jsonObject1.getBoolean("result");
                                    JSONArray array = jsonObject1.getJSONArray("rows");
                                    //第三步：将字符串转成list集合
                                    adDatas = com.alibaba.fastjson.JSONObject.parseArray(array.toJSONString(), AdData.class);
                                    if (result){
                                        if (adDatas.size() > 0) {
                                            for (int i = 0; i < adDatas.size(); i++) {
                                                if (adDatas.get(i).getAdType().equals("1")) {
                                                    if (adDatas.get(i).getAdUrl().contains("http://") || adDatas.get(i).getAdUrl().contains("https://")) {
                                                        ad_urls.add(adDatas.get(i).getAdUrl());
                                                    } else {
                                                        String url = imagepath + adDatas.get(i).getAdUrl().replace("\\", "/");
                                                        ad_urls.add(url);
                                                    }
                                                } else if (adDatas.get(i).getAdType().equals("2")) {
                                                    if (adDatas.get(i).getAdUrl().contains("http://") || adDatas.get(i).getAdUrl().contains("https://")) {
                                                        ad_urls.add(adDatas.get(i).getAdUrl());
                                                    } else {
                                                        HttpProxyCacheServer proxy = ChajiAPP.getProxy(getApplicationContext());
                                                        String proxyUrl = proxy.getProxyUrl(videopath + adDatas.get(i).getAdUrl().replace("\\", "/"));
                                                        ad_urls.add(proxyUrl);
                                                        String data = gson.toJson(ad_urls);
                                                        SpUtils.put(AdpicActivity.this,"json_ads",data);

                                                    }
                                                }
                                                videoUtil = new VideoUtil();
                                                videoUtil.setAd_url(ad_urls.get(i));
                                                videoUtil.setAd_time(adDatas.get(i).getAdTime());
                                                if (adDatas.get(i).getAdQrcode() != null) {
                                                    videoUtil.setAd_qrcode(imagepath + adDatas.get(i).getAdQrcode().replace("\\", "/"));
                                                }
                                                videoUtils.add(videoUtil);
                                                if (videoUtils.size() > 0) {
                                                    setDataList(videoUtils);
                                                }
                                            }

                                        }
                                    }

                                }

                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                    String data = sp.getString("json_ads", "");
                                    Type listType = new TypeToken<List<String>>() {
                                    }.getType();
                                    List<String> list = gson.fromJson(data, listType);
                                    videoUtil = new VideoUtil();
                                    for (int i = 0;i<list.size();i++){
                                       videoUtil.setAd_url(list.get(i));
                                   }
                                    videoUtils.add(videoUtil);
                                    if (videoUtils.size() > 0) {
                                        setDataList(videoUtils);
                                    }
                                    Log.e(TAG,"获取错误的msg"+e.toString());

                                }


                            });

                    boolean reset = (boolean) SpUtils.get(AdpicActivity.this, "reset_clock", false);
                    Log.e(TAG, "布尔变了: " + reset);
                    if (!reset) {
                        initClock();
                    }
                    break;
                case 3:
//                    RxToast.warning("重启");
                    Log.e("重启设备","重启设备");
                    try {
                        Runtime.getRuntime().exec("su -c reboot");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    MainCommunicate.getInstance().change_normal();
                    editor.putBoolean("mode_type", false);
                    editor.apply();
                    break;
                case 6:
                    boolean mode_type = (boolean) SpUtils.get(AdpicActivity.this, "mode_type", false);
                    if (mode_type) {
                        //退出清洁模式指令
                        String user_id = (String) SpUtils.get(AdpicActivity.this, "userId", "");
                        Map<String, String> map1 = new HashMap<>();
                        map1.put("appId", "APP0000001");
                        map1.put("secret", "chaji20190505");
                        map1.put("deviceNo", deviceNo);
                        map1.put("deviceRunStatus", "1");
                        map1.put("userId", user_id);
                        String aa1 = CommonUtils.getGsonEsa(map1);
                        RetrofitServiceManager.getAPIService().cleaningDevice(aa1)
                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new MyObserver<String>() {


                                    @Override
                                    public void onSuccess(String s) {
                                        String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, s);
                                        //第一步：先获取jsonobject对象
                                        com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(decryStr);
                                        //第二步：把对象转换成jsonArray数组
                                        boolean result = jsonObject1.getBoolean("result");
                                        if (result) {
                                            MainCommunicate.getInstance().system_reset();
                                            handler.sendEmptyMessageDelayed(5, 6000);
                                        }
                                    }
                                });
                    }
                    break;
                case 7:
                    AppUtils.relaunchApp();
                    break;

            }
        }
    };

    private void initClock() {
        SpUtils.put(AdpicActivity.this, "reset_clock", true);
        String time_format = "04:00:00";
        String[] times = time_format.split(":");
        am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmResetBroadcast.class);
        intent.setAction("startAlarm");
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar calendar = Calendar.getInstance();
        //是设置日历的时间，主要是让日历的年月日和当前同步
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(times[1]));
        calendar.set(Calendar.SECOND, Integer.parseInt(times[2]));
        //获取当前毫秒值,如果时间大于现在，则加一天
        long systemTime = System.currentTimeMillis();
        long calTime = calendar.getTimeInMillis();
        if (systemTime > calTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        SpUtils.put(this,"calTime",calendar.getTimeInMillis());
        Log.e("获取时间戳", String.valueOf(calendar.getTimeInMillis()));
    }

    private boolean rufund_result;
    private Integer wendu_shi;
    private boolean wendu_flag;
    private boolean isupDate;
    private boolean net_flag;
    private boolean mqtt_connect = false;
//    private boolean goucha_speak = false;
//    private boolean shuiwen = false;


    private void setDataList(List<VideoUtil> videoUtils) {
        FragmentPagerAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), videoUtils);
        viewpageree.setAdapter(adapter);
        viewpageree.setCurrentItem(autoCurrIndex);
        Fragment1.newInstance(videoUtils).updateDate(autoCurrIndex);
        String youhui_url = videoUtils.get(autoCurrIndex).getAd_qrcode();
        Log.e(TAG, "二维码地址: " + youhui_url);
        if (youhui_url != null) {
            iv_youhui.setVisibility(View.VISIBLE);
            tv_sao.setVisibility(View.VISIBLE);
            Glide.with(AdpicActivity.this).load(youhui_url).into(iv_youhui);
        } else {
            tv_sao.setVisibility(View.INVISIBLE);
            iv_youhui.setVisibility(View.INVISIBLE);
        }
        viewpageree.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Fragment1.newInstance(videoUtils).updateDate(i);
                String youhui_url = videoUtils.get(i).getAd_qrcode();
                if (youhui_url != null) {
                    Log.e(TAG, "优惠二维码: " + youhui_url);
                    iv_youhui.setVisibility(View.VISIBLE);
                    tv_sao.setVisibility(View.VISIBLE);
                    Glide.with(AdpicActivity.this).load(youhui_url).into(iv_youhui);
                } else {
                    iv_youhui.setVisibility(View.INVISIBLE);
                    tv_sao.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_adpic;
    }


    public void gocurret(int i) {
        Log.e(TAG, "gocurret: " + i);
        viewpageree.setCurrentItem(i);
        autoCurrIndex = i;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initEventAndData() {
        serviceConnection = new MqttServiceConnection();
        Intent intent = new Intent(this, MQTTService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        ActivityController.getInstance().addActivity(this);
        initMqttlistener();
        mINetEvent = this;
        net_flag = (boolean) SpUtils.get(AdpicActivity.this, "net_flag", false);
        videopath = Contact.p_url + File.separator + "file" + File.separator;
        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "chaji" + File.separator;
        imagepath = Contact.p_url + File.separator + "file" + File.separator;
        Log.e(TAG, "initEventAndData: " + "执行hander之前");
        handler.sendEmptyMessage(1);
        firstRun();


//        RxView.clicks(btn_click_me).throttleFirst(3, TimeUnit.SECONDS)
//                .subscribe(o -> go_choose());

        btn_click_me.setOnLongClickListener(v -> {
            if (isgotea || isError || !wendu_flag) {
                Intent i = new Intent(AdpicActivity.this, OpsActivity.class);
                startActivity(i);
            } else {
                //不做任何操作
            }
            return false;
        });
    }

    private void initMqttlistener() {
        mqttService = MQTTService.getInstance(new MQTTService.MqttCallbacks() {
            @Override
            public void subscribedSuccess(String message) {
                connect_request = 0;
//                mqtt_connect = true;
                SpUtils.put(AdpicActivity.this,"mqtt_connect",true);
                Log.e(TAG, "subscribedSuccess: " + message);
//                RxToast.normal("subscribedSuccess" + mqtt_connect);
//                editor.putBoolean("mqtt_connect", true);
//                editor.apply();
                LogsFileUtil.getInstance().addLog("mqtt通信请求连接", message);
            }

            @Override
            public void connectFail(String message) {
                SpUtils.put(AdpicActivity.this,"mqtt_connect",false);
                LogsFileUtil.getInstance().addLog("mqtt通信请求连接失败", message);
            }

            @Override
            public void connectRequest() {
//                connect_request++;
//                Log.e(TAG, "connectRequest: "+connect_request );
//                if (connect_request == 20) {
//                    handler.sendEmptyMessage(3);
//                }
            }

            @Override
            public void connectLost(String message) {
                SpUtils.put(AdpicActivity.this,"mqtt_connect",false);
                LogsFileUtil.getInstance().addLog("mqtt通信过程中断", message);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void firstRun() {
//        handler.sendEmptyMessageDelayed(6, 1000);
    }

    private void go_choose() {
        if (!isgotea) {
            TTSUtils.getInstance().pauseSpeech();
            if (!wendu_flag) {
                TTSUtils.getInstance().speak("当前水温不够,请稍作等待");
            } else {
                TTSUtils.getInstance().speak("进入购茶");
                Intent intent = new Intent(this, BuyGoodsActivity.class);
                startActivity(intent);
            }
        } else {
            TTSUtils.getInstance().pauseSpeech();
            TTSUtils.getInstance().speak("机器故障现在不可购买");
            RxToast.normal("机器故障现在不可购买");
        }

    }

    //退出的时候保存状态
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onResume() {
        isgotea = false;
        if (!MainCommunicate.serialPortStatus) {
            //打开串口
            MainCommunicate.getInstance().openSerialPort();
        }
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
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        TTSUtils.getInstance().pauseSpeech();
        super.onPause();
    }

    @Override
    protected void initUI() {
        press_refill = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        if (MainCommunicate.serialPortStatus) {
            MainCommunicate.getInstance().closeSerialPort();
        }
        unbindService(serviceConnection);
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
            String wendu = msg.getErr_code().substring(8, 10);
            wendu_shi = Integer.valueOf(wendu, 16);
            int tmp = (int) SpUtils.get(AdpicActivity.this, "hotWaterTemperature", 77);
            wendu_flag = wendu_shi >= tmp;
//            Log.e("=======", "getmsg1: " + "获得串口");
            String warn1 = msg.getInscontent1();
            String warn2 = msg.getInscontent2();
            String warn3 = msg.getInscontent3();
            if (warn2.substring(6, 7).equals("1")) {
                if (!water_low) {
                    fault_reminder_sb.append("内置水箱水位低。");
                }
                bit9 = 1;
            } else {
                bit9 = 0;
            }
            if (warn2.substring(7, 8).equals("1")) {
                bit8 = 1;
                if (!heating_failure) {
                    fault_reminder_sb.append("加热故障,加热管不加热。");
                }
            } else {
                bit8 = 0;
            }
            if (warn2.substring(4, 5).equals("1")) {
                if (!water_used_all) {
                    fault_reminder_sb.append("所有桶的水都用完了。");
                }
                Log.e(TAG, "警告二分指令: " + "所有桶用完" + bit11);
                bit11 = 1;
            } else {
                bit11 = 0;
            }
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
                if (!door_failure) {
                    fault_reminder_sb.append("取茶口隔离门无法关闭。");
                }
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
                if (!cup_failure_b) {
                    fault_reminder_sb.append("落杯器B卡杯故障。");
                }
                bit4 = 1;
                Log.e(TAG, "警告三分指令: " + "落杯器2卡杯故障");
            } else {
                bit4 = 0;
            }
            if (warn3.substring(4, 5).equals("1")) {
                if (!cup_failure_a) {
                    fault_reminder_sb.append("落杯器A卡杯故障。");
                }
                bit3 = 1;
                Log.e(TAG, "警告三分指令: " + "落杯器1卡杯故障");
            } else {
                bit3 = 0;
            }
            if (bit3 == 1 && bit4 == 1) {
                if (!cup_failure_all) {
                    fault_reminder_sb.append("两个落杯器都卡杯故障了。");
                }
            }
            if (warn3.substring(5, 6).equals("1")) {
//                if (!no_cup_b) {
//                    fault_wx_sb.append("货道B无杯。");
//                }
                bit2 = 1;
                garway_status2 = 1;
            } else {
                garway_status2 = 0;
                bit2 = 0;
            }
            if (warn3.substring(6, 7).equals("1")) {
//                if (!no_cup_a) {
//                    fault_wx_sb.append("货道A无杯。");
//                }
                bit1 = 1;
                garway_status1 = 1;
            } else {
                garway_status1 = 0;
                bit1 = 0;
            }
            if (bit1 == 1 && bit2 == 1) {
                if (!no_cup_all) {
                    fault_reminder_sb.append("货道A和货道B都没杯了。");
//                    fault_wx_sb.append("货道A和货道B都没杯了。");
//                    FaultReminder("货道A和货道B都没杯了");
                }
            }
            if (warn3.substring(6, 7).equals("1") && warn3.substring(5, 6).equals("1")) {
            }
            if (warn1.substring(0, 1).equals("1")) {

            } else {
            }
            if (warn1.substring(1, 2).equals("1")) {
                //1-为按下续水按键，此时界面应该显示为续水界面
                if (NetworkUtils.isConnected()) {
                    if (!press_refill) {
//                    MainCommunicate.getInstance().normal_refill();
                        if (!isgotea && wendu_flag) {
                            Intent i = new Intent(AdpicActivity.this, RefillPayActivity.class);
                            startActivity(i);
                        } else {
                            if (!wendu_flag) {
                                TTSUtils.getInstance().speak("当前水温不够,请稍作等待");
                            }
                            RxToast.normal("该商品现在不可购买");
                        }
                        press_refill = true;
                    }
                }else {
                    RxToast.normal("网络不可用");
                }
            }
            if (warn1.substring(2, 3).equals("1")) {
//                Log.e(TAG, "警告一分指令: " + "用户取杯完成");
                bit21 = 1;
            } else {
                bit21 = 0;
            }
            if (warn1.substring(3, 4).equals("1")) {
                Log.e(TAG, "警告一分指令: " + "送杯完成");
                bit20 = 1;
            } else {
                bit20 = 0;
            }
            if (warn1.substring(4, 5).equals("1")) {
                bit19 = 1;
                Log.e(TAG, "警告一分指令: " + "加水完成，开始送杯");
            } else {
                bit19 = 0;
            }
            if (warn1.substring(5, 6).equals("1")) {
                bit18 = 1;
                Log.e(TAG, "警告一分指令: " + "蓄水接杯完成");
            } else {
                bit18 = 0;
            }
            if (warn1.substring(6, 7).equals("1")) {
                bit17 = 1;
                if (!guizi_open) {
                    fault_wx_sb.append("储物柜柜门没有关闭。");
                }
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

            if (warn2.substring(5, 6).equals("1")) {
//                Log.e(TAG, "警告二分指令: " + "1桶用完" + bit10);
//                if (!water_used_a){
//                    FaultWx("1桶的水用完了。");
//                }
                if (!water_used_a) {
                    fault_wx_sb.append("1桶的水用完了。");
                }
                bit10 = 1;
            } else {
                bit10 = 0;
            }

            StringBuilder ss = new StringBuilder();
            for (int i = 0; i < 24; i++) {
                ss.append("0");
            }
            if (ss.toString().equals(warn1 + warn2 + warn3)) {
                isError = false;
                isgotea = false;
                initFalutStatus();
            }
//            sendError();
            canbuyErrror();


            if ( !guizi_open || !water_used_a
            || !heating_failure || !cup_failure_a || !cup_failure_b || !cup_failure_all || !water_used_all
                    || !door_failure || !no_cup_all || !water_low) {
//                if (fault_wx_sb.toString().contains("货道B无杯")) {
//                    no_cup_b = true;
//                }
//                if (fault_wx_sb.toString().contains("货道A无杯")) {
//                    no_cup_a = true;
//                }
                if (fault_wx_sb.toString().contains("储物柜柜门没有关闭")) {
                    guizi_open = true;
                }
                if (fault_wx_sb.toString().contains("1桶的水用完了")) {
                    water_used_a = true;
                }
                if (fault_reminder_sb.toString().contains("加热故障,加热管不加热")) {
                    heating_failure = true;
                }
                if (fault_reminder_sb.toString().contains("内置水箱水位低")) {
                    water_low = true;
                }
                if (fault_reminder_sb.toString().contains("所有桶的水都用完了")) {
                    water_used_all = true;
                }
                if (fault_reminder_sb.toString().contains("货道A和货道B都没杯了")) {
                    no_cup_all = true;
                }
                if (fault_reminder_sb.toString().contains("取茶口隔离门无法关闭")) {
                    door_failure = true;
                }
                if (fault_reminder_sb.toString().contains("落杯器A卡杯故障")) {
                    cup_failure_a = true;
                }
                if (fault_reminder_sb.toString().contains("落杯器B卡杯故障")) {
                    cup_failure_b = true;
                }
                if (fault_reminder_sb.toString().contains("两个落杯器都卡杯故障了")) {
                    cup_failure_all = true;
                }
                if (fault_reminder_sb.toString().length() > 0) {
                    Log.e(TAG, "getmsg1: "+"发送错误"+fault_reminder_sb.toString() );
                    FaultReminder(fault_reminder_sb.toString());
                }
//                LogsFileUtil.getInstance().addLog("发送前补货通知",fault_wx_sb.toString());
//                LogsFileUtil.getInstance().addLog("发送前故障提醒",fault_reminder_sb.toString());
                if (fault_wx_sb.toString().length() > 0 || fault_reminder_sb.toString().length() >0) {
                    Log.e(TAG, "getmsg1: "+"发送微信"+fault_reminder_sb.toString() +fault_wx_sb.toString());
                    FaultWx(fault_wx_sb.toString(),fault_reminder_sb.toString());
                }
                fault_wx_sb.delete(0, fault_wx_sb.length());
                fault_reminder_sb.delete(0, fault_reminder_sb.length());

            }


//            if (!heating_failure || !cup_failure_a || !cup_failure_b || !cup_failure_all || !water_used_all
//                    || !door_failure || !no_cup_all || !water_low) {
//
//
//
//            }

        }

    }


    private void canbuyErrror() {
//        shuiwen = false;
//        goucha_speak = false;
        sb_tishi = new StringBuilder();
        if (bit1 == 1 && bit2 == 1) {
            isgotea = true;
            sb_tishi.append("货道A和货道B无杯。");
        }
        if (bit3 == 1) {
            sb_tishi.append("落杯器A卡杯故障。");
        }
        if (bit4 == 1 && bit3 == 1) {
            //两个都卡杯的情况下不能购买
            isgotea = true;
        }
        if (bit4 == 1) {
            sb_tishi.append("落杯器B卡杯故障。");
        }
        if (bit5 == 1) {
            isgotea = true;
            //取茶口隔离门未关闭
        }
        if (bit8 == 1) {
            isgotea = true;
//            sb_tishi.append("加热管不加热。");
        }
        if (bit9 == 1) {
            isgotea = true;
//            sb_tishi.append("内置水箱水位低。");
        }
        if (bit11 == 1) {
            isgotea = true;
            sb_tishi.append("两个水桶的水都用完了。");
        }
        if (bit16 == 1) {
            isgotea = true;
            sb_tishi.append("水路控制与落杯控制板通信故障。");
        }
        //储物柜提示放到微信提醒中
//        if (bit17 == 1) {
//            sb_tishi.append("储物柜没有关闭。");
//        }
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
                        String result_far;
                        result_far = CommonUtils.fileSize((long) soFarBytes);
                        String result_total;
                        result_total = CommonUtils.fileSize((long) totalBytes);
                        RxToast.normal("程序升级中..总共需要更新" + result_total + "，已经更新" + result_far);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Log.e(TAG, "completed: " + "apk下载完成");
                        isupDate = false;
                        RxToast.normal("apk下载完成");
                        boolean is = Install.isRoot();
                        if (is) {
//                            RxToast.normal("拥有root权限");
                            Log.e(TAG, "completed: " + "静默安装");
//                            Install.copyApkFromAssets(ProductActivity.this, "茶机", filepath+"茶机.apk");
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
                        Log.e(TAG, "paused: " + "暂停下载");

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
//                        RxToast.normal("出错了" + e.toString());
                        Log.e(TAG, "error: " + "报错体系");
                        Log.e(TAG, "paused: " + e.toString());
                        updataError();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                }).start();
    }

    private void updataError() {
        String updateVersionId = (String) SpUtils.get(AdpicActivity.this, "updateVersionId", "");
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
                            btn_click_me.setEnabled(true);
                        }
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    //在ui线程执行 优先级100
    public void getmsg(Mqttmessages msg) {
        if (msg.getType() != null) {
            switch (msg.getType()) {
                case "4":
                    Log.e(TAG, "getmsg: " + "通知11");
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
//            Toast.makeText(this,"目标：AdpicActivity",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(this, MakeTeaActivity.class);
                    i.putExtra("order_no", order_no);
                    i.putExtra("orderAmount", orderAmount);
                    i.putExtra("preAmount", preAmount);
                    startActivity(i);
                    break;
                case "3":
                    TTSUtils.getInstance().speak("支付超时,续杯失败");
                    RxToast.normal("支付超时，续杯失败");
                    break;
                case "6":
                    RxToast.normal("系统即将在10秒钟后重启");
                    handler.sendEmptyMessageDelayed(3, 10000);
                    break;
            }
        }
    }

    private void updata(Mqttmessages msg) {
        Log.e(TAG, "updata: " + "222");
//        shuiwen = false;
//        goucha_speak = false;
        isupDate = true;
        TTSUtils.getInstance().speak("程序升级中,现在暂时不能做购茶操作");
        btn_click_me.setEnabled(false);
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
                            TTSUtils.getInstance().speak("出茶失败,已经自动退款");
                            RxToast.normal("出茶失败,已经自动退款");
                        } else {
                            rufund_result = false;
                        }
                    }

                });
        return rufund_result;
    }


    /**
     * 点我点击事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (NetworkUtils.isConnected()) {
                if (!isupDate) {
                    boolean connect = (boolean) SpUtils.get(AdpicActivity.this,"mqtt_connect",true);
                    if (connect) {
                        go_choose();
                    } else {
                        RxToast.warning("网络连接状况不好,请稍后再试");
                    }
                }
            } else {
                TTSUtils.getInstance().speak("没有可用网络,请检查网络");
                RxToast.warning("没有可用网络,请检查网络");
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    @Override
    public void onNetChange(int netWorkState) {
        switch (netWorkState) {
            case NetUtils.NETWORK_NONE:
                break;
            case NetUtils.NETWORK_MOBILE:
                Log.e(TAG, "onNetChange: "+"移动网络可用" );
                boolean m_net = (boolean) SpUtils.get(AdpicActivity.this,"net_flag",false);
                if (!m_net){
                    RxToast.error("检测到网络可用,准备重启app");
                    handler.sendEmptyMessageDelayed(7,3000);
                }
                break;
        }

    }


    /**
     * 故障提醒
     */
    //加热故障标志位
    boolean heating_failure = false;
    //落杯器卡杯故障标志位a
    boolean cup_failure_a = false;
    //落杯器卡杯故障标志位b
    boolean cup_failure_b = false;
    //落杯器都卡杯故障标志位
    boolean cup_failure_all = false;
    //两桶水用完标志位,用完为0,用完1桶为1,用完2桶为2，1桶确认为3，2桶确认为4
    boolean water_used_a = false;
    //
    boolean water_used_b = false;
    //
    boolean water_used_all = false;

    //取茶口隔离无法关闭
    boolean door_failure = false;
    //A货道无杯,1桶用完为1，2桶用完为2，1桶已经确认为3，2桶已经确认发送故障为4
    boolean no_cup_a = false;
    //b货道无杯
    boolean no_cup_b = false;
    //所有货道无杯
    boolean no_cup_all = false;
    //内置水箱水位低
    boolean water_low = false;


    private void initFalutStatus() {
        heating_failure = false;
        cup_failure_a = false;
        cup_failure_b = false;
        cup_failure_all = false;
        water_used_all = false;
//        water_used_b = false;
        door_failure = false;
        no_cup_a = false;
        no_cup_b = false;
        no_cup_all = false;
        water_low = false;

        guizi_open = false;
        water_used_a = false;
    }

    private void FaultReminder(String content) {
        LogsFileUtil.getInstance().addLog("故障提醒",content);
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
                        LogsFileUtil.getInstance().addLog("故障返回提醒",ECBAESUtils.decrypt(Constants.AES_KEY, response));
                        Log.e("故障提醒","故障提醒"+ECBAESUtils.decrypt(Constants.AES_KEY, response));
                    }
                });

    }

    boolean guizi_open = false;

    private void FaultWx(String wxcontent,String errorcontent) {
        LogsFileUtil.getInstance().addLog("微信提醒补货通知",wxcontent);
        LogsFileUtil.getInstance().addLog("微信提醒故障通知",errorcontent);
            Map<String, String> map = new HashMap<>();
            map.put("appId", "APP0000001");
            map.put("secret", "chaji20190505");
            map.put("deviceNo", deviceNo);
            map.put("waterNumber", wxcontent);
            map.put("faultCause", errorcontent);
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
