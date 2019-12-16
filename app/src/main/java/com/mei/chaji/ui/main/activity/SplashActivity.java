package com.mei.chaji.ui.main.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.mei.chaji.R;
import com.mei.chaji.app.ChajiAPP;
import com.mei.chaji.app.Constants;
import com.mei.chaji.app.Contact;
import com.mei.chaji.base.activity.BaseActivity;
import com.mei.chaji.component.ActivityController;
import com.mei.chaji.core.http.api.RetrofitServiceManager;
import com.mei.chaji.core.rxretorfit.MyObserver;
import com.mei.chaji.service.AlarmService;
import com.mei.chaji.service.MQTTService;
import com.mei.chaji.utils.CommonUtils;
import com.mei.chaji.utils.ECBAESUtils;
import com.mei.chaji.utils.Install;
import com.mei.chaji.utils.SpUtils;
import com.mei.chaji.utils.TTSUtils;
import com.vondear.rxtool.view.RxToast;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android_serialport_api.SerialPort;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.mei.chaji.app.Constants.Permissions;

//引导页，第一次进入时加载

public class SplashActivity extends BaseActivity {
    boolean first;
    //串口操作
    protected ChajiAPP chajiAPP;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;

    private Dialog dialog;
    private View inflate;

    String deviceNo;
    //文件路径
    private String filepath;
    @BindView(R.id.ll_check_network)
    LinearLayout ll_check_network;
    @BindView(R.id.tv_net_status)
            TextView tv_net_status;
    Button btn_confirm;
    EditText et_deviceNo;
    String device_id, mac_id, icc_id, table_name;
    private static String TAG = "SplashActivity";
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();


    public static final int RC_CAMERA_AND_STORAGE = 120;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                case 3:
                    editor.putBoolean("reset_clock",false);
                    editor.apply();
                    Intent i = new Intent(SplashActivity.this, AdpicActivity.class);
                    startActivity(i);
                    break;
                case 2:
                    if (NetworkUtils.isConnected()) {
                        ll_check_network.setVisibility(View.INVISIBLE);
                        tv_net_status.setText("网络可用");
                        initservice();
                    } else {
                        tv_net_status.setText("网络不可用,2秒后进入首页");
                        SpUtils.put(SplashActivity.this,"net_flag",false);
                        mHandler.sendEmptyMessageDelayed(1,2000);
                    }
                    break;
            }
        }
    };
    private PendingIntent pi;
    private AlarmManager manager;


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        TTSUtils.getInstance().pauseSpeech();
        mHandler.removeMessages(3);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        manager.cancel(pi);
        Log.e(TAG, "onDestroy: " + "停止计时了");
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initEventAndData() {
        ActivityController.getInstance().addActivity(this);
        initPermission();
        initdialog();
    }

    /**
     * 第一件事情请求权限
     */
    @AfterPermissionGranted(RC_CAMERA_AND_STORAGE)
    public void initPermission() {
        if (EasyPermissions.hasPermissions(this, Permissions)) {

        } else {
            EasyPermissions.requestPermissions(this, "需要摄像头权限以及存储权限",
                    RC_CAMERA_AND_STORAGE, Permissions);
        }
    }

    private void initdialog() {//自定义dialog显示布局
        inflate = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null);
        et_deviceNo = inflate.findViewById(R.id.et_deviceNo);
        btn_confirm = inflate.findViewById(R.id.btn_confirm);
        dialog = new Dialog(this, R.style.DialogCentre);
        //点击其他区域消失
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(inflate);
    }


    private void initservice() {
        ll_check_network.setVisibility(View.INVISIBLE);
        TTSUtils.getInstance().speak("你好，欢迎使用");
            SpUtils.put(SplashActivity.this,"net_flag",true);
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        mac_id = DeviceUtils.getMacAddress();
        icc_id = tm.getSimSerialNumber();

        table_name = Build.DEVICE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            device_id = tm.getImei();
        } else {
            device_id = tm.getDeviceId();
        }
        Log.e(TAG, "initservice: " + table_name + Build.PRODUCT + device_id + mac_id);

        if (device_id == null) {
            String uuid = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
            Log.e(TAG, "initserviceuu: " + uuid);
            device_id = uuid;
        }
        if (device_id != null && mac_id != null) {
//            RxToast.normal("获取设备信息成功" + table_name);
            RxToast.normal("获取成功iccid:" + icc_id + "mac_id:" + mac_id);
            editor.putString("device_id", device_id);
            editor.putString("mac_id", mac_id);
            editor.putString("table_name", table_name);
            editor.apply();
            deviceNo = sp.getString("deviceNo", "");
            if (deviceNo.equals("")) {
                RxToast.normal("设备未激活,请先激活");
                Log.e(TAG, "initEventAndData: " + "设备未激活,请先激活");
                dialog.show();
                dialog.setCancelable(false);
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, String> map = new HashMap<>();
                        map.put("appId", "APP0000001");
                        map.put("secret", "chaji20190505");
                        map.put("deviceNo", et_deviceNo.getText().toString().trim().toLowerCase());
                        map.put("padImei", device_id);
                        map.put("padMac", mac_id);
                        map.put("padName", table_name);
                        map.put("deviceVersion", "1.1.0");
                        String aa = CommonUtils.getGsonEsa(map);
                        cachedThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                RetrofitServiceManager.getAPIService().updateDeviceActivation(aa)
                                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new MyObserver<String>() {

                                            @Override
                                            public void onSuccess(String s) {
                                                String de_s = ECBAESUtils.decrypt(Constants.AES_KEY, s);
                                                JSONObject jsonObject1 = JSONObject.parseObject(de_s);
                                                //第二步：把对象转换成jsonArray数组
                                                boolean result = jsonObject1.getBoolean("result");
                                                String msg = jsonObject1.getString("msg");
                                                if (result) {
                                                    deviceNo = jsonObject1.getString("deviceNo");
                                                    RxToast.normal(msg);
                                                    editor.putString("deviceNo", deviceNo);
                                                    editor.putString("deviceVersion", "1.1.0");
                                                    editor.apply();
                                                    Message message = new Message();
                                                    message.what = 1;
                                                    if (deviceNo != null) {
                                                        dialog.dismiss();
                                                        getVersions();
                                                    }
                                                } else {
                                                    RxToast.normal(msg);
                                                }
                                            }
                                        });
                            }
                        });
                    }
                });
            } else {
                getVersions();
            }


        }

        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "chaji" + File.separator;
        Log.e("========", "initEventAndData: " + "进入页面");
        File file = new File(filepath);
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    private void getVersions() {
        RxToast.normal("设备已激活,查询是否需要更新中。。");
        TTSUtils.getInstance().speak("设备已激活,查询是否需要更新中。。");
        mHandler.sendEmptyMessageDelayed(3,6000);
        Map<String, String> map = new HashMap<>();
        map.put("appId", "APP0000001");
        map.put("secret", "chaji20190505");
        map.put("deviceNo", deviceNo);
        map.put("deviceVersion", "1.1.0");
        String aa = CommonUtils.getGsonEsa(map);
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                RetrofitServiceManager.getAPIService().findDeviceVersionInfo(aa).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MyObserver<String>() {

                            @Override
                            public void onSuccess(String response) {
                                String de_s = ECBAESUtils.decrypt(Constants.AES_KEY, response);
                                mHandler.removeMessages(3);
                                Log.e(TAG, "获取服务器更新状态: " + de_s);
                                JSONObject j1 = JSONObject.parseObject(de_s);
                                //第二步：把对象转换成jsonArray数组
                                boolean result = j1.getBoolean("result");
                                Log.e(TAG, "onSuccessres: "+result );
//                                        String msg = j1.getString("msg");
                                if (result) {
                                    JSONObject row_ob = j1.getJSONObject("row");
                                    String updateVersionId = row_ob.getString("updateVersionId");
                                    String versionUrl = row_ob.getString("versionUrl");
                                    Log.e(TAG, "获取当前版本URL: "+versionUrl );
//                                            if (versionUrl ==null){
//                                                mHandler.sendEmptyMessageDelayed(1, 2000);
//                                            }
                                    editor.putString("updateVersionId", updateVersionId);
                                    editor.apply();
                                    if (versionUrl!=null){
                                        String url = Contact.p_url + File.separator + "file" + File.separator + versionUrl.replace("\\", "/");
                                        Log.e(TAG, "onSuccess: "+url );
                                        File file = new File(filepath + File.separator + "茶机.apk");
                                        if (file.exists() && file.isFile()) {
                                            if (file.delete()) {
                                                createDownloadTask(filepath + "茶机" + ".apk", url);
                                            }
                                        } else {
                                            createDownloadTask(filepath + "茶机" + ".apk", url);
                                        }
                                    }else {
                                        RxToast.warning("获取url失败，更新失败,将直接跳转");
                                        mHandler.sendEmptyMessageDelayed(1, 2000);
                                    }
                                }else {
                                    mHandler.sendEmptyMessageDelayed(1, 2000);
                                }
                            }
                        });
            }
        });

    }

    public void createDownloadTask(String path1, String adurl) {
        Log.e(TAG, "createDownloadTask: " + "下载");
        FileDownloader.getImpl().create(adurl)
                .setPath(path1)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        if (isContinue) {

                        } else {
                        }
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.e(TAG, "progress: " + soFarBytes);
                        String result_far;
                        result_far = CommonUtils.fileSize((long) soFarBytes);
                        String result_total;
                        result_total = CommonUtils.fileSize((long) totalBytes);
                        RxToast.normal("程序升级中..总共需要更新" + result_total + "，已经更新" + result_far);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Log.e(TAG, "completed: " + "apk下载完成");
                        RxToast.normal("apk下载完成");
                        boolean is = Install.isRoot();
                        if (is) {
                            Log.e(TAG, "completed: " + "静默安装");
                            Install.install(filepath + "茶机.apk");
                        } else {
                            Log.e(TAG, "completed: " + "手机没有root");
                        }

                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.e(TAG, "paused: " + "暂停下载");

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
//                        RxToast.normal("出错了" + e.toString());
                        Log.e(TAG, "error: " + "报错体系");
                        Log.e(TAG, "paused: " + e.toString());
                        updataError();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                }).start();
    }

    private void updataError() {
        String updateVersionId = sp.getString("updateVersionId", "");
        Map<String, String> map = new HashMap<>();
        map.put("appId", "APP0000001");
        map.put("secret", "chaji20190505");
        map.put("deviceNo", deviceNo);
        map.put("updateVersionId", updateVersionId);
        map.put("status", "2");
        String aa = CommonUtils.getGsonEsa(map);
        RetrofitServiceManager.getAPIService().alertUpVersion(aa)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>() {
                    @Override
                    public void onSuccess(String response) {
                        String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, response);
                        JSONObject jsonObject1 = JSONObject.parseObject(decryStr);
                        //第二步：把对象转换成jsonArray数组
                        boolean result = jsonObject1.getBoolean("result");
                        if (result) {
                            RxToast.normal("超时提示成功");
                            mHandler.sendEmptyMessageDelayed(1, 2000);
                        }
                    }
                });
    }

    @Override
    protected void initUI() {
        Intent intent = new Intent(this, AlarmService.class);
        startService(intent);
    }



    private void network() {
        ll_check_network.setVisibility(View.VISIBLE);
        TTSUtils.getInstance().init();
        /**
         * 判断网络状态
         */
        if (NetworkUtils.isConnected()) {
            initservice();
        } else {
            mHandler.sendEmptyMessageDelayed(2, 10000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 120) {
            network();
        }else {
            RxToast.warning("获取权限失败,请重新获取");
        }
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


}
