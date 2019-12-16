package com.mei.chaji.ui.main.fragment.opsfm;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.mei.chaji.R;
import com.mei.chaji.app.Constants;
import com.mei.chaji.core.http.api.RetrofitServiceManager;
import com.mei.chaji.core.rxretorfit.MyObserver;
import com.mei.chaji.instruction.MainCommunicate;
import com.mei.chaji.ui.main.activity.OpsActivity;
import com.mei.chaji.utils.ECBAESUtils;
import com.vondear.rxtool.view.RxToast;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ClearModeFm extends Fragment {
    private Unbinder unbinder;
    @BindView(R.id.btn_cancel_clearmode)
    Button btn_cancel_clearmode;
    private static String device_No;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_clear_mode, container, false);
        unbinder = ButterKnife.bind(this, view);
        initUI();
        initData();
        initListener();
        return view;
    }

    private void initUI() {
    }

    private void initData() {
    }

    @SuppressLint("CheckResult")
    private void initListener() {
        RxView.clicks(btn_cancel_clearmode).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(o -> btn_cancel_clearmods());
    }

    private void btn_cancel_clearmods() {
        //退出清洁模式指令
        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();
        map.put("appId", "APP0000001");
        map.put("secret", "chaji20190505");
        map.put("deviceNo", device_No);
        map.put("deviceRunStatus", "1");
        String jsonbody = gson.toJson(map);
        String aa = ECBAESUtils.encrypt(Constants.AES_KEY, jsonbody);
        RetrofitServiceManager.getAPIService().cleaningDevice(aa)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>() {


                    @Override
                    public void onSuccess(String s) {
                        String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, s);
                        Log.e("解密", "onSuccess: " + decryStr);
                        //第一步：先获取jsonobject对象
                        com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(decryStr);
                        //第二步：把对象转换成jsonArray数组
                        boolean result = jsonObject1.getBoolean("result");
                        String msg = jsonObject1.getString("msg");
                        if (result){
                            RxToast.normal(msg);
                            MainCommunicate.getInstance().change_normal();
                        }else {
                            Log.e("==", "onSuccess: "+msg );
                        }
                    }
                });

    }



    private void btn_confirms() {

    }

    public static ClearModeFm newInstance(String deviceNo) {
        ClearModeFm settingDeviceNoFm = new ClearModeFm();
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
}
