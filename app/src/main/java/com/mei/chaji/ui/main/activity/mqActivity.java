package com.mei.chaji.ui.main.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;

import com.jakewharton.rxbinding2.view.RxView;
import com.mei.chaji.R;
import com.mei.chaji.base.activity.BaseActivity;
import com.mei.chaji.component.ActivityController;
import com.mei.chaji.service.AlarmResetBroadcast;
import com.mei.chaji.service.MQTTService;
import com.mei.chaji.utils.Utilss;
import com.vondear.rxtool.view.RxToast;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class mqActivity extends BaseActivity {
    @BindView(R.id.btn_ccc)
    Button a_cup;
    @BindView(R.id.btn_ddd)
    Button b_cup;
    @BindView(R.id.btn_eee)
    Button btn_eee;
    @BindView(R.id.btn_fff)
    Button btn_fff;
    @BindView(R.id.btn_ggg)
    Button btn_ggg;
    MQTTService mqttService;
    private static final String TAG = mqActivity.class.getSimpleName();
    AlarmManager am;
    PendingIntent pendingIntent;

    @BindView(R.id.timePic1)
    TimePicker timePicker;


    @SuppressLint("CheckResult")
    @Override
    protected void initUI() {
        mqttService = MQTTService.getInstance(new MQTTService.MqttCallbacks() {
            @Override
            public void subscribedSuccess(String message) {
                Log.e(TAG, "subscribedSuccess: " + message);

            }

            @Override
            public void connectFail(String message) {
                Log.e(TAG, "connectFail: " + message);
                mqttService.doClientConnection(mqActivity.this);
            }

            @Override
            public void connectRequest() {

            }

            @Override
            public void connectLost(String message) {
                mqttService.doClientConnection(mqActivity.this);
                Log.e(TAG, "connectLost: " + message);
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.mq_activity;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initEventAndData() {
        ActivityController.getInstance().addActivity(this);
        timePicker.setIs24HourView(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "initss: " + timePicker.getHour() + ":" + timePicker.getMinute());
//            tv_times.setText(timePicker.getHour() + ":" + timePicker.getMinute());
        } else {
//            tv_times.setText(timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
            Log.e(TAG, "init11: " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());

        }
//        initPermission();

        RxView.clicks(a_cup).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o ->
                {
                    testtt("11111111", "11111111", "11111111");

                });

        RxView.clicks(b_cup).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o ->
                {
                    testtt("10101100", "11110000", "10101010");
                });

        RxView.clicks(btn_eee).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o ->
                {
                    testtt("00111101", "10110101", "10100110");
                });


        RxView.clicks(btn_fff).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o ->
                {
                    testtt("11010101", "10101100", "00011110");
                });

        RxView.clicks(btn_ggg).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o ->
                {
                    initFalutStatus();
                    testtt("00000000", "00000000", "00000000");
                });



    }

    private void initss() {

        long open_time;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            open_time = Utilss.getString2Date(timePicker.getHour() + ":" + timePicker.getMinute() + ":00", "HH:mm:ss");
        } else {
            open_time = Utilss.getString2Date(timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute() + ":00", "HH:mm:ss");
        }
        Log.e(TAG, "initss: " + open_time);
        String time_format = Utilss.getDateToString(open_time);
        String times[] = time_format.split(":");
        am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmResetBroadcast.class);
        intent.setAction("startAlarm");
//        intent.setAction(AlarmResetService.ACTION_ALARM);
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
        RxToast.normal("设置成功");

    }

    // 设置闹钟
    private void setAlarm(Calendar calendar) {
    }

    int bit0, bit1, bit2, bit3, bit4, bit5, bit6, bit7, bit8, bit9, bit10, bit11, bit12, bit13, bit14, bit15, bit16, bit17,
            bit18, bit19, bit20, bit21;
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
    boolean guizi_open = false;
    StringBuilder fault_wx_sb = new StringBuilder();
    StringBuilder fault_reminder_sb = new StringBuilder();


    private void initFalutStatus(){
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

    void testtt(String warn1, String warn2, String warn3) {
        Log.e("=======", "getmsg1: " + "获得串口");
//        String warn1 = "11111111";
//        String warn2 = "11111110";
//        String warn3 = "11111111";
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
                fault_reminder_sb.append("落杯器2卡杯故障。");
            }
            bit4 = 1;
            Log.e(TAG, "警告三分指令: " + "落杯器2卡杯故障");
        } else {
            bit4 = 0;
        }
        if (warn3.substring(4, 5).equals("1")) {
            if (!cup_failure_a) {
                fault_reminder_sb.append("落杯器1卡杯故障。");
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
            if (!no_cup_b) {
                fault_wx_sb.append("货道B无杯。");
            }
            bit2 = 1;
        } else {
            bit2 = 0;
        }
        if (warn3.substring(6, 7).equals("1")) {
            if (!no_cup_a) {
                fault_wx_sb.append("货道A无杯。");
            }
            bit1 = 1;
        } else {
            bit1 = 0;
        }
        if (bit1 == 1 && bit2 == 1) {
            if (!no_cup_all) {
                fault_reminder_sb.append("货道A和货道B都没杯了。");
            }
        }
        if (warn3.substring(6, 7).equals("1") && warn3.substring(5, 6).equals("1")) {
        }
        if (warn1.substring(0, 1).equals("1")) {

        } else {
        }
        if (warn1.substring(2, 3).equals("1")) {
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
        if (warn2.substring(4, 5).equals("1")) {
            if (!water_used_all) {
                fault_reminder_sb.append("所有桶的水都用完了。");
            }
            Log.e(TAG, "警告二分指令: " + "所有桶用完" + bit11);
            bit11 = 1;
        } else {
            bit11 = 0;
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
        StringBuilder ss = new StringBuilder();
        for (int i = 0; i < 24; i++) {
            ss.append("0");
        }
//        if (ss.toString().equals(warn1 + warn2 + warn3)) {
//            initFalutStatus();
//        }
//            sendError();
//        canbuyErrror();
        Log.e(TAG, "状态a" + no_cup_a + "状态B" + no_cup_b + "柜子" + guizi_open + "水" + water_used_a);
        if (!no_cup_a || !no_cup_b || !guizi_open || !water_used_a) {
            if (fault_wx_sb.toString().contains("货道B无杯")) {
                no_cup_b = true;
            }
            if (fault_wx_sb.toString().contains("货道A无杯")) {
                no_cup_a = true;
            }
            if (fault_wx_sb.toString().contains("储物柜柜门没有关闭")) {
                guizi_open = true;
            }
            if (fault_wx_sb.toString().contains("1桶的水用完了")) {
                water_used_a = true;
            }
            if (fault_wx_sb.toString().length()>0) {
                FaultWx(fault_wx_sb.toString());
            }
            fault_wx_sb.delete(0, fault_wx_sb.length());
        }


        if (!heating_failure || !cup_failure_a || !cup_failure_b || !cup_failure_all || !water_used_all
                || !door_failure || !no_cup_all || !water_low) {
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
            if (fault_reminder_sb.toString().contains("落杯器1卡杯故障")) {
                cup_failure_a = true;
            }
            if (fault_reminder_sb.toString().contains("落杯器2卡杯故障")) {
                cup_failure_b = true;
            }
            if (fault_reminder_sb.toString().contains("两个落杯器都卡杯故障了")) {
                cup_failure_all = true;
            }
            if (fault_reminder_sb.toString().length()>0) {
                FaultReminder(fault_reminder_sb.toString());
            }
            fault_reminder_sb.delete(0, fault_reminder_sb.length());
        }
    }

    private void FaultReminder(String toString) {
        Log.e(TAG, "FaultReminder: " + toString);
    }

    private void FaultWx(String toString) {
        Log.e(TAG, "FaultWx: " + toString);
    }


}