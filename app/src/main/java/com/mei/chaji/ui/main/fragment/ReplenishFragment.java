package com.mei.chaji.ui.main.fragment;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.mei.chaji.R;
import com.mei.chaji.app.Constants;
import com.mei.chaji.base.fragment.BaseFragment;
import com.mei.chaji.core.http.api.RetrofitServiceManager;
import com.mei.chaji.core.rxretorfit.MyObserver;
import com.mei.chaji.instruction.MainCommunicate;
import com.mei.chaji.ui.main.activity.OpsActivity;
import com.mei.chaji.utils.CommonUtils;
import com.mei.chaji.utils.ECBAESUtils;
import com.vondear.rxtool.view.RxToast;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Jyx
 * 补货
 */

public class ReplenishFragment extends BaseFragment {
    private String TAG = "ReplenishFragment";
    @BindView(R.id.btn_Sub1)
    ImageView btn_Sub1;
    @BindView(R.id.btn_Sub2)
    ImageView btn_Sub2;
    @BindView(R.id.btn_add1)
    ImageView btn_add1;
    @BindView(R.id.btn_add2)
    ImageView btn_add2;
    @BindView(R.id.tv_cup_a)
    EditText tv_cup_a;
    @BindView(R.id.tv_cup_b)
    EditText tv_cup_b;
    @BindView(R.id.tv_tea1_name)
    TextView tv_tea1_name;
    @BindView(R.id.tv_tea2_name)
    TextView tv_tea2_name;
    @BindView(R.id.btn_confirm)
    Button btn_confirm;
    @BindView(R.id.a_key_pull)
    Button a_key_pull;
    @BindView(R.id.b_key_pull)
    Button b_key_pull;
    @BindView(R.id.btn_only_cup)
    Button btn_only_cup;
    int i = 1;
    int i1 = 1;
    int i_t1, i_t2;
    int maxvou;


    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        RxView.clicks(btn_Sub1).subscribe((Object o) -> Subtract1());
        RxView.clicks(btn_add1).subscribe((Object o) -> addition1());
        RxView.clicks(btn_Sub2).subscribe((Object o) -> Subtract2());
        RxView.clicks(btn_add2).subscribe((Object o) -> addition2());
        RxView.clicks(btn_confirm).throttleFirst(2, TimeUnit.SECONDS).subscribe((Object o) -> confirm());
        RxView.clicks(a_key_pull).subscribe((Object o) -> onekeypulls());
        RxView.clicks(b_key_pull).subscribe((Object o) -> twokeypulls());
        btn_only_cup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainCommunicate.getInstance().change_add();
            }
        });

    }

    protected void initData() {
        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();
        map.put("appId", "APP0000001");
        map.put("secret", "chaji20190505");
        map.put("deviceNo", device_No);
        String jsonbody = gson.toJson(map);
        String aa = ECBAESUtils.encrypt(Constants.AES_KEY, jsonbody);
        RetrofitServiceManager.getAPIService().findDeviceGoodsCount(aa)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>() {


                    @Override
                    public void onSuccess(String s) {
                        String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, s);
                        Log.e(TAG, "onSuccess: " + decryStr);
                        //第一步：先获取jsonobject对象
                        com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(decryStr);
                        //第二步：把对象转换成jsonArray数组
                        boolean result = jsonObject1.getBoolean("result");
                        String msg = jsonObject1.getString("msg");
                        if (result) {
                            String cargoWayCountOne = jsonObject1.getString("cargoWayCountOne");
                            String cargoWayCountTwo = jsonObject1.getString("cargoWayCountTwo");
                            tv_cup_a.setText(cargoWayCountOne);
                            tv_cup_b.setText(cargoWayCountTwo);
                            String maxVolume = jsonObject1.getString("maxVolume");
                            if (maxVolume != null) {
                                maxvou = Integer.parseInt(maxVolume);
                            } else {
                                maxvou = 50;
                            }
                            RxToast.normal(msg);
                        } else {
                            Log.e(TAG, "onSuccess: " + msg);
                        }
                    }
                });
    }

    @Override
    protected void initUI() {

    }

    @SuppressLint("SetTextI18n")
    private void onekeypulls() {
        Log.e(TAG, "onekeypull: " + maxvou);
        tv_cup_a.setText(maxvou + "");
    }

    private void twokeypulls() {
        Log.e(TAG, "twokeypull: " + maxvou);
        tv_cup_b.setText(maxvou + "");
    }

    private void confirm() {
        Map<String, String> map = new HashMap<>();
        map.put("appId", "APP0000001");
        map.put("secret", "chaji20190505");
        map.put("deviceNo", device_No);
        map.put("deviceId", "1");
        map.put("cargoWayCountOne", tv_cup_a.getText().toString().trim());
        map.put("cargoWayCountTwo", tv_cup_b.getText().toString().trim());
        map.put("userId", user_id);
        String aa = CommonUtils.getGsonEsa(map);
        RetrofitServiceManager.getAPIService().UpdateDeviceGC(aa)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e("请求数据", "onNext: " + s);
                        String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, s);
                        Log.e("解密后", "onNext: " + decryStr);
                        //第一步：先获取jsonobject对象
                        com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(decryStr);
                        //第二步：把对象转换成jsonArray数组
                        boolean result = jsonObject1.getBoolean("result");
                        String msg = jsonObject1.getString("msg");
                        if (result) {
                            RxToast.normal(msg);
                            MainCommunicate.getInstance().change_normal();
                            OpsActivity activity = (OpsActivity) getActivity();
                            if (activity != null) {
                                activity.goHome();
                            }
                        } else {
                        }
                    }
                });
    }

    private void Subtract2() {
        i_t2 = Integer.parseInt(tv_cup_b.getText().toString().trim());
        Log.e("减法", "不能点击: " + i1);
        i_t2 -= 1;
        tv_cup_b.setText(i_t2 + "");
    }

    private void addition2() {
        i_t2 = Integer.parseInt(tv_cup_b.getText().toString().trim());
        if (i_t2 < maxvou) {
            i_t2 += 1;
            tv_cup_b.setText(i_t2 + "");
        } else {
            Log.e("=========", "不能点击: " + i_t2);
        }
    }

    private void addition1() {
        i_t1 = Integer.parseInt(tv_cup_a.getText().toString().trim());
        if (i_t1 < maxvou) {
            i_t1 += 1;
            tv_cup_a.setText(i_t1 + "");
        } else {
            Log.e("加法b", "不能点击: " + i_t1);
        }
    }

    private void Subtract1() {
        i_t1 = Integer.parseInt(tv_cup_a.getText().toString().trim());
        if (i_t1 > 1) {
            i_t1 -= 1;
            tv_cup_a.setText(i_t1 + "");
        } else {
            Log.e("减法b", "不能点击: " + i_t1);
        }
    }


    public static ReplenishFragment newInstance() {
        return new ReplenishFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected int setContentView() {
        return R.layout.fm_replenish;
    }

    @Override
    protected void lazyLoad() {

    }

}
