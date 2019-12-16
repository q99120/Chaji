package com.mei.chaji.core.bean.msg;

/**
 * Created by jingyuxuan on 2019/5/18.
 */

public class InsMessage {
    String insmsg;
    String inscontent1;
    String inscontent2;
    String inscontent3;
    String err_code;

    public InsMessage(String insmsg, String inscontent1, String inscontent2, String inscontent3,String err_code) {
        this.insmsg = insmsg;
        this.inscontent1 = inscontent1;
        this.inscontent2 = inscontent2;
        this.inscontent3 = inscontent3;
        this.err_code = err_code;
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

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }
}
