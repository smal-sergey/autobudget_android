package com.smalser.autobudget.main;

import com.smalser.autobudget.Category;
import com.smalser.autobudget.Message;

import java.util.List;

public class CategoryTotal {
    public final List<Message> messages;
    public final double result;
    public final Category category;
    public boolean selected;

    public CategoryTotal(List<Message> messages, double result, Category category) {
        this.messages = messages;
        this.result = result;
        this.category = category;
    }

    @Override
    public String toString() {
        return "Category total " + category.getName() + ", selected=" + selected;
    }
}
