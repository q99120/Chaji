package com.mei.chaji.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

public class AlarmResetService extends Service {
    public static String ACTION_ALARM = "action_alarm";
    private Handler mHanler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHanler.post(new Runnable() {
            @Override
            public void run() {
//                try {
//                    Runtime.getRuntime().exec("su -c reboot");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                Log.e("syst", "run: "+"执行过程" );
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
