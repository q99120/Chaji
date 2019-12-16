package com.mei.chaji.base.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mei.chaji.component.ActivityController;
import com.mei.chaji.service.INetEvent;
import com.mei.chaji.utils.FastStackUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseActivity extends AppCompatActivity   {
    //    protected T mPresenter;
    private CompositeDisposable compositeDisposable;
    public static String deviceNo;
    public Unbinder unBinder;
    public  SharedPreferences sp;
    public SharedPreferences.Editor editor;
    String TAG = "BaseActivity";
    protected BaseActivity mActivity;
//    public SpeechSynthesizer mSpeech;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化网络状态的监听
        sp = getSharedPreferences("mc_info", MODE_PRIVATE);
        editor = sp.edit();
        deviceNo = sp.getString("deviceNo", deviceNo);
        initSystemBarTint();
        NavigationBarStatusBar(this,true); // 导航栏，状态栏隐藏
        setContentView(getLayoutId());
        unBinder = ButterKnife.bind(this);
//        ActivityController.getInstance().addActivity(this);
        FastStackUtil.getInstance().push(this);
        initUI();
        initEventAndData();

    }


    @Override
    protected void onDestroy() {
        FastStackUtil.getInstance().pop(this);
//        ActivityController.getInstance().removeActivity(this);
        //销毁移除当前activity并解绑
        if (unBinder != null && unBinder != Unbinder.EMPTY) {
            unBinder.unbind();
            unBinder = null;
        }
        super.onDestroy();
    }


    public void replaceFragment(@IdRes int resId, Fragment fragment, boolean isBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(resId, fragment);
        if (isBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    public void addFragment(@IdRes int resId, Fragment fragment, boolean isBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(resId, fragment);
        if (isBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }


    protected abstract void initUI();

    private void initDecord() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void initSystemBarTint() {
        //模拟器不能横屏暂时屏蔽
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉状态栏
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
//        Window window = getWindow();
//            if (translucentStatusBar()) {
//        // 设置状态栏全透明
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
////            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            //设置状态栏字体颜色
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
        return;
    }


    /**
     * 导航栏，状态栏隐藏
     *
     * @param activity
     */
    public static void NavigationBarStatusBar(Activity activity, boolean hasFocus) {
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    /**
     * 获取当前Activity的UI布局
     *
     * @return 布局id
     */
    protected abstract int getLayoutId();


    /**
     * 初始化数据
     */
    protected abstract void initEventAndData();

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: " + "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: " + "");
    }

}
