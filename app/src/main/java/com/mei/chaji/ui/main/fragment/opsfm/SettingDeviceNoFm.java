package com.mei.chaji.ui.main.fragment.opsfm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jakewharton.rxbinding2.view.RxView;
import com.mei.chaji.R;
import com.mei.chaji.base.fragment.BaseFragment;
import com.mei.chaji.service.AlarmResetBroadcast;
import com.mei.chaji.utils.SpUtils;
import com.mei.chaji.utils.Utilss;
import com.vondear.rxtool.view.RxToast;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingDeviceNoFm extends BaseFragment {
    private Unbinder unbinder;
    public static String TAG = "SettingDeviceNoFm";
    @BindView(R.id.et_deviceNo)
    EditText et_deviceNo;
    @BindView(R.id.timePic1)
    TimePicker timePicker;
    @BindView(R.id.tv_times)
    TextView tv_times;
    @BindView(R.id.title_clock)
    TextView title_clock;
    @BindView(R.id.btn_open_clock)
    Button btn_open_clock;
    @BindView(R.id.btn_close_clock)
    Button btn_close_clock;
    private static String device_No;
    AlarmManager am;
    public SharedPreferences sp;
    public SharedPreferences.Editor editor;
    PendingIntent pendingIntent;
//    String device_id, mac_id, table_name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_setting_deviceno, container, false);
        unbinder = ButterKnife.bind(this, view);
        initUI();
        initData();
        initListener();
        return view;
    }

    @Override
    protected void initListeners() {

    }

    protected void initUI() {
        boolean IsClock = (boolean) SpUtils.get(getActivity(), "reset_clock", false);
        long time_long = (long) SpUtils.get(getActivity(), "calTime", 0L);
        Log.e("获取时间戳2", String.valueOf(time_long));
        String time_formate = Utilss.getDateToString(time_long);
        if (IsClock) {
            tv_times.setText("(定时重启时间(已开启)" + time_formate);
        } else {
            tv_times.setText("(定时重启时间(已关闭)");
        }

    }

    protected void initData() {
        timePicker.setIs24HourView(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(4);
            timePicker.setMinute(0);
        }
    }

    @SuppressLint({"CheckResult"})
    private void initListener() {
        et_deviceNo.setText(device_No);
        RxView.clicks(btn_open_clock).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> open_clocks());
        RxView.clicks(btn_close_clock).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> close_clocks());

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void open_clocks() {
        long open_time;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            open_time = Utilss.getString2Date(timePicker.getHour() + ":" + timePicker.getMinute() + ":00", "HH:mm:ss");
//        } else {
//            open_time = Utilss.getString2Date(timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute() + ":00", "HH:mm:ss");
//        }
//        Log.e(TAG, "syst: " + open_time);
//        String time_format = Utilss.getDateToString(open_time);
//        String times[] = time_format.split(":");
        am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmResetBroadcast.class);
        intent.setAction("startAlarm");
//        intent.setAction(AlarmResetService.ACTION_ALARM);
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar calendar = Calendar.getInstance();
        //是设置日历的时间，主要是让日历的年月日和当前同步
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
            calendar.set(Calendar.SECOND,0);
        }else {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            calendar.set(Calendar.SECOND,0);
        }

        //获取当前毫秒值,如果时间大于现在，则加一天
        long systemTime = System.currentTimeMillis();
        long calTime = calendar.getTimeInMillis();
        if (systemTime > calTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        SpUtils.put(getActivity(),"reset_clock",true);
        SpUtils.put(getActivity(),"calTime",calTime);
        tv_times.setText("定时重启时间(已开启)" + timePicker.getHour()+":"+timePicker.getMinute());
        RxToast.normal("设置成功");
    }

    private void close_clocks() {
        am.cancel(pendingIntent);
        tv_times.setText("定时重启时间(已关闭)");
        SpUtils.put(getActivity(),"reset_clock",false);
    }


//    private void btn_confirms() {
//        Gson gson = new Gson();
//        Map<String, String> map = new HashMap<>();
//        map.put("appId", "APP0000001");
//        map.put("secret", "chaji20190505");
//        map.put("deviceNo", et_deviceNo.getText().toString().trim());
//        map.put("padImei", device_id);
//        map.put("padMac", mac_id);
//        map.put("padName", table_name);
//        String jsonbody = gson.toJson(map);
//        Log.e(TAG, "btn_confirms: " + jsonbody);
//        String aa = ECBAESUtils.encrypt(Constants.AES_KEY, jsonbody);
//        RetrofitServiceManager.getAPIService().updateDeviceActivation(aa)
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new MyObserver<String>() {
//
//                    @Override
//                    public void onSuccess(String s) {
//                        String de_s = ECBAESUtils.decrypt(Constants.AES_KEY, s);
//                        Log.e(TAG, "onSuccess: " + de_s);
//                    }
//                });
//    }

    public static SettingDeviceNoFm newInstance(String deviceNo) {
        SettingDeviceNoFm settingDeviceNoFm = new SettingDeviceNoFm();
        device_No = deviceNo;
        return settingDeviceNoFm;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    protected int setContentView() {
        return 0;
    }

    @Override
    protected void lazyLoad() {

    }

}
