package com.mei.chaji.core.bean.main;

public class Mqttmessages {
    public  boolean connect;
    public String device_msg;
    public boolean device_status;
    long dateTime;
    String type;
    String orderNo;
    int waterTemperature;
    int gargoWay;
    int cupNumber;

    String insmsg;
    String inscontent1;
    String inscontent2;
    String inscontent3;

    String login_msg, login_content_msg;

    String orderAmount,preAmount;

    public Mqttmessages(String device_msg, boolean device_status, long dateTime, String type, String orderNo, int waterTemperature
            , int gargoWay, int cupNumber) {
        this.device_msg = device_msg;
        this.device_status = device_status;
        this.dateTime = dateTime;
        this.type = type;
        this.orderNo = orderNo;
        this.waterTemperature = waterTemperature;
        this.gargoWay = gargoWay;
        this.cupNumber = cupNumber;
    }

    public Mqttmessages(String device_msg, boolean device_status, long dateTime, String type, String orderNo, int waterTemperature
            , int gargoWay, int cupNumber,String orderAmount,String preAmount) {
        this.device_msg = device_msg;
        this.device_status = device_status;
        this.dateTime = dateTime;
        this.type = type;
        this.orderNo = orderNo;
        this.waterTemperature = waterTemperature;
        this.gargoWay = gargoWay;
        this.cupNumber = cupNumber;
        this.orderAmount = orderAmount;
        this.preAmount = preAmount;
    }

    public Mqttmessages(String type, int waterTemperature, int gargoWay) {
        this.type = type;
        this.gargoWay = gargoWay;
        this.waterTemperature = waterTemperature;
    }

    public Mqttmessages(String insmsg, String inscontent1, String inscontent2, String inscontent3) {
        this.insmsg = insmsg;
        this.inscontent1 = inscontent1;
        this.inscontent2 = inscontent2;
        this.inscontent3 = inscontent3;
    }

    public Mqttmessages(String login_msg, String login_content_msg) {
        this.login_msg = login_msg;
        this.login_content_msg = login_content_msg;
    }

    public Mqttmessages(boolean connect) {
       this.connect = connect;
    }


    public String getLogin_msg() {
        return login_msg;
    }

    public void setLogin_msg(String login_msg) {
        this.login_msg = login_msg;
    }

    public String getLogin_content_msg() {
        return login_content_msg;
    }

    public void setLogin_content_msg(String login_content_msg) {
        this.login_content_msg = login_content_msg;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getPreAmount() {
        return preAmount;
    }

    public void setPreAmount(String preAmount) {
        this.preAmount = preAmount;
    }

    public String getInsmsg() {
        return insmsg;
    }

    public void setInsmsg(String insmsg) {
        this.insmsg = insmsg;
    }

    public String getInscontent1() {
        return inscontent1;
    }

    public void setInscontent1(String inscontent1) {
        this.inscontent1 = inscontent1;
    }

    public String getInscontent2() {
        return inscontent2;
    }

    public void setInscontent2(String inscontent2) {
        this.inscontent2 = inscontent2;
    }

    public String getInscontent3() {
        return inscontent3;
    }

    public void setInscontent3(String inscontent3) {
        this.inscontent3 = inscontent3;
    }

    public String getDevice_msg() {
        return device_msg;
    }

    public void setDevice_msg(String device_msg) {
        this.device_msg = device_msg;
    }

    public boolean isDevice_status() {
        return device_status;
    }

    public void setDevice_status(boolean device_status) {
        this.device_status = device_status;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getWaterTemperature() {
        return waterTemperature;
    }

    public void setWaterTemperature(int waterTemperature) {
        this.waterTemperature = waterTemperature;
    }

    public int getGargoWay() {
        return gargoWay;
    }

    public void setGargoWay(int gargoWay) {
        this.gargoWay = gargoWay;
    }

    public int getCupNumber() {
        return cupNumber;
    }

    public void setCupNumber(int cupNumber) {
        this.cupNumber = cupNumber;
    }

    public boolean isConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }
}
