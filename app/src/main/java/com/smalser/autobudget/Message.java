package com.smalser.autobudget;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;

public class Message {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");
    public final Double purchase;
    public final String source;
    public final Calendar date;
    public final Double balance;
    public final String fullMessage;
    private Category category;

    public Message(String fullMessage, Double purchase, String source, Calendar date, Double balance) {
        this.fullMessage = fullMessage;
        this.purchase = purchase;
        this.source = source;
        this.date = date;
        this.balance = balance;
    }

    public static Message fromMatcher(Matcher m) throws ParseException {
        String fullMessage = m.group(0);
        Double purchase = Double.parseDouble(m.group(1));
        String source = m.group(2);
        Calendar date = Calendar.getInstance();
        date.setTime(DATE_FORMAT.parse(m.group(3)));

        Double balance = Double.parseDouble(m.group(4));
        return new Message(fullMessage, purchase, source, date, balance);
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
