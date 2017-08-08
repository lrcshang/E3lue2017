package com.e3lue.us.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Leo on 2017/3/29.
 */

public class RegexUtil {
    //是否为数字
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
