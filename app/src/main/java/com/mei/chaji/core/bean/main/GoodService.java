package com.mei.chaji.core.bean.main;

public class GoodService {
    public GoodService() {
    }

    /**
     * "deviceGoodsId": 1, 		//设备商品唯一主键id
     * "cargoWayCount": 10,	    //该货道商品数量
     * "goodsNo": "no1", 			// 商品编号
     * "cargoWayType": 1, 		//货道类型，1表示一货道，2表示二货道
     * "goodsPrice": 10, 			//商品价格
     * "goodsImage": "\\upload\\imgFile\\1555936744267.png", 			//商品图片
     * "deviceNo": "tobeNo1", 	//设备编号
     * "goodsName": "红茶" 		   //商品名称
     */


    String goodsId;
    String cargoWayCount;
    String goodsNo;
    String cargoWayType;
    String goodsPrice;
    String goodsImage;
    String deviceNo;
    String goodsName;
    String goodsRemark;
    String oneGargoWay;
    String twoGargoWay;
    String isFree;

    public GoodService(String goodsId, String cargoWayCount, String goodsNo, String cargoWayType, String goodsPrice, String goodsImage, String deviceNo, String goodsName,
                       String goodsRemark, String oneGargoWay, String twoGargoWay,String isFree) {
        this.goodsId = goodsId;
        this.cargoWayCount = cargoWayCount;
        this.goodsNo = goodsNo;
        this.cargoWayType = cargoWayType;
        this.goodsPrice = goodsPrice;
        this.goodsImage = goodsImage;
        this.deviceNo = deviceNo;
        this.goodsName = goodsName;
        this.oneGargoWay = oneGargoWay;
        this.twoGargoWay = twoGargoWay;
        this.goodsRemark = goodsRemark;
        this.isFree = isFree;

    }

    public String getGoodsRemark() {
        return goodsRemark;
    }

    public void setGoodsRemark(String goodsRemark) {
        this.goodsRemark = goodsRemark;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getCargoWayCount() {
        return cargoWayCount;
    }

    public void setCargoWayCount(String cargoWayCount) {
        this.cargoWayCount = cargoWayCount;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getCargoWayType() {
        return cargoWayType;
    }

    public void setCargoWayType(String cargoWayType) {
        this.cargoWayType = cargoWayType;
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getOneGargoWay() {
        return oneGargoWay;
    }

    public void setOneGargoWay(String oneGargoWay) {
        this.oneGargoWay = oneGargoWay;
    }

    public String getTwoGargoWay() {
        return twoGargoWay;
    }

    public void setTwoGargoWay(String twoGargoWay) {
        this.twoGargoWay = twoGargoWay;
    }

    public String getIsFree() {
        return isFree;
    }

    public void setIsFree(String isFree) {
        this.isFree = isFree;
    }

    @Override
    public String toString() {
        return "GoodService{" +
                "goodsId='" + goodsId + '\'' +
                ", cargoWayCount='" + cargoWayCount + '\'' +
                ", goodsNo='" + goodsNo + '\'' +
                ", cargoWayType='" + cargoWayType + '\'' +
                ", goodsPrice='" + goodsPrice + '\'' +
                ", goodsImage='" + goodsImage + '\'' +
                ", deviceNo='" + deviceNo + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", goodsRemark='" + goodsRemark + '\'' +
                ", oneGargoWay='" + oneGargoWay + '\'' +
                ", twoGargoWay='" + twoGargoWay + '\'' +
                ", isFree='" + isFree + '\'' +
                '}';
    }
}
