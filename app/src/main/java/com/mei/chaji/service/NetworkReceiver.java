package com.mei.chaji.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.mei.chaji.base.activity.BaseActivity;
import com.mei.chaji.ui.main.activity.AdpicActivity;
import com.mei.chaji.utils.NetUtils;

public class NetworkReceiver extends BroadcastReceiver {
    private INetEvent mINetEvent = AdpicActivity.mINetEvent;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果相等的话就说明网络状态发生了变化
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            //容错机制
            if (mINetEvent != null) {
                mINetEvent.onNetChange(NetUtils.getNetWorkState(context));
            }
        }
    }
}
