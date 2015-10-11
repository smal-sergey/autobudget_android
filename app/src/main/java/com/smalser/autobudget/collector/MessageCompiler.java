package com.smalser.autobudget.collector;

import com.smalser.autobudget.Message;

import java.text.ParseException;
import java.util.regex.Matcher;

public interface MessageCompiler {
    Message getMessage(Matcher m) throws ParseException;

}
