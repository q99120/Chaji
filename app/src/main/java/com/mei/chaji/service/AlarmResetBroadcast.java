package com.mei.chaji.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.mei.chaji.utils.FileUtils;
import com.mei.chaji.utils.LogsFileUtil;
import com.mei.chaji.utils.SpUtils;
import com.mei.chaji.utils.Utilss;
import com.vondear.rxtool.view.RxToast;

import java.io.IOException;

public class AlarmResetBroadcast extends BroadcastReceiver {

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent) {
            sharedPreferences = context.getSharedPreferences("mc_info", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            if ("startAlarm".equals(intent.getAction())) {
                SpUtils.put(context,"resetclock",true);
                Log.e("syst", "onReceive: "+ "闹钟提醒");
                RxToast.normal("设备将在5秒后重启");
                LogsFileUtil.getInstance().addLog("重启设备","这次重启设备的时间为"+ Utilss.getCurrentTimesss());
                handler.sendEmptyMessageAtTime(1,5000);

            // 处理闹钟事件
            // 振动、响铃、或者跳转页面等
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what){
                case 1:
                    try {
                        Runtime.getRuntime().exec("su -c reboot");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
}
