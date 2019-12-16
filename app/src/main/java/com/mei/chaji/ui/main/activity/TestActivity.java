package com.mei.chaji.ui.main.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.mei.chaji.R;
import com.mei.chaji.base.activity.BaseActivity;
import com.mei.chaji.component.ActivityController;
import com.mei.chaji.core.bean.msg.InsMessage;
import com.mei.chaji.instruction.MainCommunicate;
import com.mei.chaji.service.MQTTService;
import com.mei.chaji.utils.TTSUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.mei.chaji.app.Constants.Permissions;

public class TestActivity extends BaseActivity  {
    @BindView(R.id.a_cup)
    Button a_cup;
    @BindView(R.id.b_cup)
    Button b_cup;
    @BindView(R.id.a_cup_hot)
    Button a_cup_hot;
    @BindView(R.id.a_cup_cold)
    Button a_cup_cold;
    @BindView(R.id.b_cup_hot)
    Button b_cup_hot;
    @BindView(R.id.b_cup_cold)
    Button b_cup_cold;
    @BindView(R.id.auto_open)
    Button auto_luobei;
    @BindView(R.id.auto_close)
    Button stop_auto_luobei;
    @BindView(R.id.tv_luobei_num)
    TextView tv_luobei_num;
    @BindView(R.id.clear_luobei)
    TextView clear_luobei;
    @BindView(R.id.iv_back)
    ImageView iv_back;


    @BindView(R.id.tv_tmp)
    TextView tv_tmp;
    @BindView(R.id.tv_send_ins)
    TextView tv_send_ins;
    @BindView(R.id.tv_back_ins)
    TextView tv_back_ins;
    @BindView(R.id.tv_warn1)
    TextView tv_warn1;
    @BindView(R.id.tv_warn2)
    TextView tv_warn2;
    @BindView(R.id.tv_warn3)
    TextView tv_warn3;
    @BindView(R.id.tv_error)
    TextView tv_error;


    @BindView(R.id.clear_mode)
    Button clear_mode;
    @BindView(R.id.reset_mode)
    Button reset_mode;
    @BindView(R.id.normal_mode)
    Button normal_mode;
    @BindView(R.id.open_one_hands)
    Button open_one_hands;
    @BindView(R.id.close_one_hands)
    TextView close_one_hands;
    @BindView(R.id.open_two_hands)
    Button open_two_hands;
    @BindView(R.id.close_two_hands)
    TextView close_two_hands;
    int flag = 0;
    int lll = 0;
    boolean aa;//是否取杯

    int bit1, bit2;
    private String TAG = "TestActivity";

    public static final int RC_CAMERA_AND_STORAGE = 120;

    @SuppressLint("CheckResult")
    @Override
    protected void initUI() {


    }

    @Override
    protected int getLayoutId() {
        return R.layout.fm_test;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initEventAndData() {
        ActivityController.getInstance().addActivity(this);
//        initPermission();

        RxView.clicks(a_cup).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o ->
                {

                    tv_send_ins.setText("发送指令:" + "A货道落杯测试");
                    MainCommunicate.getInstance().normal_onlycup(1);
                });
        RxView.clicks(b_cup).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    tv_send_ins.setText("发送指令:" + "B货道落杯测试");
                    MainCommunicate.getInstance().normal_onlycup(2);
                });
        RxView.clicks(a_cup_hot).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    tv_send_ins.setText("发送指令:" + "A货道热水测试");
                    MainCommunicate.getInstance().test_water(1, 1);
                });
        RxView.clicks(a_cup_cold).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    tv_send_ins.setText("发送指令:" + "A货道冷水测试");
                    MainCommunicate.getInstance().test_water(1, 2);
                });
        RxView.clicks(b_cup_hot).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    tv_send_ins.setText("发送指令:" + "B货道热水测试");
                    MainCommunicate.getInstance().test_water(2, 1);
                });
        RxView.clicks(b_cup_cold).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    tv_send_ins.setText("发送指令:" + "B货道冷水测试");
                    MainCommunicate.getInstance().test_water(2, 2);
                });
        RxView.clicks(auto_luobei).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    Log.e(TAG, "initEventAndData: " + bit1 + bit2);
                    if (bit1 == 0) {
                        MainCommunicate.getInstance().normal_onlycup(1);
                        tv_send_ins.setText("发送指令:" + "A货道自动落杯中..");
                    } else {
                        MainCommunicate.getInstance().normal_onlycup(2);
                        tv_send_ins.setText("发送指令:" + "B货道自动落杯中..");
                    }
                    if (bit1 == 1 && bit2 == 1) {
                        tv_send_ins.setText("发送指令:" + "两个货道都没杯了");
                    }
                    flag = 1;
                });
        RxView.clicks(stop_auto_luobei).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    tv_send_ins.setText("发送指令:" + "自动落杯已经关闭..");
                    flag = 0;
                });

        RxView.clicks(clear_mode).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    tv_send_ins.setText("发送指令:" + "进入清洁模式..");
                    MainCommunicate.getInstance().change_clear();
                });
        RxView.clicks(reset_mode).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    tv_send_ins.setText("发送指令:" + "复位..");
                    MainCommunicate.getInstance().system_reset();
                });
        RxView.clicks(normal_mode).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    tv_send_ins.setText("发送指令:" + "正常模式..");
                    MainCommunicate.getInstance().change_normal();
                });
        RxView.clicks(open_one_hands).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    tv_send_ins.setText("发送指令:" + "爪A打开..");
                    MainCommunicate.getInstance().repair_hand1(1);
                });

        RxView.clicks(close_one_hands).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    tv_send_ins.setText("发送指令:" + "爪A关闭..");
                    MainCommunicate.getInstance().repair_hand1(0);
                });
        RxView.clicks(open_two_hands).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    tv_send_ins.setText("发送指令:" + "爪B打开..");
                    MainCommunicate.getInstance().repair_hand2(1);
                });

        RxView.clicks(close_two_hands).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    tv_send_ins.setText("发送指令:" + "爪B关闭..");
                    MainCommunicate.getInstance().repair_hand2(0);
                });
        RxView.clicks(clear_luobei).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    lll = 0;
                    if (tv_luobei_num != null) {
                        tv_luobei_num.setText("落杯数量：" + lll);
                    }
                });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TestActivity.this, OpsActivity.class);
                i.putExtra("test_flag", 1);
                startActivity(i);
            }
        });
    }


    /**
     * 第一件事情请求权限
     */
    @AfterPermissionGranted(RC_CAMERA_AND_STORAGE)
    public void initPermission() {
        if (EasyPermissions.hasPermissions(this, Permissions)) {

        } else {
            EasyPermissions.requestPermissions(this, "需要摄像头权限以及存储权限",
                    RC_CAMERA_AND_STORAGE, Permissions);
        }

    }

    @SuppressLint("CheckResult")
    @Override
    protected void onResume() {


        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
//        if (!MainCommunicate.serialPortStatus) {
//            MainCommunicate.getInstance().openSerialPort();
//        }
//        EventBus.getDefault().post(new Mqttmessages("111",true));
        super.onResume();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }


    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    //在ui线程执行 优先级100
    public void getmsg1(InsMessage msg) {
        if (msg.getInsmsg().equals("ins_success")) {
            tv_back_ins.setText("返回指令:" + msg.getErr_code());
            String wendu = msg.getErr_code().substring(8, 10);
            String wendu_shi = String.valueOf(Integer.valueOf(wendu, 16));
            tv_tmp.setText("温度:" + wendu_shi + "°C");
//            Log.e("=======", "getmsg1: " + "获得串口");
            String warn1 = msg.getInscontent1();
            String warn2 = msg.getInscontent2();
            String warn3 = msg.getInscontent3();
            tv_warn1.setText("23bit-16bit:" + warn1);
            tv_warn2.setText("15bit-8bit:" + warn2);
            tv_warn3.setText("7bit-0bit:" + warn3);
            StringBuilder ss = new StringBuilder();
            if (warn3.substring(7, 8).equals("1")) {
                ss.append("机器处于忙碌中。");
            } else {
                ss.append("");
                if (aa) {
                    Log.e(TAG, "getmsg1: " + "机器不忙");
                    if (flag == 1) {
                        handler.sendEmptyMessageDelayed(1, 1000);
                    }
                }
            }
            if (warn3.substring(0, 1).equals("1")) {
            }
            if (warn3.substring(1, 2).equals("1")) {
            }
            if (warn3.substring(2, 3).equals("1")) {
            }
            if (warn3.substring(2, 3).equals("0")) {
//                Log.e(TAG, "getmsg1: " + bit5);

            }
            if (warn3.substring(3, 4).equals("1")) {
                ss.append("落杯器B卡杯故障。");
            } else {
                ss.append("");
            }
            if (warn3.substring(4, 5).equals("1")) {
                ss.append("落杯器A卡杯故障。");
            } else {
                ss.append("");
            }
            if (warn3.substring(5, 6).equals("1")) {
                ss.append("B货道无杯。");
                bit2 = 1;
            } else {
                bit2 = 0;
                ss.append("");
            }
            if (warn3.substring(6, 7).equals("1")) {
                ss.append("A货道无杯。");
                bit1 = 1;
            } else {
                bit1 = 0;
                ss.append("");
            }
            //这里修改为出杯完成
            if (warn1.substring(3, 4).equals("1")) {
                Log.e("查询", "getmsg1: " + warn1.substring(2, 3));
                if (!aa) {
                    Log.e("查询", "出杯完成: " + aa);
                    lll += 1;
                    if (tv_luobei_num != null) {
                        tv_luobei_num.setText("落杯数量：" + lll);
                    }
                    aa = true;
                }
            } else {
                aa = false;
            }
            if (warn2.substring(4, 5).equals("1")) {
                ss.append("所有桶的水用完了。");
            } else {
                ss.append("");
            }
            if (warn2.substring(5, 6).equals("1")) {
                ss.append("1桶的水用完了。");
            } else {
                ss.append("");
            }
            if (warn2.substring(6, 7).equals("1")) {
                ss.append("内置冷水箱水位低。");
            } else {
                ss.append("");
            }
            if (warn2.substring(7, 8).equals("1")) {
                ss.append("加热故障加热管不加热。");
            } else {
                ss.append("");
            }
            tv_error.setText("机器错误描述:" + ss.toString());
        }

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (flag == 1) {
                    Log.e(TAG, "测试模式自动储备: " + bit1 + bit2);
                    if (bit1 == 0) {
                        Log.e(TAG, "handleMessage: " + "出A货道");
                        tv_send_ins.setText("发送指令:" + "A货道自动落杯中..");
                        MainCommunicate.getInstance().normal_onlycup(1);
                    } else {
                        Log.e(TAG, "handleMessage: " + "出B货道");
                        tv_send_ins.setText("发送指令:" + "B货道自动落杯中..");
                        MainCommunicate.getInstance().normal_onlycup(2);
                    }
                    if (bit1 == 1 && bit2 == 1) {
                        tv_send_ins.setText("两个货道都无杯了,自动落杯关闭");
                        flag = 0;
                    }
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult: " + requestCode);
        TTSUtils.getInstance().init();
        TTSUtils.getInstance().speak("使用语音");
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}