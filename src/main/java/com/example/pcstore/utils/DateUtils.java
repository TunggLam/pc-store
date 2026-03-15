package com.example.pcstore.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss";

    public static String getDate(String format) {
        return convertDateToString(new Date(), format);
    }

    public static String getExpireDate(String date, String format, int minute) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        LocalDateTime newDateTime = dateTime.plusMinutes(minute);
        return newDateTime.format(formatter);
    }

    public static String convertLocalDateTimeToString(LocalDateTime date, String format) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    public static String convertDateToString(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat ft = new SimpleDateFormat(format);
        return ft.format(date);
    }
}

