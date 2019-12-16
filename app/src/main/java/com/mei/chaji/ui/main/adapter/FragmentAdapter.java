package com.mei.chaji.ui.main.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.mei.chaji.core.bean.main.VideoUtil;
import com.mei.chaji.ui.main.fragment.Fragment1;

import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {
    private FragmentManager fragmentManager;
    private List<VideoUtil> list;

    public FragmentAdapter(FragmentManager fm, List<VideoUtil> videoUtils) {
        super(fm);
        this.fragmentManager = fm;
        this.list = videoUtils;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment1 fragment = Fragment1.newInstance(list);
        Log.e("获取当前适配器", "getItem: "+list.get(position).getAd_url() );
        return fragment;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        // 最简单解决 notifyDataSetChanged() 页面不刷新问题的方法
        return POSITION_NONE;
    }

}
