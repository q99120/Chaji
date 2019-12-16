package com.mei.chaji.base.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * MVP模式的Base fragment
 *
 * @author quchao
 * @date 2017/11/28
 */

public abstract class BaseFragment extends Fragment {
    /**
     * 视图是否已经初初始化
     */
    protected boolean isInit = false;
    protected boolean isLoad = false;
    protected final String TAG = "LazyLoadFragment";
    private View view;
    private Unbinder unBinder;
    public SharedPreferences sp;
    public SharedPreferences.Editor editor;
    public String user_id;
    public Gson gson;
    public String device_No;
    public String deviceVersion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(setContentView(), container, false);
        unBinder = ButterKnife.bind(this,view);
        sp = getActivity().getSharedPreferences("mc_info", Context.MODE_PRIVATE);
        editor = sp.edit();
        user_id = sp.getString("userId","");
        device_No = sp.getString("deviceNo","");
        deviceVersion = sp.getString("deviceVersion","");
        gson = new Gson();
        initUI();
        isInit = true;
        /**初始化的时候去加载数据**/
        isCanLoadData();
        initData();
        initListeners();
        return view;
    }

    protected abstract void initListeners();

    protected abstract void initData();

    protected abstract void initUI();


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * 视图是否已经对用户可见，系统的方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCanLoadData();
    }

    /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     */
    private void isCanLoadData() {
        if (!isInit) {
            return;
        }

        if (getUserVisibleHint()) {
            lazyLoad();
            isLoad = true;
        } else {
            if (isLoad) {
                stopLoad();
            }
        }
    }

    /**
     * 视图销毁的时候讲Fragment是否初始化的状态变为false
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;

    }

    protected void showToast(String message) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 设置Fragment要显示的布局
     *
     * @return 布局的layoutId
     */
    protected abstract int setContentView();

    /**
     * 获取设置的布局
     *
     * @return
     */
    protected View getContentView() {
        return view;
    }

    /**
     * 找出对应的控件
     *
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T findViewById(int id) {

        return (T) getContentView().findViewById(id);
    }

    /**
     * 当视图初始化并且对用户可见的时候去真正的加载数据
     */
    protected abstract void lazyLoad();

    /**
     * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以调用此方法
     */
    protected void stopLoad() {
    }

    @Override
    public void onDestroy() {
        //销毁移除当前activity并解绑
        if (unBinder != null && unBinder != Unbinder.EMPTY) {
            unBinder.unbind();
            unBinder = null;
        }
        super.onDestroy();
    }
}
