package com.mei.chaji.ui.main.fragment;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mei.chaji.R;
import com.mei.chaji.core.bean.main.VideoUtil;
import com.mei.chaji.ui.main.activity.AdpicActivity;
import com.mei.chaji.ui.other.MVideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

/**
 * Created by yuandl on 2016-11-17.
 */

public class Fragment1 extends LazyLoadFragment {
    private static String TAG = "Fragment1";
    static VideoUtil videoUtil;
    int i = 0;
    static int index;
    static String ad_url;
    static List<VideoUtil> lists = new ArrayList<>();
    @BindView(R.id.videoview)
    MVideoView test_vi;
    @BindView(R.id.image)
    ImageView image;

    public static Fragment1 newInstance(List<VideoUtil> list) {
        Fragment1 fragment1 = new Fragment1();
        lists = list;
        Log.e(TAG, "newInstance: " + ad_url);
        return fragment1;
    }

    @Override
    protected void initui() {

    }


    public void updateDate(int indexs) {
        index = indexs;
        Log.e(TAG, "updateDate: " + indexs);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (index < lists.size() - 1) {
                    index += 1;
                } else {
                    index = 0;
                }
                AdpicActivity activity = (AdpicActivity) getActivity();
                //等同于if(activity!=null)
                Objects.requireNonNull(activity).gocurret(index);
            }
        }
    };

    @Override
    public int setContentView() {
        return R.layout.fm_layout1;
    }

    @Override
    protected void lazyLoad() {
        String message = "Fragment1" + (isInit ? "已经初始并已经显示给用户可以加载数据" : "没有初始化不能加载数据") + ">>>>>>>>>>>>>>>>>>>";
        if (isInit) {
            String url, ad_time;
            if (lists.size() > 1) {
                Log.e(TAG, "lazyLoad: " + index);
//                if (index == 0){
//                  index = lists.size()-1;
//                }else if (index == lists.size()+1){
//                    index = 0;
//                }else {
//                    index = lists.size()-1;
//                }
                url = lists.get(index).getAd_url();
//                Toast.makeText(getActivity(),url+"",Toast.LENGTH_LONG).show();
                if (MimeTypeMap.getFileExtensionFromUrl(lists.get(index).getAd_url()).equals("mp4")) {
                    Log.e(TAG, "视频地址: " + lists.get(index).getAd_url());
                    test_vi.setVisibility(View.VISIBLE);
                    test_vi.setVideoPath(lists.get(index).getAd_url());
                    test_vi.start();
                    test_vi.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (index < lists.size() - 1) {
                                index += 1;
                            } else {
                                index = 0;
                            }
                            finishplay(index);
                        }
                    });

                } else {
                    test_vi.pause();
//                    Log.e(TAG, "图片地址: " + lists.get(index).getAd_url());
                    test_vi.setVisibility(View.INVISIBLE);
                    image.setVisibility(View.VISIBLE);
                    Glide.with(this).load(lists.get(index).getAd_url()).apply(new RequestOptions().centerCrop()).into(image);
                    int times = Integer.parseInt((lists.get(index).getAd_time())) * 1000;
                    Log.e(TAG, "时间是多少: " + times);
                    handler.removeMessages(1);
                    handler.sendEmptyMessageDelayed(1, times);
                }
            } else if (lists.size() == 1) {
                Log.e(TAG, "setDataList: " + "111111");
                if (MimeTypeMap.getFileExtensionFromUrl(lists.get(0).getAd_url()).equals("mp4")) {
                    test_vi.setVisibility(View.VISIBLE);
                    test_vi.setVideoPath(lists.get(0).getAd_url());
                    test_vi.start();
                    test_vi.setOnPreparedListener(mp -> mp.setLooping(true));
                } else {
                    test_vi.pause();
                    test_vi.setVisibility(View.INVISIBLE);
                    image.setVisibility(View.VISIBLE);
                    Glide.with(this).load(lists.get(0).getAd_url()).apply(new RequestOptions().centerCrop()).into(image);
                }
            }
        } else {
            showToast(message);
        }
    }

    private void finishplay(int i) {
        AdpicActivity activity = (AdpicActivity) getActivity();
        activity.gocurret(i);
    }

    @Override
    protected void stopLoad() {
//        Log.e(TAG, "Fragment1" + "已经对用户不可见，可以停止加载数据");
//        test_vi.pause();
        test_vi.stopPlayback();
        test_vi.setVisibility(View.INVISIBLE);
    }
}
