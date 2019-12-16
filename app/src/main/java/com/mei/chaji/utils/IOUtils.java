package com.mei.chaji.utils;

import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    /**
     * 把流数据转化
     *
     * @param is
     * @return
     */
    public static byte[] get_byteimg(InputStream is) {
        BufferedInputStream buf = null;
        byte[] ff = null;
        try {
            //根据字节输入流构建字节缓冲流
            buf = new BufferedInputStream(is);
            byte[] bytes = new byte[1024];
            //数据读取
            int len = -1;
            StringBuffer sb = new StringBuffer();
            while ((len = buf.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, len));
            }
            String dd = sb.toString();
            ff = Base64.decode(dd.getBytes(), 0, dd.length(), Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ff;
    }
}
