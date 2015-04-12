package com.smalser.autobudget;

import android.app.Application;

import java.util.List;

public class MyApplication extends Application {
    private List<Message> data;

    public List<Message> getData() {
        return data;
    }

    public void setData(List<Message> data) {
        this.data = data;
    }

    public static String stringifyDate(int year, int month, int day){
        return stringify(day) + "-" + stringify(month + 1) + "-" + year + " ";
    }

    private static String stringify(Integer value) {
        return value < 10 ? "0" + value.toString() : value.toString();
    }
}
