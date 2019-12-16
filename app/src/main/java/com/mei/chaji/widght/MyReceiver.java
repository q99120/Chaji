package com.mei.chaji.widght;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.mei.chaji.ui.main.activity.AdpicActivity;
import com.mei.chaji.ui.main.activity.SplashActivity;
import com.vondear.rxtool.view.RxToast;


public class MyReceiver extends BroadcastReceiver {
    private final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("注册广播", "onReceive: " + "注册广播");
        /**
         * 如果 系统 启动的消息，则启动 APP 主页活动
         */
//        if (NetworkUtils.isConnected()) {
//            if (NetworkUtils.isAvailableByPing()) {
//                if (NetworkUtils.getMobileDataEnabled()) {
//                    if (NetworkUtils.isMobileData()) {
//                        if (NetworkUtils.is4G()) {
////                            initservice();
//                            RxToast.normal("当前4G网络可用");
//                            Log.e("当前4G网络可用", "initEventAndData: " + "当前4G网络可用");
//                        }
//                    }
//                } else if (NetworkUtils.getWifiEnabled()) {
//                    if (NetworkUtils.isWifiConnected()) {
//                        if (NetworkUtils.isWifiAvailable()) {
//                            RxToast.normal("当前wifi网络可用");
//                            Log.e("当前wifi网络可用", "initEventAndData: " + "当前wifi网络可用");
//                        }
//                    }
//
//                }
//            } else {
//                RxToast.normal("网络不通");
//            }
//        } else {
//            RxToast.normal("无可用网络,请设置网络后重新启动软件");
//            Log.e("无可用网络", "initEventAndData: " + "无可用网络");
//            if (!NetworkUtils.getWifiEnabled()) {
////                NetworkUtils.setWifiEnabled(true);
////            }else if (!NetworkUtils.getMobileDataEnabled()){
////                NetworkUtils.setMobileDataEnabled(true);
//            }
//            AppUtils.relaunchApp();
//        }
//    }
        if (ACTION_BOOT.equals(intent.getAction())) {
            Intent intentMainActivity = new Intent(context, SplashActivity.class);
            intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentMainActivity);
            Toast.makeText(context, "开机完毕~", Toast.LENGTH_LONG).show();
        }
    }

}
