package com.smalser.autobudget;

import android.app.Application;

import com.smalser.autobudget.collector.StatisticCollector;

import java.util.Calendar;
import java.util.List;

public class MyApplication extends Application {
    private int curYear;
    private int curMonth;
    private int curDay;

    private StatisticCollector collector;
    private Message editedMessage;

    public List<Message> getFilteredMessages(Category category) {
        return collector.filterMessages(category, getCurDate());
    }

    public void setStatisticCollector(StatisticCollector collector) {
        this.collector = collector;
    }

    public StatisticCollector getStatisticCollector() {
        return collector;
    }

    public void setCurDate(int curYear, int curMonth, int curDay) {
        this.curYear = curYear;
        this.curMonth = curMonth;
        this.curDay = curDay;
    }

    public Calendar getCurDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, curYear);
        cal.set(Calendar.MONTH, curMonth);
        cal.set(Calendar.DAY_OF_MONTH, curDay);
        return cal;
    }

    public Calendar getMinDate() {
        return collector.getMinDate();
    }

    public Message getEditedMessage() {
        return editedMessage;
    }

    public void setEditedMessage(Message editedMessage) {
        this.editedMessage = editedMessage;
    }

    public String getCurDateString() {
        return Utils.stringifyDate(getCurDate());
    }
}
