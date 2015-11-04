package com.smalser.autobudget;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    private static DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
    private static DecimalFormat formatter;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');

        formatter = new DecimalFormat("0.00#", symbols);
        formatter.setMaximumFractionDigits(2);
    }

    public static String stringifyDate(Calendar c) {
        return df.format(new Date(c.getTimeInMillis()));
    }

    public static String getFormattedCash(double value) {
        return formatter.format(value);
    }
}
