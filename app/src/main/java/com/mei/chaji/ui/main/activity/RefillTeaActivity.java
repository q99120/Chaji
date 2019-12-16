package com.mei.chaji.ui.main.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.bumptech.glide.Glide;
import com.mei.chaji.R;
import com.mei.chaji.app.Constants;
import com.mei.chaji.app.Contact;
import com.mei.chaji.base.activity.BaseActivity;
import com.mei.chaji.component.ActivityController;
import com.mei.chaji.core.bean.main.AdData;
import com.mei.chaji.core.bean.main.VideoInfo;
import com.mei.chaji.core.bean.main.VideoUtil;
import com.mei.chaji.core.bean.msg.InsMessage;
import com.mei.chaji.core.http.api.RetrofitServiceManager;
import com.mei.chaji.ui.main.view.GlideImageLoader;
import com.mei.chaji.utils.CommonUtils;
import com.mei.chaji.utils.ECBAESUtils;
import com.mei.chaji.utils.TTSUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import pl.droidsonroids.gif.GifImageView;

public class RefillTeaActivity extends BaseActivity {
    List<VideoInfo> videos = new ArrayList<>();
    int i_flag;
    @BindView(R.id.iv_youhui)
    ImageView iv_youhui;
    @BindView(R.id.tv_sao)
    TextView tv_sao;
    //    @BindView(R.id.banner)
//    Banners banners;
    @BindView(R.id.into_gif)
    GifImageView gifImageView;
    @BindView(R.id.refill_status)
    TextView tv_refill_status;
    String TAG = "MakeTeaActivity";
    private String filepath;
    private String imagepath;
    private String videopath;
    RxPermissions rxPermissions;
    int garway_status1, garway_status2;
    private List<String> ad_urls = new ArrayList<>();
    List<AdData> adDatas = new ArrayList<>();
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    int bit0, bit1, bit2, bit3, bit4, bit5, bit6, bit7, bit8, bit9, bit10, bit11, bit12, bit13, bit14, bit15, bit16, bit17,
            bit18, bit19, bit20, bit21;

    List<String> images = new ArrayList<>();
    List<String> youhui_url = new ArrayList<>();
    @BindView(R.id.viewpageree)
    Banner banner;


    boolean refill_ing, refill_finish;

    /**
     * 判断茶机状态
     */
    private boolean make_pre, make_cup, make_finish, make_pick;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Map<String, String> map = new HashMap<>();
                    map.put("appId", "APP0000001");
                    map.put("secret", "chaji20190505");
                    map.put("deviceNo", deviceNo);
                    map.put("adPositionId", "5");
                    String aa = CommonUtils.getGsonEsa(map);
                    RetrofitServiceManager.getAPIService().getAllAdString(aa).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe((Consumer<? super String>) s -> {
                                if (adDatas != null) {
                                    adDatas.clear();
                                } else {
                                    adDatas = new ArrayList<>();
                                }
                                VideoUtil videoUtil;
                                String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, s);
                                com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(decryStr);
                                boolean rusult = jsonObject1.getBoolean("result");
                                JSONArray array = jsonObject1.getJSONArray("rows");
                                //第三步：将字符串转成list集合
                                adDatas = com.alibaba.fastjson.JSONObject.parseArray(array.toJSONString(), AdData.class);//把字符串转换成集合
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
                            });
                    break;
                case 1001:
                    gobuy();
                    break;
            }
        }
    };
    private int list_flag;
    private boolean press_refill;

    private void initBanner() {
        Glide.with(RefillTeaActivity.this).load(youhui_url.get(0)).into(iv_youhui);
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
                    Glide.with(RefillTeaActivity.this).load(yo_url).into(iv_youhui);
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

    private Runnable stop_run = () -> gobuy();

    private void gobuy() {
        Intent i = new Intent(RefillTeaActivity.this, BuyGoodsActivity.class);
        startActivity(i);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refill;
    }


    @SuppressLint("CheckResult")
    @Override
    protected void initEventAndData() {
        ActivityController.getInstance().addActivity(this);
        TTSUtils.getInstance().pauseSpeech();
        TTSUtils.getInstance().speak("请把杯子放入杯托..");
        beizi_timer.start();
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
    }

    /**
     * 倒计时30秒，一次1秒
     */
    CountDownTimer beizi_timer = new CountDownTimer(30 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
            if (tv_refill_status != null) {
//                TTSUtils.getInstance().speak(millisUntilFinished / 1000+"秒");
                tv_refill_status.setText("请把杯子放入杯托 ( " + millisUntilFinished / 1000 + " ) S");
            }
        }

        @Override
        public void onFinish() {
            handler.postDelayed(stop_run, 500);
        }
    };

    @SuppressLint("CheckResult")
    @Override
    protected void onResume() {
        refill_ing = false;
        refill_finish = false;
        make_cup = false;
        make_pre = false;
        make_finish = false;
        make_pick = false;
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
        Log.e(TAG, "onPause: " + "生命周期");
        TTSUtils.getInstance().pauseSpeech();
        banner.stopAutoPlay();
        beizi_timer.cancel();
        handler.removeMessages(1001);
        if (EventBus.getDefault().isRegistered(this)) {
            Log.e(TAG, "onDestroy: " + "eventbus解绑");
            EventBus.getDefault().unregister(this);
        }
        super.onPause();
    }

    @Override
    protected void initUI() {
        Typeface textfont = Typeface.createFromAsset(getAssets(), "zhongheiti.ttf");
        tv_refill_status.setTypeface(textfont);
        press_refill = false;

    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: " + "");
        super.onDestroy();
    }

    /**
     * 取杯完成后过5秒回到购买界面
     */
    private Runnable buyrunn = () -> handler.sendEmptyMessage(1001);


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
            Log.e(TAG, "警告一的状态 " + warn1 + warn2 + warn3);
            if (warn3.substring(7, 8).equals("1")) {
                bit0 = 1;
//                Log.e(TAG, "警告三分指令: " + "忙");
            } else {
                bit0 = 0;
                if (make_pick) {
                    handler.sendEmptyMessageDelayed(1001, 500);
                }
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
            if (warn3.substring(6, 7).equals("1") && warn3.substring(5, 6).equals("1")) {
            }
            if (warn1.substring(0, 1).equals("1")) {

            } else {
            }
            if (warn1.substring(1, 2).equals("1")) {
                //1-为按下续水按键，此时界面应该显示为续水界面
                if (!press_refill) {
                    press_refill = true;
                }
            }
            if (warn1.substring(2, 3).equals("1")) {
                if (!make_pick) {
                    tv_refill_status.setText("取杯完成");
                    TTSUtils.getInstance().pauseSpeech();
                    TTSUtils.getInstance().speak("取杯完成，欢迎下次光临");
                    handler.removeMessages(1001);
                    make_pick = true;
                }
            }
            if (warn1.substring(3, 4).equals("1")) {
                if (!make_finish) {
                    TTSUtils.getInstance().pauseSpeech();
                    TTSUtils.getInstance().speak("送杯完成，请取杯");
                    tv_refill_status.setText("送杯完成");
                    handler.removeMessages(1001);
                    handler.sendEmptyMessageDelayed(1001, 15000);
                    make_finish = true;
                }
            }
            if (warn1.substring(4, 5).equals("1")) {
                if (!make_cup) {
                    TTSUtils.getInstance().pauseSpeech();
                    TTSUtils.getInstance().speak("加水完成，开始送杯,小心烫伤");
                    tv_refill_status.setText("制作完成");
                    handler.sendEmptyMessageDelayed(1001, 15000);
                    make_cup = true;
                }
            }
            if (warn1.substring(5, 6).equals("1")) {
                if (!refill_ing) {
                    Log.e(TAG, "getmsg1: " + "哈哈哈");
                    bit18 = 1;
                    beizi_timer.cancel();
                    refill_ing = true;
                    TTSUtils.getInstance().pauseSpeech();
                    TTSUtils.getInstance().speak("长按出水键为您续杯，如果您已经按下出水键，请耐心等待，松开按键后请稍等，将自动送杯");
                    if (tv_refill_status != null) {
                        tv_refill_status.setText("长按出水键为你续杯");
                    }
                    if (gifImageView != null) {
                        gifImageView.setImageResource(R.mipmap.make_teacup);
                    }
                    Log.e(TAG, "警告一分指令: " + "蓄水接杯完成");
                }
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
                Log.e(TAG, "警告二分指令: " + "停电报警");
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

}
