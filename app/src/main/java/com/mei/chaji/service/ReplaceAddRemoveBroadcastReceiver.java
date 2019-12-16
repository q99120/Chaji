package com.mei.chaji.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
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

public class ReplaceAddRemoveBroadcastReceiver extends BroadcastReceiver {

    public static final String UPDATE_ACTION = "android.intent.action.PACKAGE_REPLACED";
    public String TAG = "ReplaceAddReceiver";

    // APP包名ID
    public static final String PACKAGE_NAME = "com.mei.chaji";
    private SharedPreferences sharedPreferences;


    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences("mc_info", Context.MODE_PRIVATE);
        String deviceNo = sharedPreferences.getString("deviceNo", "");
        String updateVersionId = sharedPreferences.getString("updateVersionId","");
        if (intent.getAction().equals(UPDATE_ACTION)) {
            String packageName = intent.getData().getEncodedSchemeSpecificPart();
            if (packageName.equals(PACKAGE_NAME)) {

                Log.e(TAG, "更新安装成功....." + packageName);
                Toast.makeText(context, "更新安装成功", Toast.LENGTH_SHORT).show();
                //给服务器发
                Gson gson = new Gson();
                Map<String, String> map = new HashMap<>();
                map.put("appId", "APP0000001");
                map.put("secret", "chaji20190505");
//                map.put("deviceNo", deviceNo);
                map.put("updateVersionId", updateVersionId);
                map.put("status", "1");
                String aa = CommonUtils.getGsonEsa(map);
                RetrofitServiceManager.getAPIService().alertUpVersion(aa)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MyObserver<String>() {
                            @Override
                            public void onSuccess(String response) {
                                Log.e(TAG, "更新...: "+"" );
                                String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, response);
                                com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(decryStr);
                                //第二步：把对象转换成jsonArray数组
                                boolean result = jsonObject1.getBoolean("result");
                                String msg = jsonObject1.getString("msg");
                                if (result) {
                                    RxToast.normal("修改版本更新信息成功");
                                }
                            }
                        });

                // 重新启动APP
                Intent intentToStart = context.getPackageManager().getLaunchIntentForPackage(packageName);
                context.startActivity(intentToStart);
            }
        }

    }
}
