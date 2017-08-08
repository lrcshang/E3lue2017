package com.e3lue.us.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Leo on 2017/5/19.
 */

public class DateUtil {

    public static String getNowDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static String strToDateShort(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        SimpleDateFormat formatter2=new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter2.format(strtodate);
        return date;
    }

    public static String strToDateShortZh(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        SimpleDateFormat formatter2=new SimpleDateFormat("yyyy年MM月dd日");
        String date = formatter2.format(strtodate);
        return date;
    }

    public static String DateToShort(Date date) {
        SimpleDateFormat formatter2=new SimpleDateFormat("yyyy-MM-dd");
        String datestr = formatter2.format(date);
        return datestr;
    }
}
