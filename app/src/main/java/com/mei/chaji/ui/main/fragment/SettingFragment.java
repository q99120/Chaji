package com.mei.chaji.ui.main.fragment;

import android.annotation.SuppressLint;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.mei.chaji.R;
import com.mei.chaji.base.fragment.BaseFragment;
import com.mei.chaji.ui.main.activity.OpsActivity;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

/**
 * 设置主页面
 */

public class SettingFragment extends BaseFragment {
    @BindView(R.id.set_device_num)
    ImageButton set_device_num;
    @BindView(R.id.add_goods)
    ImageButton add_goods;
    //    @BindView(R.id.clear_mode)
//    Button clear_mode;
    @BindView(R.id.btn_basic_param)
    ImageButton btn_basic_param;
    //    @BindView(R.id.reset_system)
//    Button reset_system;
//    @BindView(R.id.param_setting)
//    Button param_setting;
    @BindView(R.id.btn_ops_set)
    ImageButton btn_ops_set;
    @BindView(R.id.goods_setting)
    ImageButton goods_setting;
    @BindView(R.id.btn_test_mode)
    ImageButton btn_test_mode;
    @BindView(R.id.iv_home)
    ImageView iv_home;
    private String TAG = "SettingFragment";
    OpsActivity activity;


    public static SettingFragment newInstance() {
        return new SettingFragment();
    }


    @SuppressLint("CheckResult")
    @Override
    public void initListeners() {
        RxView.clicks(set_device_num).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> device_num());
        RxView.clicks(add_goods).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> addgoods());
        RxView.clicks(iv_home).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> iv_homes());
        RxView.clicks(btn_basic_param).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> btn_basic_params());
        RxView.clicks(goods_setting).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> goods_settings());
        RxView.clicks(btn_ops_set).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> btn_ops_sets());
        RxView.clicks(btn_test_mode).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> activity.gotest());
    }

    @Override
    public void initData() {

    }


    protected void initUI() {
        activity = (OpsActivity) getActivity();
    }

    private void goods_settings() {
        activity.gogoodsset();
    }

    private void iv_homes() {
        activity.goPro();
    }

    private void btn_basic_params() {
        activity.goBasic();
    }

    private void btn_ops_sets() {
        activity.gobtn_ops_set();
    }


    //补货
    private void addgoods() {
        OpsActivity activity = (OpsActivity) getActivity();
        activity.goReplenish();
    }

    private void device_num() {
        activity.setDeviceNo();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected int setContentView() {
        return R.layout.fm_setting;
    }

    @Override
    protected void lazyLoad() {

    }

}
