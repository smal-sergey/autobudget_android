package com.smalser.autobudget;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatisticCollector {
    private final Collection<Message> messages;

    public StatisticCollector(Collection<Message> messages) {
        this.messages = messages;
    }

    //todo method name
    public Double getCategory(Category category) {
        double result = 0;
        Pattern p = Pattern.compile(category.resolve());

        for (Message msg : messages) {
            Matcher m = p.matcher(msg.source);
            if (m.matches()) {
                result += msg.purchase;
            }
        }
        return (Math.round(result * 100) + 0.0) / 100;
    }
}
