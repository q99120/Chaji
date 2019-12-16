package com.mei.chaji.core.bean.main;

public class DeviceBasicParam {

    /**
     * "deviceConfigId": "2", //设备配置主键id
     * "loseCupTime": "10", 	//丢杯时间
     * "hotWaterTemperature": "70",	//热水温度
     * "adVolumeSize": "5", 	//广告音量
     * "deviceVolumeSize": "5", 	//设备音量
     * "standByTime": "10",	//待机时间
     * "maxVolume": "50" 	//货道最大容量
     */

    public String deviceConfigId;
    public String loseCupTime;
    public String hotWaterTemperature;
    public String adVolumeSize;
    public String deviceVolumeSize;
    public String standByTime;
    public String maxVolume;

    public DeviceBasicParam() {
    }


    public DeviceBasicParam(String deviceConfigId, String loseCupTime, String hotWaterTemperature, String adVolumeSize, String deviceVolumeSize, String standByTime, String maxVolume) {
        this.deviceConfigId = deviceConfigId;
        this.loseCupTime = loseCupTime;
        this.hotWaterTemperature = hotWaterTemperature;
        this.adVolumeSize = adVolumeSize;
        this.deviceVolumeSize = deviceVolumeSize;
        this.standByTime = standByTime;
        this.maxVolume = maxVolume;
    }

    public String getDeviceConfigId() {
        return deviceConfigId;
    }

    public void setDeviceConfigId(String deviceConfigId) {
        this.deviceConfigId = deviceConfigId;
    }

    public String getLoseCupTime() {
        return loseCupTime;
    }

    public void setLoseCupTime(String loseCupTime) {
        this.loseCupTime = loseCupTime;
    }

    public String getHotWaterTemperature() {
        return hotWaterTemperature;
    }

    public void setHotWaterTemperature(String hotWaterTemperature) {
        this.hotWaterTemperature = hotWaterTemperature;
    }

    public String getAdVolumeSize() {
        return adVolumeSize;
    }

    public void setAdVolumeSize(String adVolumeSize) {
        this.adVolumeSize = adVolumeSize;
    }

    public String getDeviceVolumeSize() {
        return deviceVolumeSize;
    }

    public void setDeviceVolumeSize(String deviceVolumeSize) {
        this.deviceVolumeSize = deviceVolumeSize;
    }

    public String getStandByTime() {
        return standByTime;
    }

    public void setStandByTime(String standByTime) {
        this.standByTime = standByTime;
    }

    public String getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(String maxVolume) {
        this.maxVolume = maxVolume;
    }

    @Override
    public String toString() {
        return "DeviceBasicParam{" +
                "deviceConfigId='" + deviceConfigId + '\'' +
                ", loseCupTime='" + loseCupTime + '\'' +
                ", hotWaterTemperature='" + hotWaterTemperature + '\'' +
                ", adVolumeSize='" + adVolumeSize + '\'' +
                ", deviceVolumeSize='" + deviceVolumeSize + '\'' +
                ", standByTime='" + standByTime + '\'' +
                ", maxVolume='" + maxVolume + '\'' +
                '}';
    }
}
