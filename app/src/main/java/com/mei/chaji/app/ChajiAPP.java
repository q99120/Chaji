package com.mei.chaji.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.dlc.dlclogfile.LogFileUtil;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.mei.chaji.core.exception.MyException;
import com.mei.chaji.service.MQTTService;
import com.mei.chaji.service.MqttServiceConnection;
import com.mei.chaji.utils.LogsFileUtil;
import com.mei.chaji.utils.VideoCacheUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.vondear.rxtool.RxTool;
import com.wyh.plog.core.PLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import io.reactivex.plugins.RxJavaPlugins;
import tech.liujin.manager.NetStateChangeManager;


public class ChajiAPP extends Application {
    private HttpProxyCacheServer proxy;
    public static Context context;

    private HashMap<String, String> queue = new HashMap<>();

    public HashMap<String, String> getQueue() {
        return queue;
    }

    public void addQueue(String key, String value) {
        queue.put(key, value);
    }

    public void removeQueue(String key) {
        if (queue.containsKey(key)) {
            queue.remove(key);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
//        if (context != null) {
//            initMqtt();
//        }
        Logger.addLogAdapter(new AndroidLogAdapter());
        instance = this;
        //初始化工具包
        RxTool.init(this);
        initlog();
        RxJavaPlugins.setErrorHandler(throwable -> {
            //异常处理
//            RxToast.warning("连接服务器失败");
        });
//
        MyException exception = MyException.getInstance(this);
        Thread.setDefaultUncaughtExceptionHandler(exception);

        NetStateChangeManager.create(this);

        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15_000) // set connection timeout.
                        .readTimeout(15_000) // set read timeout.
                ))
                .commit();

//        bugly();

//        CrashReport.initCrashReport(getApplicationContext(), "8e226be55b", true);

    }

    public static Context getContext() {
        return context;
    }

    private void bugly() {
//        Context context = getApplicationContext();
//// 获取当前包名
//        String packageName = context.getPackageName();
//// 获取当前进程名
//        String processName = getProcessName(android.os.Process.myPid());
//// 设置是否为上报进程
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
//        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
//        CrashReport.initCrashReport(context, "8e226be55b", true, strategy);
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    private void initlog() {
        LogsFileUtil.getInstance().init(getApplicationContext());
    }


    //videocache

    public static HttpProxyCacheServer getProxy(Context context) {
        ChajiAPP app = (ChajiAPP) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }


    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(VideoCacheUtils.getVideoCacheDir(this))
                .build();
    }


    private static ChajiAPP instance;
    //    private RefWatcher refWatcher;
    public static boolean isFirstRun = true;
//    private static volatile AppComponent appComponent;
//    private DaoSession mDaoSession;

    static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static synchronized ChajiAPP getInstance() {
        return instance;
    }
//
//    public static RefWatcher getRefWatcher(Context context) {
//        ChajiAPP application = (ChajiAPP) context.getApplicationContext();
//        return application.refWatcher;
//    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onTerminate() {
        NetStateChangeManager.destroy(this);
        super.onTerminate();
    }
}
