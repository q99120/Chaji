package com.mei.chaji.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.mei.chaji.app.Constants;
import com.mei.chaji.core.http.api.RetrofitServiceManager;
import com.mei.chaji.core.rxretorfit.MyObserver;
import com.mei.chaji.utils.CommonUtils;
import com.mei.chaji.utils.ECBAESUtils;
import com.vondear.rxtool.view.RxToast;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AlarmService.class);
        context.startService(i);
    }
}
