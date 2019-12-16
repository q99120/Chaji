package com.mei.chaji.utils;

import android.content.Context;

import com.mei.chaji.app.Contact;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttManager {
    private static final String TAG = MqttManager.class.getSimpleName();
    private  final String serverUri = Contact.mq;//服务器地址（协议+地址+端口号）
    private  final String userName = "tmomp";//用户名
    private final  String passWord = "tmomp@12345";//密码
    private MqttAndroidClient mqttAndroidClient;
    private static volatile MqttManager mqttManager = null;
    private String mqttTopic;
    private int qos;
    private MqttCallback callback;
    
    public static MqttManager getInstance(Context context, String strings, int qos, MqttCallback callback) {
        if (mqttManager == null) {
            synchronized (MqttManager.class) {
                if (mqttManager == null) {
                    mqttManager = new MqttManager(context, strings, qos, callback);
                }
            }
        }
        return mqttManager;
    }

    private MqttManager(Context context, String strings, int qos, MqttCallback callback) {
        this.callback = callback;
        this.mqttTopic = strings;
        this.qos = qos;
        initConnect(context.getApplicationContext());
    }

    private void initConnect(Context context) {
        if (mqttAndroidClient == null) {
            synchronized (MqttAndroidClient.class) {
                if (mqttAndroidClient == null) {
                    mqttAndroidClient = new MqttAndroidClient(context, serverUri, MqttClient.generateClientId());
                }
            }
        }
        try {
        mqttAndroidClient.registerResources(context);
        mqttAndroidClient.setCallback(new MqttCallbackExtended(){

            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                subscribeToTopic(reconnect, context);
            }
        });
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        //设置自动重连
        mqttConnectOptions.setAutomaticReconnect(true);
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录
        // 这里设置为true表示每次连接到服务器都以新的身份连接
        mqttConnectOptions.setCleanSession(false);
        //设置连接的用户名
        mqttConnectOptions.setUserName(userName);
        //设置连接的密码
        mqttConnectOptions.setPassword(passWord.toCharArray());
        // 设置超时时间 单位为秒
        mqttConnectOptions.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        mqttConnectOptions.setKeepAliveInterval(20);
            mqttAndroidClient.connect(mqttConnectOptions,context, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            callback.connectFail(e.toString());
        }
    }

    private void subscribeToTopic(final boolean reconnect, Context context) {
        try {
            mqttAndroidClient.subscribe(mqttTopic, qos, context, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    callback.subscribedSuccess(reconnect);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    LogPlus.e("订阅失败");
                    callback.connectFail(exception.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.connectFail(e.toString());
        }
    }


    public interface MqttCallback {
        void subscribedSuccess(boolean reconnect);

        void receiveMessage(String topic, MqttMessage message);

        void connectFail(String message);

        void connectLost(String message);
    }
}
