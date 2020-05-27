package com.lwr.password.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS");

    public static String format(Date date) {
        return dateFormat.format(new Date());
    }
}
