package com.mei.chaji.core.bean.main;

public class AdData {
    private String deviceName;
    private String adId;
    private String adName;
    private String adType;
    private String adUrl;
    private String adPositionId;
    private String adTime;
    private String adQrcode;


    public AdData() {
    }

    public AdData(String deviceName, String adId, String adName, String adType, String adUrl, String adPositionId, String adTime, String adQrcode, String adDiscountPrice) {
        this.deviceName = deviceName;
        this.adId = adId;
        this.adName = adName;
        this.adType = adType;
        this.adUrl = adUrl;
        this.adPositionId = adPositionId;
        this.adTime = adTime;
        this.adQrcode = adQrcode;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String getAdUrl() {
        return adUrl;
    }

    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }

    public String getAdPosition() {
        return adPositionId;
    }

    public void setAdPosition(String adPosition) {
        this.adPositionId = adPosition;
    }

    public String getAdTime() {
        return adTime;
    }

    public void setAdTime(String adTime) {
        this.adTime = adTime;
    }

    public String getAdQrcode() {
        return adQrcode;
    }

    public void setAdQrcode(String adQrcode) {
        this.adQrcode = adQrcode;
    }

    @Override
    public String toString() {
        return "AdData{" +
                "deviceName='" + deviceName + '\'' +
                ", adId='" + adId + '\'' +
                ", adName='" + adName + '\'' +
                ", adType='" + adType + '\'' +
                ", adUrl='" + adUrl + '\'' +
                ", adPositionId='" + adPositionId + '\'' +
                ", adTime='" + adTime + '\'' +
                ", adQrcode='" + adQrcode + '\'' +
                '}';
    }
}
