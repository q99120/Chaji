package com.mei.chaji.core.exception;

import android.content.Context;
import android.util.Log;

import com.mei.chaji.ui.main.activity.AdpicActivity;
import com.mei.chaji.utils.LogsFileUtil;

/**
 * 全局异常捕获
 */

public class MyException implements Thread.UncaughtExceptionHandler {
    private static MyException myCrashHandler;

    private Context mContext;

    private MyException(Context context) {
        mContext = context;
    }

    public static synchronized MyException getInstance(Context context) {
        if (null == myCrashHandler) {
            myCrashHandler = new MyException(context);
        }
        return myCrashHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        long threadId = thread.getId();
        String message = throwable.getMessage();
        String localizedMessage = throwable.getLocalizedMessage();
        Log.i("KqwException", "------------------------------------------------------");
        Log.i("KqwException", "threadId = " + threadId);
        Log.i("KqwException", "message = " + message);
        Log.i("KqwException", "localizedMessage = " + localizedMessage);
        Log.i("KqwException", "------------------------------------------------------");
//        AdpicActivity.crash("测试闪屏故障"+message);
        throwable.printStackTrace();


//        // TODO 下面捕获到异常以后要做的事情，可以重启应用，获取手机信息上传到服务器等
//        Log.i("KqwException", "------------------应用被重启----------------");
//         重启应用
//        mContext.startActivity(mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName()));
//        //干掉当前的程序
//        android.os.Process.killProcess(android.os.Process.myPid());
        LogsFileUtil.getInstance().addLog("异常crash",message);
    }
}
