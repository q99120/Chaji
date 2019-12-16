package com.mei.chaji.core.bean.main;

public class GoodsInfo {

    public GoodsInfo() {
    }

//    "total": 3,
//            "result": true,
//            "msg": "查询商品列表成功!",
//            "rows":    [
//    {
//        "goodsId": 4,
//            "status": 1,
//            "remark": "123",
//            "lastUpdateBy": 4,
//            "lastUpdateByName": "超级管理员",
//            "goodsTypeName": "种类一",
//            "goodsNo": "no4",
//            "createByName": "超级管理员",
//            "createBy": 4,
//            "lastUpdateDate": 1556618639000,
//            "goodsPrice": 30,
//            "goodsStatus": 1,
//            "createDate": 1556073272000,
//            "goodsName": "大红袍",
//            "goodsType": 1,
//            "goodsStatusName": "上架"

    public String goodsId;
    public String status;
    public String remark;
    public String lastUpdateBy;
    public String lastUpdateByName;
    public String goodsTypeName;
    String goodsNo;
    String createByName;
    public String createBy;
    public String lastUpdateDate;
    public String goodsPrice;
    public String goodsStatus;
    public String createDate;
    public String goodsName;
    public String goodsType;
    public String goodsStatusName;


    public GoodsInfo(String goodsId, String status, String remark, String lastUpdateBy, String lastUpdateByName, String goodsTypeName, String goodsNo, String createByName, String createBy, String lastUpdateDate, String goodsPrice, String goodsStatus, String createDate, String goodsName, String goodsType, String goodsStatusName) {
        this.goodsId = goodsId;
        this.status = status;
        this.remark = remark;
        this.lastUpdateBy = lastUpdateBy;
        this.lastUpdateByName = lastUpdateByName;
        this.goodsTypeName = goodsTypeName;
        this.goodsNo = goodsNo;
        this.createByName = createByName;
        this.createBy = createBy;
        this.lastUpdateDate = lastUpdateDate;
        this.goodsPrice = goodsPrice;
        this.goodsStatus = goodsStatus;
        this.createDate = createDate;
        this.goodsName = goodsName;
        this.goodsType = goodsType;
        this.goodsStatusName = goodsStatusName;
    }


    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public String getLastUpdateByName() {
        return lastUpdateByName;
    }

    public void setLastUpdateByName(String lastUpdateByName) {
        this.lastUpdateByName = lastUpdateByName;
    }

    public String getGoodsTypeName() {
        return goodsTypeName;
    }

    public void setGoodsTypeName(String goodsTypeName) {
        this.goodsTypeName = goodsTypeName;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(String goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsStatusName() {
        return goodsStatusName;
    }

    public void setGoodsStatusName(String goodsStatusName) {
        this.goodsStatusName = goodsStatusName;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;

    }


}
