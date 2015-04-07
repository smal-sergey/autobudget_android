package com.smalser.autobudget;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

public class Message {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/mm/yy");
    public final Double purchase;
    public final String source;
    public final Date date;
    public final Double balance;

    public Message(Double purchase, String source, Date date, Double balance) {
        this.purchase = purchase;
        this.source = source;
        this.date = date;
        this.balance = balance;
    }

    public static Message fromMatcher(Matcher m) throws ParseException {
        Double purchase = Double.parseDouble(m.group(1));
        String source = m.group(2);
        Date date = DATE_FORMAT.parse(m.group(3));
        Double balance = Double.parseDouble(m.group(4));
        return new Message(purchase, source, date, balance);
    }
}