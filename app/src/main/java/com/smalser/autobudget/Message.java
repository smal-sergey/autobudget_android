package com.smalser.autobudget;

import java.util.Calendar;

public class Message {
    public final String id;
    public final Double purchase;
    public final String source;
    public final Calendar date;
    public final Double balance;
    public final String fullMessage;
    private Category category;

    public Message(String id, String fullMessage, Double purchase, String source, Calendar date, Double balance) {
        this.id = id;
        this.fullMessage = fullMessage;
        this.purchase = purchase;
        this.source = source;
        this.date = date;
        this.balance = balance;
    }

    public String getDateString() {
        return Utils.stringifyDate(date);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
