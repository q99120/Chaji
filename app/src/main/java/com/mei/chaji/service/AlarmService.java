package com.mei.chaji.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.mei.chaji.app.Constants;
import com.mei.chaji.core.http.api.RetrofitServiceManager;
import com.mei.chaji.core.rxretorfit.MyObserver;
import com.mei.chaji.utils.CommonUtils;
import com.mei.chaji.utils.ECBAESUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlarmService extends Service {
    boolean service_flag;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("服务", "onCreate: " + "进入声明服务");
        service_flag = false;
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("闹钟", "onReceive: " + "进入闹钟");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("mc_info", Context.MODE_PRIVATE);
                String deviceNo = sharedPreferences.getString("deviceNo", "");
                Map<String, String> map = new HashMap<>();
                map.put("appId", "APP0000001");
                map.put("secret", "chaji20190505");
                map.put("deviceNo", deviceNo);
                String aa = CommonUtils.getGsonEsa(map);
                RetrofitServiceManager.getAPIService().deviceOnlineStatement(aa)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MyObserver<String>() {
                            @Override
                            public void onSuccess(String response) {
                                if (response != null) {
                                    String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, response);
                                    Log.e("输出结果", "onSuccess: " + decryStr);
                                    com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(decryStr);
                                    //第二步：把对象转换成jsonArray数组
                                    boolean result = jsonObject1.getBoolean("result");
                                    if (result) {
                                    }
                                }
                            }
                        });
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + 5 * 60 * 1000;
        Intent intent2 = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent2, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
