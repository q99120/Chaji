package com.mei.chaji.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MqttServiceConnection implements ServiceConnection {
    private MQTTService mqttService;
    private IGetMessageCallBack IGetMessageCallBack;

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mqttService = ((MQTTService.CustomBinder)iBinder).getService();
        mqttService.setIGetMessageCallBack(IGetMessageCallBack);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

}
