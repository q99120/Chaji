package com.mei.chaji.ui.main.fragment.opsfm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.mei.chaji.R;
import com.mei.chaji.app.Constants;
import com.mei.chaji.base.fragment.BaseFragment;
import com.mei.chaji.component.ActivityController;
import com.mei.chaji.core.bean.main.DeviceBasicParam;
import com.mei.chaji.core.http.api.RetrofitServiceManager;
import com.mei.chaji.core.rxretorfit.MyObserver;
import com.mei.chaji.instruction.MainCommunicate;
import com.mei.chaji.ui.main.activity.OpsActivity;
import com.mei.chaji.utils.CommonUtils;
import com.mei.chaji.utils.ECBAESUtils;
import com.mei.chaji.utils.FastStackUtil;
import com.vondear.rxtool.view.RxToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BasicParamFm extends BaseFragment {
    //    @BindView(R.id.btn_cancel_clearmode)
//    Button btn_cancel_clearmode;
    String TAG = "BasicParamFm";
    @BindView(R.id.et_lost_cup)
    EditText et_lost_cup;
    @BindView(R.id.et_tmp)
    EditText et_tmp;
    @BindView(R.id.et_key_full)
    EditText et_key_full;
    @BindView(R.id.btn_confirm)
    Button btn_confirm;
    @BindView(R.id.btn_exit)
    Button btn_exit;
    @BindView(R.id.system_seek)
    SeekBar system_seek;
    @BindView(R.id.media_seek)
    SeekBar media_seek;
    @BindView(R.id.tv_media)
    TextView tv_media;
    AudioManager mAudioManager;
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    int media_voice, system_voice;
    private List<DeviceBasicParam> basicParamFms = new ArrayList<>();


    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        RxView.clicks(btn_confirm).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> btn_confirms());
        RxView.clicks(btn_exit).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> btn_exits());
    }

    @SuppressLint("SetTextI18n")
    protected void initUI() {
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        int curret_system_voice = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        Log.e(TAG, "系统音量获取: " + curret_system_voice);
        int curret_media_voice = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.e(TAG, "多媒体音量获取: " + curret_media_voice);
        tv_media.setText("当前音量：" + curret_media_voice);
        media_seek.setProgress(curret_media_voice);
        btn_exit.setText("退出程序" + "版本号" + "1.1.0");
        system_voice = curret_system_voice;
        media_voice = curret_media_voice;

        et_tmp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int int_tmp = Integer.parseInt(s.toString());
                if (int_tmp<77){
                    RxToast.warning("温度不能小于77度,请重新输入");
                }

            }
        });

    }

    protected void initData() {
        cachedThreadPool.execute(() -> {
            Map<String, String> map = new HashMap<>();
            map.put("appId", "APP0000001");
            map.put("secret", "chaji20190505");
            map.put("deviceNo", device_No);
            String aa = CommonUtils.getGsonEsa(map);
            RetrofitServiceManager.getAPIService().findtDevicePreferences(aa)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver<String>() {
                        @Override
                        public void onSuccess(String s) {
                            String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, s);
                            Log.e(TAG, "onSuccess: " + decryStr);
                            JSONObject jsonObject1 = JSONObject.parseObject(decryStr);
                            //第二步：把对象转换成jsonArray数组
                            boolean result = jsonObject1.getBoolean("result");
                            if (result) {
                                JSONObject row_ob = jsonObject1.getJSONObject("row");
                                et_lost_cup.setText(row_ob.getString("loseCupTime"));
                                et_key_full.setText(row_ob.getString("maxVolume"));
                                tv_media.setText("当前音量：" + row_ob.getString("deviceVolumeSize"));
                                media_seek.setProgress(Integer.parseInt(row_ob.getString("deviceVolumeSize")));
                                et_tmp.setText(row_ob.getString("hotWaterTemperature"));
//                                editor.putInt("hotWaterTemperature", Integer.parseInt(row_ob.getString("hotWaterTemperature")));

                                Log.e(TAG, "结果" + row_ob.getString("loseCupTime") + row_ob.getString("maxVolume")
                                        + row_ob.getString("deviceVolumeSize") + row_ob.getString("hotWaterTemperature"));
                            }
                        }
                    });

        });
        system_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                system_voice = progress;
                Log.e(TAG, "onRangeChanged: " + system_voice);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, system_voice, AudioManager.FLAG_PLAY_SOUND);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                system_voice = seekBar.getProgress();
            }
        });
        media_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                media_voice = progress;
                Log.e(TAG, "initData: " + media_voice);
                tv_media.setText("音量:" + media_voice);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, media_voice, AudioManager.FLAG_PLAY_SOUND);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                media_voice = seekBar.getProgress();
                tv_media.setText("音量:" + media_voice);
            }
        });
    }


    private void btn_exits() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage("是否退出程序?")//设置对话框的内容
                //设置对话框的按钮
                .setNegativeButton("否", (dialog12, which) -> {
                    dialog12.dismiss();
                })
                .setPositiveButton("是", (dialog1, which) -> {
//                    FastStackUtil.getInstance().exit();
                    ActivityController.getInstance().exitApp(getActivity());
                }).create();
        dialog.show();
    }


    private void btn_confirms() {
        cachedThreadPool.execute(() -> {
            Gson gson = new Gson();
            Map<String, String> map = new HashMap<>();
            map.put("appId", "APP0000001");
            map.put("secret", "chaji20190505");
            map.put("deviceNo", device_No);
            map.put("maxVolume", et_key_full.getText().toString().trim());
            map.put("loseCupTime", et_lost_cup.getText().toString());
            map.put("hotWaterTemperature", et_tmp.getText().toString());
            map.put("adVolumeSize", "10");
            map.put("deviceVolumeSize", media_voice + "");
            map.put("standByTime", "60");
            String jsonbody = gson.toJson(map);
            String aa = ECBAESUtils.encrypt(Constants.AES_KEY, jsonbody);
            RetrofitServiceManager.getAPIService().updateDevicePreferences(aa)
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
                                Log.e(TAG, "onSuccess: " + msg);
                                RxToast.normal(msg);
                                int lost_time = Integer.valueOf(et_lost_cup.getText().toString());
                                MainCommunicate.getInstance().lost_cup(lost_time);
                                OpsActivity activity = (OpsActivity) getActivity();
                                editor.putInt("hotWaterTemperature", Integer.parseInt(et_tmp.getText().toString().trim()));
                                editor.apply();
                                if (activity != null) {
                                    Log.e(TAG, "基础设置进入首页" );
                                    activity.goHome();
                                }
                            } else {
                                RxToast.normal(msg);
                                Log.e(TAG, "onfailure: " + msg);
                            }
                        }
                    });
        });

    }

    public static BasicParamFm newInstance() {
        return new BasicParamFm();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected int setContentView() {
        return R.layout.fm_setting_basic;
    }

    @Override
    protected void lazyLoad() {

    }
}
