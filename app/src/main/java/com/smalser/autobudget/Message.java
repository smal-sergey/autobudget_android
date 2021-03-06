package com.smalser.autobudget;

import android.support.annotation.NonNull;

import java.util.Calendar;

public class Message implements Comparable<Message> {
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
        this.category = CategoriesRepository.OTHER;
    }

    public String getDateString() {
        return Utils.stringifyDate(date);
    }

    public Category getCategory() {
        return CategoriesRepository.exists(category.getName()) ? category : CategoriesRepository.OTHER;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public int compareTo(@NonNull Message another) {
        return date.compareTo(another.date);
    }
}
