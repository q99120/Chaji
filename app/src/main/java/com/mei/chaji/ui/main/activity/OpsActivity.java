package com.mei.chaji.ui.main.activity;

import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mei.chaji.R;
import com.mei.chaji.base.activity.BaseActivity;
import com.mei.chaji.component.ActivityController;
import com.mei.chaji.core.bean.main.Mqttmessages;
import com.mei.chaji.instruction.MainCommunicate;
import com.mei.chaji.ui.main.fragment.LoginFragment;
import com.mei.chaji.ui.main.fragment.ReplenishFragment;
import com.mei.chaji.ui.main.fragment.SettingFragment;
import com.mei.chaji.ui.main.fragment.opsfm.BasicParamFm;
import com.mei.chaji.ui.main.fragment.opsfm.ClearModeFm;
import com.mei.chaji.ui.main.fragment.opsfm.SettingDeviceNoFm;
import com.mei.chaji.ui.main.fragment.opsfm.SettingGoodsFm;
import com.mei.chaji.ui.main.fragment.opsfm.SettingOpsFm;
import com.vondear.rxtool.view.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

public class OpsActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.iv_home)
    ImageView iv_home;
    @BindView(R.id.tv_home)
    TextView tv_home;
    @BindView(R.id.iv_bg)
    ImageView iv_bg;

    private Timer mTimer; // 计时器，每1秒执行一次任务
    private MyTimerTask mTimerTask; // 计时任务，判断是否未操作时间到达ns
    private long mLastActionTime; // 上一次操作时间
    int flag;
    int ins_flag;
    String TAG = "OpsActivity";

    @Override
    protected void initUI() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ops;
    }

    @Override
    protected void initEventAndData() {
        ActivityController.getInstance().addActivity(this);
        Intent i = getIntent();
        int test_flag = i.getIntExtra("test_flag", 2);
        Log.e("登陆界面呢", "initEventAndData: " + test_flag);
        if (test_flag == 1) {
            goHome();
        } else {
            gologin();
        }
    }

    public void gologin() {
        Log.e(TAG, "gologin: " + "进入了login");
        iv_home.setVisibility(View.VISIBLE);
        iv_back.setVisibility(View.INVISIBLE);
        tv_home.setVisibility(View.VISIBLE);
        flag = 0;
        startTimer();
        ins_flag = 0;
        replaceFragment(R.id.frame_ops, LoginFragment.newInstance(), false);
//        replaceFragment(R.id.frame_ops, BasicParamFm.newInstance(), false);
    }

    //基本参数设置
    public void goBasic() {
        iv_home.setVisibility(View.INVISIBLE);
        iv_back.setVisibility(View.VISIBLE);
        tv_home.setVisibility(View.INVISIBLE);
        flag = 1;
        ins_flag = 0;
        replaceFragment(R.id.frame_ops, BasicParamFm.newInstance(), false);
    }

    //主页
    public void goHome() {
        Log.e(TAG, "goHome: " + "进入首页");
        if (mTimer != null) {
            mTimer.cancel();
        }
        flag = 1;
        ins_flag = 0;
        tv_home.setVisibility(View.VISIBLE);
        iv_home.setVisibility(View.VISIBLE);
        iv_back.setVisibility(View.INVISIBLE);
        Log.e("==================", "goHome: " + "111111111111");
        replaceFragment(R.id.frame_ops, SettingFragment.newInstance(), false);
//        replaceFragment(R.id.frame_ops, SettingGoodsFm.newInstance(deviceNo), false);
    }

    //补货
    public void goReplenish() {
        tv_home.setVisibility(View.INVISIBLE);
        iv_home.setVisibility(View.INVISIBLE);
        iv_back.setVisibility(View.VISIBLE);
        flag = 1;
        ins_flag = 1;
        replaceFragment(R.id.frame_ops, ReplenishFragment.newInstance(), false);
    }

    //设置设备编号
    public void setDeviceNo() {
        ins_flag = 0;
        tv_home.setVisibility(View.INVISIBLE);
        iv_home.setVisibility(View.INVISIBLE);
        iv_back.setVisibility(View.VISIBLE);
        flag = 1;
        replaceFragment(R.id.frame_ops, SettingDeviceNoFm.newInstance(deviceNo), false);
    }


    //测试
    public void gotest() {
        Intent i = new Intent();
        i.setClass(OpsActivity.this, TestActivity.class);
        startActivity(i);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    //在ui线程执行 优先级100
    public void getmsg(Mqttmessages msg) {
        Log.e("opsssss", "getmsg: " + msg.getType());
        int curret_time = (int) (System.currentTimeMillis() / 1000);
        if (msg.getType().equals("6")) {
            if (curret_time - msg.getDateTime() > 60) {
                Log.e("==============", "getmsg: " + "超时");
            } else {
                //远程设备复位
            }
        } else if (msg.getType().equals("7")) {
            if (curret_time - msg.getDateTime() > 60) {
                Log.e("==============", "getmsg: " + "超时");
            } else {
                //登陆
                if (msg.device_status) {
                    String userid = msg.getDevice_msg();
                    Log.e(TAG, "用户主键ID: "+userid );
                    editor.putString("userId", userid);
                    editor.apply();
                    goHome();
                } else {
                    RxToast.warning("登陆失败");
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("运维页面", "onResume: " + "进入页面");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void startTimer() {
        mTimer = new Timer();
        mTimerTask = new MyTimerTask();
        // 初始化上次操作时间为登录成功的时间
        mLastActionTime = System.currentTimeMillis();
        // 每过1s检查一次
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            // 30秒钟钟未操作停止计时并退出登录
            if (System.currentTimeMillis() - mLastActionTime > 1000 * 30) {
                if (flag == 0) {
                    stopTimer();// 停止计时任务
                }
            }
        }
    }

    private void stopTimer() {
        Intent i = new Intent(this, AdpicActivity.class);
        startActivity(i);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mLastActionTime = System.currentTimeMillis();
        Log.e("触摸时间", "dispatchTouchEvent: " + mLastActionTime);
        return super.dispatchTouchEvent(ev);
    }

    //运维设置
    public void gobtn_ops_set() {
        ins_flag = 0;
        tv_home.setVisibility(View.INVISIBLE);
        iv_home.setVisibility(View.INVISIBLE);
        iv_back.setVisibility(View.VISIBLE);
        replaceFragment(R.id.frame_ops, SettingOpsFm.newInstance(deviceNo), false);
    }

    //返回广告
    public void goPro() {
        Intent i = new Intent();
        i.setClass(OpsActivity.this, AdpicActivity.class);
        startActivity(i);
    }

    public void btn_ivback(View view) {
        tv_home.setVisibility(View.INVISIBLE);
        iv_home.setVisibility(View.INVISIBLE);
        iv_back.setVisibility(View.VISIBLE);
        replaceFragment(R.id.frame_ops, SettingFragment.newInstance(), false);
    }

    //商品设置
    public void gogoodsset() {
        ins_flag = 0;
        tv_home.setVisibility(View.INVISIBLE);
        iv_home.setVisibility(View.INVISIBLE);
        iv_back.setVisibility(View.VISIBLE);
        replaceFragment(R.id.frame_ops, SettingGoodsFm.newInstance(), false);
    }


    public void btn_ivbgs(View view) {
        if (flag == 1) {
            tv_home.setVisibility(View.INVISIBLE);
            iv_home.setVisibility(View.INVISIBLE);
            iv_back.setVisibility(View.VISIBLE);
            replaceFragment(R.id.frame_ops, SettingFragment.newInstance(), false);
            if (ins_flag == 1) {
                MainCommunicate.getInstance().change_normal();
            }
        } else {
            goPro();
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause: " + "进入暂停");
        if (mTimer != null) {
            mTimer.cancel();
        }
        super.onPause();
    }
}
