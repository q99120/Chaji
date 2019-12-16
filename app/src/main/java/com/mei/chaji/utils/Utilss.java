package com.mei.chaji.utils;

import com.mei.chaji.core.bean.main.VideoInfo;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utilss {
    /**
     * 提供精确乘法运算的mul方法
     *
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static double mul(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 获取系统当前日期时间时分秒
     */
    public static String getTime() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当日期
        int mWay = c.get(Calendar.DAY_OF_WEEK);// 获取当前日期的星期
        int mHour = c.get(Calendar.HOUR_OF_DAY);//时
        int mMinute = c.get(Calendar.MINUTE);//分
        int mSecond = c.get(Calendar.SECOND);
        String times = year + "-" + mMonth + "-" + mDay + " " + mHour + ":" + mMinute + ":" + mSecond;
        return times;
    }


    /***转小写**/
    public static char charToLowerCase(char ch){
        if(ch <= 90 && ch >= 65){
            ch += 32;
        }
        return ch;
    }

    /**转大写**/
    public static char charToUpperCase(char ch){
        if(ch <= 122 && ch >= 97){
            ch -= 32;
        }
        return ch;
    }



    public static String getDateToString(long time) {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
        sf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date d = new Date(time);
        return sf.format(d);
    }

    /**
     * @param dateString 2018-11-07 13:42:03,
     * @param pattern    yyyy-MM-dd HH:mm:ss
     * @return 1541569323000
     */
    public static long getString2Date(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        //时间戳会有时差 设置时区即可
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }
    //获取系统日期
    public static long getCurrentTimesss(){
        //获取系统的 日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        long tineee = getString2Date(year+"年"+month+"月"+day+"日"+hour + "时" + minute + "分",
                "yyyy年MM月dd日HH时mm分");
        return tineee;
    }

}
