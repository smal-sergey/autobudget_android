package com.smalser.autobudget;

import android.app.Application;

import java.util.List;

public class MyApplication extends Application {
    private List<Message> filteredMessages;
    private Message editedMessage;

    public List<Message> getFilteredMessages() {
        return filteredMessages;
    }

    public void setFilteredMessages(List<Message> filteredMessages) {
        this.filteredMessages = filteredMessages;
    }

    public Message getEditedMessage() {
        return editedMessage;
    }

    public void setEditedMessage(Message editedMessage) {
        this.editedMessage = editedMessage;
    }

    public static String stringifyDate(int year, int month, int day){
        return stringify(day) + "-" + stringify(month + 1) + "-" + year + " ";
    }

    private static String stringify(Integer value) {
        return value < 10 ? "0" + value.toString() : value.toString();
    }
}
