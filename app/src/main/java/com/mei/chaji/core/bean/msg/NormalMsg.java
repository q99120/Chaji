package com.mei.chaji.core.bean.msg;

import retrofit2.http.PUT;

public class NormalMsg {
    public String msg_key;
    public boolean msg_value;

    public NormalMsg(String key, boolean value) {
        this.msg_key = key;
        this.msg_value = value;
    }

    public String getMsg_key() {
        return msg_key;
    }

    public void setMsg_key(String msg_key) {
        this.msg_key = msg_key;
    }

    public boolean isMsg_value() {
        return msg_value;
    }

    public void setMsg_value(boolean msg_value) {
        this.msg_value = msg_value;
    }

    @Override
    public String toString() {
        return "NormalMsg{" +
                "msg_key='" + msg_key + '\'' +
                ", msg_value=" + msg_value +
                '}';
    }
}
