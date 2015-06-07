package com.smalser.autobudget;

import java.util.Calendar;

public class Utils {

    public static String stringifyDate(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return stringifyDatePart(day) + "-" + stringifyDatePart(month + 1) + "-" + year + " ";
    }

    private static String stringifyDatePart(Integer value) {
        return value < 10 ? "0" + value.toString() : value.toString();
    }

    public static String getFormattedCash(double value) {
        String str = "" + roundDouble(value);
        int dotIdx = str.indexOf(".");
        str = dotIdx == str.length() - 2 ? str + "0" : str;
        if (dotIdx <= 3) {
            return str;
        } else {
            String end = str.substring(dotIdx - 3);
            return str.replace(end, " " + end);
        }
    }

    public static double roundDouble(double value) {
        return (Math.round(value * 100) + 0.0) / 100;
    }
}
