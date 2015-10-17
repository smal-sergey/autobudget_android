package com.smalser.autobudget.collector;

import com.smalser.autobudget.Message;

import java.text.ParseException;
import java.util.regex.Matcher;

public interface MessageCompiler {
    Message getMessage(String id, Matcher m) throws ParseException;

}
