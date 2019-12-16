package com.mei.chaji.ui.main.fragment.opsfm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.mei.chaji.R;
import com.mei.chaji.app.Constants;
import com.mei.chaji.base.fragment.BaseFragment;
import com.mei.chaji.component.ActivityController;
import com.mei.chaji.core.http.api.RetrofitServiceManager;
import com.mei.chaji.core.rxretorfit.MyObserver;
import com.mei.chaji.instruction.MainCommunicate;
import com.mei.chaji.utils.CommonUtils;
import com.mei.chaji.utils.ECBAESUtils;
import com.vondear.rxtool.view.RxToast;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SettingOpsFm extends BaseFragment {
    private Unbinder unbinder;
    //    @BindView(R.id.btn_cancel_clearmode)
//    Button btn_cancel_clearmode;
    @BindView(R.id.btn_clear_mode)
    Button btn_clear_mode;
    @BindView(R.id.btn_normal_mode)
    Button btn_normal_mode;
    @BindView(R.id.btn_kaiqi1)
    Button btn_kaiqi1;
    @BindView(R.id.btn_kaiqi2)
    Button btn_kaiqi2;
    @BindView(R.id.btn_guanbi1)
    Button btn_guanbi1;
    @BindView(R.id.btn_guanbi2)
    Button btn_guanbi2;
    @BindView(R.id.btn_reset)
    Button btn_reset;
    @BindView(R.id.btn_exit_clear)
    Button btn_exit_clear;
    private Dialog dialog;
    private View inflate;
    private boolean check_flag;


    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        RxView.clicks(btn_clear_mode).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(o -> btn_clear_modes());
        RxView.clicks(btn_normal_mode).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(o -> btn_normal_modes());
        RxView.clicks(btn_reset).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(o -> btn_resets());
        RxView.clicks(btn_exit_clear).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(o -> btn_exit_clears());
        RxView.clicks(btn_kaiqi1).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(o -> {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setMessage("是否开启爪A货道?")//设置对话框的内容
                            //设置对话框的按钮
                            .setNegativeButton("否", (dialog12, which) -> {
                                dialog12.dismiss();
                            })
                            .setPositiveButton("是", (dialog1, which) -> {
                                MainCommunicate.getInstance().repair_hand1(1);
                            }).create();
                    dialog.show();
                });
        RxView.clicks(btn_kaiqi2).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(o -> {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setMessage("是否开启爪B货道?")//设置对话框的内容
                            //设置对话框的按钮
                            .setNegativeButton("否", (dialog12, which) -> {
                                dialog12.dismiss();
                            })
                            .setPositiveButton("是", (dialog1, which) -> {
                                MainCommunicate.getInstance().repair_hand2(1);
                            }).create();
                    dialog.show();
                });
        RxView.clicks(btn_guanbi1).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(o -> {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setMessage("是否关闭爪A货道?")//设置对话框的内容
                            //设置对话框的按钮
                            .setNegativeButton("否", (dialog12, which) -> {
                                dialog12.dismiss();
                            })
                            .setPositiveButton("是", (dialog1, which) -> {
                                MainCommunicate.getInstance().repair_hand1(0);
                            }).create();
                    dialog.show();
                });
        RxView.clicks(btn_guanbi2).throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(o -> {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setMessage("是否关闭爪B货道?")//设置对话框的内容
                            //设置对话框的按钮
                            .setNegativeButton("否", (dialog12, which) -> {
                                dialog12.dismiss();
                            })
                            .setPositiveButton("是", (dialog1, which) -> {
                                MainCommunicate.getInstance().repair_hand2(0);
                            }).create();
                    dialog.show();
                });
    }

    private void btn_exit_clears() {
        Toast.makeText(getActivity(),"exit",Toast.LENGTH_LONG).show();
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage("是否取消激活并退出app?")//设置对话框的内容
                //设置对话框的按钮
                .setNegativeButton("否", (dialog1, which) -> dialog1.dismiss())
                .setPositiveButton("是", (dialog12, which) -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("appId", "APP0000001");
                    map.put("secret", "chaji20190505");
                    map.put("deviceNo", device_No);
                    map.put("userId", user_id);
                    String aa = CommonUtils.getGsonEsa(map);
                    RetrofitServiceManager.getAPIService().updateDeviceDeactivate(aa).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new MyObserver<String>() {
                                @Override
                                public void onSuccess(String response) {
                                    String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, response);
                                    Log.e("解密", "onSuccess: " + decryStr);
                                    com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(decryStr);
                                    boolean result = jsonObject1.getBoolean("result");
                                    if (result) {
                                        editor.remove("deviceNo");
                                        editor.apply();
                                        ActivityController.getInstance().exitApp(getActivity());
                                    }
                                }
                            });
                }).create();
        dialog.show();
    }

    protected void initUI() {
    }

    protected void initData() {
    }


    private void btn_clear_modes() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage("是否开启清洗模式?")//设置对话框的内容
                //设置对话框的按钮
                .setNegativeButton("否", (dialog1, which) -> dialog1.dismiss())
                .setPositiveButton("是", (dialog12, which) -> {
                    Gson gson = new Gson();
                    Map<String, String> map = new HashMap<>();
                    map.put("appId", "APP0000001");
                    map.put("secret", "chaji20190505");
                    map.put("deviceNo", device_No);
                    map.put("deviceRunStatus", "2");
                    map.put("userId", user_id);
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
                                    if (result) {
                                        RxToast.normal(msg);
                                        MainCommunicate.getInstance().change_clear();
                                        editor.putBoolean("mode_type",true);
                                        editor.apply();
                                    } else {
                                        Log.e("11", "onSuccess: " + msg);
                                    }
                                }
                            });
                }).create();
        dialog.show();

    }

    private void btn_normal_modes() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage("是否开启正常模式?")//设置对话框的内容
                //设置对话框的按钮
                .setNegativeButton("否", (dialog1, which) -> dialog1.dismiss())
                .setPositiveButton("是", (dialog12, which) -> {
                    //退出清洁模式指令
                    Map<String, String> map = new HashMap<>();
                    map.put("appId", "APP0000001");
                    map.put("secret", "chaji20190505");
                    map.put("deviceNo", device_No);
                    map.put("deviceRunStatus", "1");
                    map.put("userId", user_id);
                    String aa = CommonUtils.getGsonEsa(map);
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
                                    if (result) {
                                        RxToast.normal(msg);
                                        MainCommunicate.getInstance().change_normal();
                                        editor.putBoolean("mode_type",false);
                                        editor.apply();
                                    } else {
                                        Log.e("==", "onSuccess: " + msg);
                                    }
                                }
                            });
                }).create();
        dialog.show();
    }

    private void btn_unlocks() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage("是否打开双爪?")//设置对话框的内容
                //设置对话框的按钮
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainCommunicate.getInstance().repair_hand1(0);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainCommunicate.getInstance().repair_hand1(1);
                    }
                }).create();
        dialog.show();
    }

    private void btn_resets() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage("是否开启机器复位?")//设置对话框的内容
                //设置对话框的按钮
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainCommunicate.getInstance().system_reset();
                    }
                }).create();
        dialog.show();
    }

    public static SettingOpsFm newInstance(String deviceNo) {
        return new SettingOpsFm();
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
        return R.layout.fm_setting_ops;
    }

    @Override
    protected void lazyLoad() {

    }
}
