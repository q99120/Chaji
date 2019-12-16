package com.mei.chaji.ui.main.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.mei.chaji.R;
import com.mei.chaji.app.Constants;
import com.mei.chaji.base.fragment.BaseFragment;
import com.mei.chaji.core.http.api.RetrofitServiceManager;
import com.mei.chaji.core.rxretorfit.MyObserver;
import com.mei.chaji.ui.main.activity.OpsActivity;
import com.mei.chaji.utils.CommonUtils;
import com.mei.chaji.utils.ECBAESUtils;
import com.mei.chaji.utils.IOUtils;
import com.vondear.rxtool.view.RxToast;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * 后台登陆
 * 账号登陆和扫码登陆
 *
 * @author jyx
 */
public class LoginFragment extends BaseFragment {
    @BindView(R.id.et_account)
    EditText tv_account;
    @BindView(R.id.et_password)
    EditText tv_password;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.qr_login)
    TextView qr_login;
    @BindView(R.id.accout_login)
    TextView accout_login;
    @BindView(R.id.iv_login_qr)
    ImageView iv_login_qr;
    @BindView(R.id.rl_account)
    RelativeLayout rl_account;
    @BindView(R.id.rl_qrcode)
    RelativeLayout rl_qrcode;
    @BindView(R.id.iv_home)
    ImageView iv_home;
    @BindView(R.id.houtaidenglu)
    TextView houtaidenglu;
    @BindView(R.id.iv_mima_eye)
    ImageView iv_mima_eye;
    OpsActivity activity;
    boolean mima_flag;
    private static String TAG = "LoginFragment";

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }


    @SuppressLint("CheckResult")
    @Override
    public void initListeners() {
        RxView.clicks(btn_login).throttleFirst(3, TimeUnit.SECONDS).subscribe((Object o) -> {
            login_ops();
        });
        RxView.clicks(qr_login).subscribe((Object o) -> qr_logins());
        RxView.clicks(accout_login).subscribe((Object o) -> accout_logins());
        RxView.clicks(iv_home).subscribe((Object o) -> iv_homes());
        iv_mima_eye.setOnTouchListener(mimaKeyListener);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initUI() {
//        activity = (OpsActivity) getActivity();
//        if (user_id != null) {
//            activity.goHome();
//        }
        Typeface font_heiti = Typeface.createFromAsset(getActivity().getAssets(), "zhongheiti.ttf");
        accout_login.setTypeface(font_heiti);
        qr_login.setTypeface(font_heiti);
        houtaidenglu.setTypeface(font_heiti);
        btn_login.setTypeface(font_heiti);
        qr_logins();
    }


    private void iv_homes() {
        OpsActivity activity = (OpsActivity) getActivity();
        activity.goPro();
    }

    private void accout_logins() {
        rl_qrcode.setVisibility(View.INVISIBLE);
        qr_login.setVisibility(View.VISIBLE);
        rl_account.setVisibility(View.VISIBLE);
        accout_login.setVisibility(View.INVISIBLE);
        iv_login_qr.setVisibility(View.INVISIBLE);
    }

    private void qr_logins() {
        iv_login_qr.setVisibility(View.VISIBLE);
        rl_qrcode.setVisibility(View.VISIBLE);
        rl_account.setVisibility(View.INVISIBLE);
        accout_login.setVisibility(View.VISIBLE);
        qr_login.setVisibility(View.INVISIBLE);
        RetrofitServiceManager.getAPIService().generateWxLoginQRCode(device_No).
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        Log.e(TAG, "获取二维码图片: " + responseBody);
                        byte[] ss = IOUtils.get_byteimg(responseBody.byteStream());
                        Bitmap bitmap = BitmapFactory.decodeByteArray(ss, 0, ss.length);
                        if (getActivity() != null) {
                            Log.e(TAG, "onSuccess: " + "成功获取图片");
                            iv_login_qr.setImageBitmap(bitmap);
                        }
                    }
                });
    }


    private void login_ops() {
        Map<String, String> map = new HashMap<>();
        /**
         * "appId": "APP0000001",
         *      "secret": "chaji20190505",
         * 	"deviceNo":"1111",
         * 	"account":"admin",
         * 	"password":"21232f297a57a5a743894a0e4a801fc3"
         */
        map.put("appId", "APP0000001");
        map.put("secret", "chaji20190505");
        map.put("deviceNo", device_No);
        Log.e(TAG, "login_ops: " + device_No);
        if (tv_account != null) {
            map.put("account", tv_account.getText().toString().trim());
        }
        if (tv_password != null) {
            map.put("password", tv_password.getText().toString().trim());
        }
        String aes_user = CommonUtils.getGsonEsa(map);
        RetrofitServiceManager.getAPIService().deviceUserLogin(aes_user).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>() {
                    @Override
                    public void onSuccess(String response) {
                        String de_user = ECBAESUtils.decrypt(Constants.AES_KEY, response);
                        if (de_user != null) {
                            com.alibaba.fastjson.JSONObject js1 = com.alibaba.fastjson.JSONObject.parseObject(de_user);
                            boolean result = js1.getBoolean("result");
                            String msg = js1.getString("msg");
                            String userId = js1.getString("userId");
                            editor.putString("userId", userId);
                            editor.apply();
                            if (result) {
                                OpsActivity activity = (OpsActivity) getActivity();
                                activity.goHome();
                            } else {
                                RxToast.normal(msg);
                            }
                        }
                    }
                });
    }

    /**
     * 按住和松手
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected int setContentView() {
        return R.layout.fm_login;
    }

    @Override
    protected void lazyLoad() {

    }

    private View.OnTouchListener mimaKeyListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    tv_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    Log.e(TAG, "onKey: " + "按下");
                    break;
                case KeyEvent.ACTION_UP:
                    Log.e(TAG, "onKey: " + "松手");
                    tv_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    break;
                default:
                    break;
            }
            return true;
        }
    };

}
