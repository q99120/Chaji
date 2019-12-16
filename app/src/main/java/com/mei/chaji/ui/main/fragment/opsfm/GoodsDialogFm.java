package com.mei.chaji.ui.main.fragment.opsfm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.mei.chaji.R;
import com.mei.chaji.app.Constants;
import com.mei.chaji.core.bean.main.GoodsInfo;
import com.mei.chaji.core.http.api.RetrofitServiceManager;
import com.mei.chaji.core.rxretorfit.MyObserver;
import com.mei.chaji.ui.main.adapter.ReclyGridAdapter;
import com.mei.chaji.utils.ECBAESUtils;
import com.vondear.rxtool.view.RxToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GoodsDialogFm extends DialogFragment {
    private ReclyGridAdapter adapter;
    private RecyclerView recyclerView;
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    List<GoodsInfo> goodsInfos = new ArrayList<>();
    String name, price, goods_id1, goods_id2;
    String final_goodsid;
    String g_id, g_type;
    Button btn_confirm,btn_cancel;
    Intent intent = new Intent();

    private Dg_Listener dg_listener;
    String TAG = "GoodsDialogFm";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //去除标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER; //底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        View view = inflater.inflate(R.layout.goods_dialog, null);
        recyclerView = view.findViewById(R.id.rcl_goods);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        GridLayoutManager mgr = new GridLayoutManager(getActivity(), 5);
        recyclerView.setLayoutManager(mgr);
        initData();
        initlistener();
        return view;
    }


    private void initData() {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (goodsInfos != null) {
                    goodsInfos.clear();
                } else {
                    goodsInfos = new ArrayList<>();
                }
                Gson gson = new Gson();
                Map<String, String> map = new HashMap<>();
                map.put("appId", "APP0000001");
                map.put("secret", "chaji20190505");
                map.put("bindGoodsNoOne", goods_id1);
                map.put("bindGoodsNoTwo", goods_id2);
                String jsonbody = gson.toJson(map);
                String aa = ECBAESUtils.encrypt(Constants.AES_KEY, jsonbody);
                RetrofitServiceManager.getAPIService().findClearGoodsList(aa)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MyObserver<String>() {
                            @Override
                            public void onSuccess(String response) {
                                String de_result = ECBAESUtils.decrypt(Constants.AES_KEY, response);
                                //第一步：先获取jsonobject对象
                                Log.e("ahahahha", "onSuccess: " + de_result);
                                com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(de_result);
                                //第二步：把对象转换成jsonArray数组
                                boolean result = jsonObject1.getBoolean("result");
                                JSONArray array = jsonObject1.getJSONArray("rows");
                                //第三步：将字符串转成list集合
                                goodsInfos = com.alibaba.fastjson.JSONObject.parseArray(array.toJSONString(), GoodsInfo.class);//把字符串转换成集合
                                List<GoodsInfo> lists = new ArrayList<>();
                                for (int i = 0; i < goodsInfos.size(); i++) {
                                    lists.add(goodsInfos.get(i));
                                }
                                Log.e("huoqusize", "onSuccess: " + goodsInfos.size());
                                adapter = new ReclyGridAdapter(getActivity(), lists);
                                recyclerView.setAdapter(adapter);
                                adapter.setOnItemClickListener((view, postion) -> {
                                    name = lists.get(postion).getGoodsName();
                                    price = lists.get(postion).getGoodsPrice();
                                    final_goodsid = lists.get(postion).getGoodsId();
                                    Log.e("点击时间", "onSuccess: " + price);
                                    RxToast.normal("选择了" + name);
                                });
                                if (result) {
//                                    Log.e("名字", "onSuccess: " +goodsInfos.get(0).getGoodsName());
                                }
                            }
                        });
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        //设置 dialog 的宽高
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置 dialog 的背景为 null
        getDialog().getWindow().setBackgroundDrawable(null);
        getDialog().setCancelable(false);

    }


    private void initlistener() {
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dg_listener.getDataFrom_Dg(name, price, goods_id, g_type);
                if (name != null) {
                    intent.putExtra("goods_name", name);
                    intent.putExtra("goods_price", price);
                    intent.putExtra("goods_id", final_goodsid);
                    intent.putExtra("g_type", g_type);
                    //获得目标Fragment,并将数据通过onActivityResult放入到intent中进行传值
                    getTargetFragment().onActivityResult(1234, Activity.RESULT_OK, intent);
                    Log.e("111111", "onClick: " + "发送数据成功");
                    dismiss();
                } else {
                  RxToast.normal("请先选择商品");
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    // 回调接口，用于传递数据给Activity -------
    public interface Dg_Listener {
        void getDataFrom_Dg(String name, String price, String goods_id, String g_type);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        if (bundle != null) {
            g_type = bundle.getString("g_type");
            goods_id1 = bundle.getString("g_id1");
            goods_id2 = bundle.getString("g_id2");

        }
    }
}
