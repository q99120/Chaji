package com.mei.chaji.ui.main.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;

import com.mei.chaji.R;
import com.mei.chaji.base.activity.BaseActivity;
import com.mei.chaji.utils.WaveProgress;

import butterknife.BindView;

public class DemoActivity extends BaseActivity {
    String TAG = "DemoActivity";
    @BindView(R.id.wave_progress)
    WaveProgress waveProgress;
    MyAsynckTask asynckTask;

    String order_no;
    boolean rufund_result = true;

    /**
     * 判断茶机状态
     */
    private boolean make_pre, make_cup, make_finish, make_pick, no_pick;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_demo;
    }


    @SuppressLint("CheckResult")
    @Override
    protected void initEventAndData() {
        asynckTask = new MyAsynckTask();
        asynckTask.execute();
    }


    @SuppressLint("CheckResult")
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause: " + "生命周期");
        super.onPause();
    }

    @Override
    protected void initUI() {
    }


    private void gobuy() {
        Intent i = new Intent(DemoActivity.this, BuyGoodsActivity.class);
        startActivity(i);
    }


    class MyAsynckTask extends AsyncTask<Integer, Integer, String> {

        @Override
        protected void onPreExecute() {
            waveProgress.setValue(0);
//            waveProgress.setValue(0);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... integers) {
            for (int i = 0; i < 101; i++) {
                //传参数给onProgressUpdate
                publishProgress(i);
                try {
                    Thread.sleep(130);     //休眠1秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.e(TAG, "onProgressUpdate: " + values[0]);
            waveProgress.setValue(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e(TAG, "onPostExecute: " + "结束了");
            waveProgress.setValue(100);
            asynckTask.cancel(true);
            super.onPostExecute(s);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: " + "");
        super.onDestroy();
    }

    //禁止使用返回键返回到上一页,但是可以直接退出程序**
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;//不执行父类点击事件
        }
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

}
