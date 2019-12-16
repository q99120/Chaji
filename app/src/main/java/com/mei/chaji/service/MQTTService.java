package com.mei.chaji.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.mei.chaji.R;
import com.mei.chaji.app.ChajiAPP;
import com.mei.chaji.app.Constants;
import com.mei.chaji.app.Contact;
import com.mei.chaji.core.bean.main.Mqttmessages;
import com.mei.chaji.utils.MqttManager;
import com.vondear.rxtool.view.RxToast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;

import static com.blankj.utilcode.util.CrashUtils.init;

public class MQTTService extends Service {
    public String TAG = "MQTTService";
    public String HOST = Contact.mq;//服务器地址（协议+地址+端口号）
    public String USERNAME = "tmomp";//用户名
    public String PASSWORD = "tmomp@12345";//密码

    public static String PUBLISH_TOPIC = "tmomp_mqtt_tobeNo2";//发布主题
    public static String RESPONSE_TOPIC = "message_arrived";//响应主题
    private MqttConnectOptions mMqttConnectOptions;
    int connect_flag;
    String device_nos;

    private static MqttCallbacks callback;

    private static MqttAndroidClient mqttAndroidClient;
    public String CLIENTID = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            ? Build.getSerial() : Build.SERIAL;//客户端ID，一般以客户端唯一标识符表示，这里用设备序列号表示

    private SharedPreferences sp;

    private IGetMessageCallBack IGetMessageCallBack;
    private static MQTTService mqttService;
    Context context = ChajiAPP.getContext();


    @Override
    public void onCreate() {
        super.onCreate();

    }

    public static MQTTService getInstance(MqttCallbacks call) {
        callback = call;
        if (mqttService == null) {
            synchronized (MQTTService.class) {
                if (mqttService == null) {
                    mqttService = new MQTTService();
                }
            }
        }
        return mqttService;
    }




    public void initMq(String TOPIC) {
        PUBLISH_TOPIC = "tmomp_mqtt_" +TOPIC;
        Log.e(TAG, "initMq: " + "2222");
        String serverURI = HOST; //服务器地址（协议+地址+端口号）
        mqttAndroidClient = new MqttAndroidClient(this, serverURI, CLIENTID);
        mqttAndroidClient.setCallback(mqttCallback); //设置监听订阅消息的回调
        mMqttConnectOptions = new MqttConnectOptions();
        mMqttConnectOptions.setCleanSession(true); //设置是否清除缓存
        mMqttConnectOptions.setConnectionTimeout(10); //设置超时时间，单位：秒
        mMqttConnectOptions.setKeepAliveInterval(20); //设置心跳包发送间隔，单位：秒
        mMqttConnectOptions.setUserName(USERNAME); //设置用户名
        mMqttConnectOptions.setPassword(PASSWORD.toCharArray()); //设置密码
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + CLIENTID + "\"}";
        String topic = PUBLISH_TOPIC;
        Integer qos = 2;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            try {
                MqttMessage message1 = new MqttMessage();
                String bean  = "0";
                String topic1 = "tmomp/mqtt/"+device_nos;
                mMqttConnectOptions.setWill(topic1, bean.getBytes(), qos, retained.booleanValue());
            } catch (Exception e) {
                Log.i(TAG, "Exception Occured", e);
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }
        Log.e(TAG, "initMq: " + doConnect);
        if (doConnect) {
            doClientConnection(context);
        }
    }

    public  void doClientConnection(Context context) {
        callback.connectRequest();
        if (!mqttAndroidClient.isConnected() && isConnectIsNomarl(context)) {
            try {
                mqttAndroidClient.connect(mMqttConnectOptions, null, iMqttActionListener);
                Log.e(TAG, "doClientConnection: " + "请求连接");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.e(TAG, "当前网络名称：" + name);
            return true;
        } else {
            Log.e(TAG, "没有可用网络");
            /*没有可用网络的时候，延迟3秒再尝试重连*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doClientConnection(context);
                }
            }, 3000);
            return false;
        }
    }

    //MQTT是否连接成功的监听
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            callback.subscribedSuccess(PUBLISH_TOPIC+"连接成功");
            Log.e(TAG, "连接成功 " + arg0);
            connect_flag = 0;
//            EventBus.getDefault().post(new Mqttmessages(true));
            RxToast.normal("第三方通知连接成功");
            try {
                if (mqttAndroidClient != null) {
                    mqttAndroidClient.subscribe(PUBLISH_TOPIC, 2);//订阅主题，参数：主题、服务质量
                    MqttMessage message = new MqttMessage();
                    String bean  = "1";
                    String topic = "tmomp/mqtt/"+device_nos;
                    message.setPayload(bean.getBytes());
                    mqttAndroidClient.publish(topic,message);
                } else {
                    init();
                    mqttAndroidClient.subscribe(PUBLISH_TOPIC, 2);//订阅主题，参数：主题、服务质量
                    MqttMessage message = new MqttMessage();
                    String bean  = "1";
                    String topic = "tmomp/mqtt/"+device_nos;
                    message.setPayload(bean.getBytes());
                    mqttAndroidClient.publish(topic,message);
                }
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable trowble) {
            trowble.printStackTrace();
            callback.connectFail(trowble.toString());
            Log.e(TAG, "连接失败 ");
            doClientConnection(context);//连接失败，重连（可关闭服务器进行模拟,这里是连接过程
        }
    };


    //订阅主题的回调
    private MqttCallback mqttCallback = new MqttCallback() {


        @Override
        public void connectionLost(Throwable cause) {
            callback.connectLost(cause.toString());
            connect_flag += 1;
//            EventBus.getDefault().post(new Mqttmessages(false));
            doClientConnection(context);
            Log.e(TAG, "connectionLost: " + connect_flag);
            //如果断线请求达到20次就重启设备
//            if (connect_flag == 20) {
//                try {
//                    Runtime.getRuntime().exec("su -c reboot");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.e(TAG, "messageArrived: " + topic + "msg" + message);
            parseJSONWithJSONObject(new String(message.getPayload()));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.e(TAG, "deliveryComplete: " + token.toString());
        }
    };

    public void setIGetMessageCallBack(IGetMessageCallBack IGetMessageCallBack) {
        this.IGetMessageCallBack = IGetMessageCallBack;
    }


    @TargetApi(Build.VERSION_CODES.O)
    public void createNotificationChannel() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 通知渠道的id
        String id = "my_channel_01";
        // 用户可以看到的通知渠道的名字.
        CharSequence name = getString(R.string.channe_title);
//         用户可以看到的通知渠道的描述
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
//         配置通知渠道的属性
        mChannel.setDescription(description);
//         设置通知出现时的闪灯（如果 android 设备支持的话）
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
//         设置通知出现时的震动（如果 android 设备支持的话）
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//         最后在notificationmanager中创建该通知渠道 //
        mNotificationManager.createNotificationChannel(mChannel);

        // 为该通知设置一个id
        int notifyID = 1;
        // 通知渠道的id
        String CHANNEL_ID = "my_channel_01";
        // Create a notification and set the notification channel.
        Notification notification = new Notification.Builder(this)
                .setContentTitle("New Message").setContentText("You've received new messages.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setChannelId(CHANNEL_ID)
                .build();
        startForeground(1, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(getClass().getName(), "onBind");  sp = getSharedPreferences("mc_info", MODE_PRIVATE);
        String topic = sp.getString("deviceNo", "");
        device_nos = topic;
        initMq(topic);
        return new CustomBinder();
    }



    public class CustomBinder extends Binder {
        public MQTTService getService() {
            return MQTTService.this;
        }
    }

    private void parseJSONWithJSONObject(String JsonData) {
        try {
            JSONObject jsonObject = new JSONObject(JsonData);
            String msg = jsonObject.getString("msg");
            Log.e(TAG, "parseJSONWithJSONObject: " + msg);
            boolean result = jsonObject.getBoolean("result");
            long dateTime = jsonObject.getLong("dateTime");
            String type = jsonObject.getString("type");
//            Toast.makeText(this,"收到推送内容："+type,Toast.LENGTH_LONG).show();
            if (type.equals("1")) {
                EventBus.getDefault().post(new Mqttmessages(msg, result, dateTime, type, "0", 0, 0, 0));
            } else if (type.equals("2")) {
                String orderNo = jsonObject.getString("orderNo");
                int waterTemperature = jsonObject.getInt("waterTemperature");
                int gargoWay = jsonObject.getInt("gargoWay");
                int cupNumber = jsonObject.getInt("cupNumber");
                String orderAmount;
                if (jsonObject.getString("orderAmount") == null) {
                    orderAmount = "0.00";
                } else {
                    orderAmount = jsonObject.getString("orderAmount");
                }
                String preAmount;
                if (jsonObject.getString("preAmount") == null) {
                    preAmount = "0.00";
                } else {
                    preAmount = jsonObject.getString("preAmount");
                }

                ChajiAPP.getInstance().addQueue(orderNo, JsonData);
                EventBus.getDefault().post(new Mqttmessages(msg, result, dateTime, type, orderNo, waterTemperature, gargoWay, cupNumber, orderAmount, preAmount));
            } else if (type.equals("3")) {
                EventBus.getDefault().post(new Mqttmessages(msg, result, dateTime, type, "0", 0, 0, 0));
            } else if (type.equals("4")) {
                String updateVersionId = jsonObject.getString("updateVersionId");
                int versionType = jsonObject.getInt("versionType");
                Log.e(TAG, "parseJSONWithJSONObject: " + updateVersionId);
                Log.e(TAG, "parseJSONWithJSONObject: " + versionType);
                String versionUrl = jsonObject.getString("versionUrl");
                EventBus.getDefault().post(new Mqttmessages(updateVersionId, result, dateTime, type, versionUrl, versionType, 0, 0));
            } else if (type.equals("7")) {
                String userId = jsonObject.getString("userId");
                EventBus.getDefault().post(new Mqttmessages(userId, result, dateTime, type, "0", 0, 0, 0));
            } else if (type.equals("6")) {
                EventBus.getDefault().post(new Mqttmessages(msg, result, dateTime, type, "0", 0, 0, 0));
            }
//            Log.e(TAG, "messageArrived: " + msg + "类型" + result + "通知时间" + dateTime + "通知类型" + type + "订单号" + "112");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface MqttCallbacks {
        void subscribedSuccess(String message);

        void connectFail(String message);

        void connectRequest();

        void connectLost(String message);
    }

}
