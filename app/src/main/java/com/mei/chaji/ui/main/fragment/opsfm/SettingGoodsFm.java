package com.mei.chaji.ui.main.fragment.opsfm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.mei.chaji.R;
import com.mei.chaji.app.Constants;
import com.mei.chaji.base.fragment.BaseFragment;
import com.mei.chaji.core.bean.main.GoodService;
import com.mei.chaji.core.http.api.RetrofitServiceManager;
import com.mei.chaji.core.rxretorfit.MyObserver;
import com.mei.chaji.ui.main.activity.OpsActivity;
import com.mei.chaji.utils.CommonUtils;
import com.mei.chaji.utils.ECBAESUtils;
import com.vondear.rxtool.view.RxToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SettingGoodsFm extends BaseFragment {
    String TAG = "SettingGoodsFm";
    //    @BindView(R.id.btn_cancel_clearmode)
//    Button btn_cancel_clearmode;
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    //    private Dialog dialog;
//    private View inflate;
    @BindView(R.id.iv_line1)
    ImageButton iv_line1;
    @BindView(R.id.iv_line2)
    ImageButton iv_line2;
    @BindView(R.id.btn_Sub1)
    ImageView btn_Sub1;
    @BindView(R.id.btn_Sub2)
    ImageView btn_Sub2;
    @BindView(R.id.btn_add1)
    ImageView btn_add1;
    @BindView(R.id.btn_add2)
    ImageView btn_add2;
    @BindView(R.id.tv_cup_a)
    EditText tv_cup_a;
    @BindView(R.id.tv_cup_b)
    EditText tv_cup_b;
    @BindView(R.id.tv_tea1_name)
    TextView tv_tea1_name;
    @BindView(R.id.tv_tea2_name)
    TextView tv_tea2_name;
    @BindView(R.id.btn_confirm)
    Button btn_confirm;
    List<GoodService> goodServices = new ArrayList<>();
    String goods_id1, goods_id2, goods_id, g_type;
    Gson gson = new Gson();
    boolean open_one, open_two;
    String finally_price1, finally_id1, finally_name1, finally_price2, finally_id2, finally_name2;


    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        Log.e("点击时间", "initListener: " + "33333");
        RxView.clicks(iv_line1).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> iv_line1s());
        RxView.clicks(iv_line2).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> iv_line2s());
        RxView.clicks(btn_confirm).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> btn_confirms());

//        tv_cup_a.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//               finally_price1 = s.toString();
//                Log.e(TAG, "afterTextChanged: "+finally_price1 );
//            }
//        });

//        tv_cup_b.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                finally_price2 = s.toString();
//                Log.e(TAG, "afterTextChanged: "+finally_price2 );
//            }
//        });
    }

    protected void initUI() {
        finally_id1 = sp.getString("finally_id1","");
        finally_id2 = sp.getString("finally_id2","");
    }

    protected void initData() {
        cachedThreadPool.execute(() -> {
            Map<String, String> map = new HashMap<>();
            map.put("appId", "APP0000001");
            map.put("secret", "chaji20190505");
            map.put("deviceNo", device_No);
            String jsonbody = gson.toJson(map);
            Log.e(TAG, "initData: " + jsonbody);
            String aa = ECBAESUtils.encrypt(Constants.AES_KEY, jsonbody);
            RetrofitServiceManager.getAPIService().getAllGoodService(aa)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver<String>() {

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(String s) {
                            if (goodServices != null) {
                                goodServices.clear();
                            } else {
                                goodServices = new ArrayList<>();
                            }
                            try {
                                String decryStr = ECBAESUtils.decrypt(Constants.AES_KEY, s);
                                Log.e("解密", "onSuccess: " + decryStr);
                                //第一步：先获取jsonobject对象
                                com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(decryStr);
                                //第二步：把对象转换成jsonArray数组
                                boolean result = jsonObject1.getBoolean("result");
                                JSONArray array = jsonObject1.getJSONArray("rows");
                                if (array==null||array.size()==0)
                                    return;
                                //第三步：将字符串转成list集合
                                goodServices = com.alibaba.fastjson.JSONObject.parseArray(array.toJSONString(), GoodService.class);//把字符串转换成集合
                                if (result) {
                                    if (goodServices.size() > 0) {
                                        String isFree = goodServices.get(0).getIsFree();
//                                    goods_id = goodServices.get(0).getGoodsId();
                                        if (isFree.equals("0")) {
                                            tv_cup_b.setText(goodServices.get(1).getGoodsPrice());
                                            tv_cup_a.setText(goodServices.get(0).getGoodsPrice());
                                            finally_price1 = goodServices.get(0).getGoodsPrice();
                                            finally_price2 = goodServices.get(1).getGoodsPrice();
                                        } else {
                                            tv_cup_a.setText("0.00");
                                            tv_cup_b.setText("0.00");
                                            finally_price1 = "0.00";
                                            finally_price2 = "0.00";
                                        }
                                        tv_tea1_name.setText(goodServices.get(0).getGoodsName());
                                        tv_tea2_name.setText(goodServices.get(1).getGoodsName());
                                        finally_name1 = goodServices.get(0).getGoodsName();
                                        finally_name2 = goodServices.get(1).getGoodsName();
                                        finally_id1 = goodServices.get(0).getGoodsId();
                                        finally_id2 = goodServices.get(1).getGoodsId();
                                        if (goodServices.get(0).getOneGargoWay().equals("1")) {
                                            open_one = true;
                                        } else {
                                            Log.e(TAG, "onSuccess: " + "一货道已经关闭");
                                            RxToast.warning("一货道已经关闭");
                                            open_one = false;
                                        }
                                        if (goodServices.get(1).getOneGargoWay().equals("1")) {
                                            open_two = true;
                                        } else {
                                            Log.e(TAG, "onSuccess: " + "二货道已经关闭");
                                            RxToast.warning("二货道已经关闭");
                                            open_two = false;
                                        }
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
        });
    }


    private void btn_confirms() {
        Log.e(TAG, "btn_confirms: " + "点击事件" + finally_name1 + finally_name2);
        Log.e(TAG, "btn_confirms: "+"获取商品id"+finally_id1+finally_id2 );
        cachedThreadPool.execute(() -> {
            Map<String, String> map = new HashMap<>();
            map.put("appId", "APP0000001");
            map.put("secret", "chaji20190505");
            map.put("deviceNo", device_No);
            map.put("userId", user_id);
            map.put("goodsIdOne", finally_id1);
            map.put("goodsIdTwo", finally_id2);
            map.put("goodsNameOne", CommonUtils.encodeURI(finally_name1));
            map.put("goodsNameTwo", CommonUtils.encodeURI(finally_name2));
            map.put("goodsRealPriceOne", finally_price1);
            map.put("goodsRealPriceTwo", finally_price2);
            String aa = CommonUtils.getGsonEsa(map);
            RetrofitServiceManager.getAPIService().bindDeviceGoods(aa).observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver<String>() {
                        @Override
                        public void onSuccess(String response) {
                            String de_responst = ECBAESUtils.decrypt(Constants.AES_KEY, response);
                            Log.e(TAG, "onSuccess: " + de_responst);
                            //第一步：先获取jsonobject对象
                            com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(de_responst);
                            //第二步：把对象转换成jsonArray数组
                            boolean result = jsonObject1.getBoolean("result");
                            String msg = jsonObject1.getString("msg");
                            if (result) {
                                RxToast.normal(msg);
                                OpsActivity activity = (OpsActivity) getActivity();
                                if (activity != null) {
                                    activity.goHome();
                                }
                            } else {
                                RxToast.normal(msg);
                            }
                        }
                    });
        });
//        if (finally_name1.equals(finally_name2)) {
//            RxToast.normal("两个货道不能选择一样的商品");
//        } else {
//            Log.e(TAG, "btn_confirms: " + "不一样的商品");
//            cachedThreadPool.execute(() -> {
//                Map<String, String> map = new HashMap<>();
//                map.put("appId", "APP0000001");
//                map.put("secret", "chaji20190505");
//                map.put("deviceNo", device_No);
//                map.put("userId", user_id);
//                map.put("goodsIdOne", finally_id1);
//                map.put("goodsIdTwo", finally_id2);
//                map.put("goodsNameOne", CommonUtils.encodeURI(finally_name1));
//                map.put("goodsNameTwo", CommonUtils.encodeURI(finally_name2));
//                map.put("goodsRealPriceOne", finally_price1);
//                map.put("goodsRealPriceTwo", finally_price2);
//                String aa = CommonUtils.getGsonEsa(map);
//                RetrofitServiceManager.getAPIService().bindDeviceGoods(aa).observeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new MyObserver<String>() {
//                            @Override
//                            public void onSuccess(String response) {
//                                String de_responst = ECBAESUtils.decrypt(Constants.AES_KEY, response);
//                                Log.e(TAG, "onSuccess: " + de_responst);
//                                //第一步：先获取jsonobject对象
//                                com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(de_responst);
//                                //第二步：把对象转换成jsonArray数组
//                                boolean result = jsonObject1.getBoolean("result");
//                                String msg = jsonObject1.getString("msg");
//                                if (result) {
//                                    RxToast.normal(msg);
//                                    OpsActivity activity = (OpsActivity) getActivity();
//                                    if (activity != null) {
//                                        activity.goHome();
//                                    }
//                                } else {
//                                    RxToast.normal(msg);
//                                }
//                            }
//                        });
//            });
//        }
    }

    private void iv_line1s() {
        Log.e("======", "iv_line1s: " + "11111");
//        Bundle bundle = getArguments();
//        bundle.putString("goods_key","1");
        try {
            goods_id1 = goodServices.get(0).getGoodsId();
        }catch (Exception e){
            e.printStackTrace();
        }
        g_type = "1";
        goDialog(g_type);
//        if (open_one) {
//            goods_id1 = goodServices.get(0).getGoodsId();
//            g_type = "1";
//            goDialog(g_type);
//        } else {
//            RxToast.warning("A货道未开启");
//        }
    }

    private void iv_line2s() {
        Log.e("======", "iv_line1s: " + "222222");
//        Bundle bundle = getArguments();
//        bundle.putString("goods_key","2");
//        goods_id = goods_id2;
        try {
            goods_id2 = goodServices.get(1).getGoodsId();
        }catch (Exception e){
            e.printStackTrace();
        }
        g_type = "2";
        goDialog(g_type);
//        if (open_two) {
//            goods_id2 = goodServices.get(1).getGoodsId();
//            g_type = "2";
//            goDialog(g_type);
//        } else {
//            RxToast.warning("B货道未开启");
//        }
    }


    public static SettingGoodsFm newInstance() {
        return new SettingGoodsFm();
    }

    public void goDialog(String g_id) {
        GoodsDialogFm dialogFragment = new GoodsDialogFm();
        Bundle bundle = new Bundle();
        bundle.putString("g_id1", goods_id1);
        bundle.putString("g_id2", goods_id2);
        bundle.putString("g_type", g_type);
        dialogFragment.setArguments(bundle);
        dialogFragment.setTargetFragment(SettingGoodsFm.this, 1234);
        if (getFragmentManager() != null) {
            dialogFragment.show(getFragmentManager(), "GoodsDialogFm");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            g_type = data.getStringExtra("g_type");
            if (g_type.equals("1")) {
                finally_id1 = data.getStringExtra("goods_id");
                finally_name1 = data.getStringExtra("goods_name");
                finally_price1 = data.getStringExtra("goods_price");
                Log.e("查询接口a", "onActivityResult: " + finally_id1);
                tv_cup_a.setText(finally_price1);
                tv_tea1_name.setText(finally_name1);
                editor.putString("finally_id1",finally_id1);
            } else {
                finally_id2 = data.getStringExtra("goods_id");
                finally_name2 = data.getStringExtra("goods_name");
                finally_price2 = data.getStringExtra("goods_price");
                Log.e("查询接口b", "onActivityResult: " + finally_id2);
                tv_cup_b.setText(finally_price2);
                tv_tea2_name.setText(finally_name2);
                editor.putString("finally_id2",finally_id2);
            }
            editor.apply();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected int setContentView() {
        return R.layout.fm_setting_goods;
    }

    @Override
    protected void lazyLoad() {

    }
}
