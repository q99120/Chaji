package com.mei.chaji.ui.main.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mei.chaji.R;
import com.mei.chaji.core.bean.main.VideoUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yuandl on 2016-11-17.
 */

public class Fragment2 extends LazyLoadFragment {
    @BindView(R.id.iv_ii)
    ImageView imageView;
    String TAG = "Fragment2";
    static List<VideoUtil> list = new ArrayList<>();
    static String adurl;
    private final int UPTATE_VIEWPAGER = 100;
    static List<String> urls = new ArrayList<>();

    @Override
    protected void initui() {
//        Log.e(TAG, "initui: " + "加载image");
    }

    public static Fragment2 newInstance(String ad_urls, String ad_times) {
//        Log.e("====", "newInstance: " + "初始化2" + ad_urls);
        Fragment2 fragment2 = new Fragment2();
        urls.add(ad_urls);
        return fragment2;
    }

    //接受消息实现轮播
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == UPTATE_VIEWPAGER) {//                    EventBus.getDefault().post(new NormalMsg("pic_finish",false));
//                Log.e(TAG, "handleMessage: " + "数据发送成功");
            }
        }
    };

    /**
     * 发消息，进行循环
     */
    private Runnable runnable = () -> mHandler.sendEmptyMessage(UPTATE_VIEWPAGER);

    @Override
    public int setContentView() {
        return R.layout.fm_layout2;
    }

    @Override
    protected void lazyLoad() {
        String message = "Fragment2" + (isInit ? "已经初始并已经显示给用户可以加载数据" : "没有初始化不能加载数据") + ">>>>>>>>>>>>>>>>>>>";
//        showToast(message);
        if (isInit) {
            for (int i = 0;i<urls.size();i++) {
//                Log.e(TAG, "lazyLoad: " + urls.get(i));
                Glide.with(getActivity()).load(urls.get(i)).into(imageView);
            }
        }
        Log.e(TAG, message);
    }

    @Override
    protected void stopLoad() {
//        Log.e(TAG, "Fragment2" + "已经对用户不可见，可以停止加载数据");
    }
}
