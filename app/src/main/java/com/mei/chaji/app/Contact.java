package com.mei.chaji.app;

import java.io.File;

public interface Contact {
    String base_url = "http://baqsng.natapp1.cc/";
    String test_url = "http://192.168.16.106:8080/";
    String hua_url = "http://huashidai.natapp1.cc";
    String test_url1 = "http://192.168.16.123:8088/";
    //正式服务器地址
    String p_url = "https://www.teagodtop.com";
//    String p_url = "https://www.chtimes.net";
        String mq = "tcp://47.112.133.168:1883";
//    String mq = "tcp://120.78.214.66:1883";
//    String baidu_voice_appid = "16390214";
//    String baidu_voice_appkey = "8Cbyhb7dXSQEdDVId2eX7OD4";
//    String baidu_voice_appsecret = "g78rinzekGxIxG2rrGdITMPncgpLTr2e";

    //新申请的
    String baidu_voice_appid = "17633267";
    String baidu_voice_appkey = "8Cbyhb7dXSQEdDVId2eX7OD4";
    String baidu_voice_appsecret = "P6RRd5xXuI4BlXiePVbs6dO1qIemw9jW";

    String imagepath = Contact.p_url + File.separator + "file" + File.separator;

    //测试服务器地址
    String base_url2 = "http://szhsd.mynatapp.cc";
    String test_url2 = "http://szhsd.mynatapp.cc/tmomp";
    String test_mqtt = "tcp://120.78.214.66:1883";
}
