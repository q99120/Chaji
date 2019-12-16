package com.mei.chaji.core.bean.main;


import java.util.List;

public class UserData  {

    boolean result;
    List<UserRows> rows;


    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<UserRows> getRows() {
        return rows;
    }

    public void setRows(List<UserRows> rows) {
        this.rows = rows;
    }

    /**
     * {
     * "rows":[
     * {
     * "deviceName": 设备name,
     * "adId": 广告ID,
     * "adName": 广告name,
     * "adType": 广告类别（1是图片，2是mp4视频）,
     * "adUrl": "广告地址",
     * "adPosition": 广告位置,(1、待机界面；2、支付界面；3、续水扫码界面；4、购买制作及完成；5、续水制作及完成)
     * "adTime": 广告时长/s,
     * "adQrcode": "广告优惠二维码",
     * "adDiscountPrice": "广告优惠价"
     * },
     * .....
     * ],
     * "result": TRUE // 信息提示
     * }
     */
    public class UserRows{
        public String deviceName;
        public String adId;
        public String adName;
        public String adType;
        public String adUrl;
        public String adPosition;
        public String adTime;
        public String adQrcode;
        public String adDiscountPrice;

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
            return adPosition;
        }

        public void setAdPosition(String adPosition) {
            this.adPosition = adPosition;
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

        public String getAdDiscountPrice() {
            return adDiscountPrice;
        }

        public void setAdDiscountPrice(String adDiscountPrice) {
            this.adDiscountPrice = adDiscountPrice;
        }
    }


}
