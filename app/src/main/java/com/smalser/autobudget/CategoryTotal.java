package com.smalser.autobudget;

import java.util.List;

public class CategoryTotal {
    public final List<Message> messages;
    public final double result;
    public final Category category;

    public CategoryTotal(List<Message> messages, double result, Category category) {
        this.messages = messages;
        this.result = result;
        this.category = category;
    }
}
