package com.smalser.autobudget.collector;

import android.util.Log;

import com.smalser.autobudget.Message;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsParser {
    private static final String SMS_PARSER_TAG = "Sms_parser_log";
    private Map<Pattern, MessageCompiler> msgPatterns;

    public SmsParser(Map<Pattern, MessageCompiler> msgPatterns) {
        this.msgPatterns = msgPatterns;
    }

    //todo make it in separate thread
    public List<Message> parse(List<String> messages) {
        List<Message> result = new ArrayList<>();
        Map<Matcher, MessageCompiler> matchers = new HashMap<>();
        for (String msg : messages) {
            for (Map.Entry<Pattern, MessageCompiler> entry : msgPatterns.entrySet()) {
                matchers.put(entry.getKey().matcher(msg), entry.getValue());
            }

            try {
                boolean matched = false;
                for (Matcher matcher : matchers.keySet()) {
                    MessageCompiler compiler = matchers.get(matcher);
                    if (matcher.matches()) {
                        result.add(compiler.getMessage(matcher));
                        matched = true;
                        break;
                    }
                }

                if(!matched){
                    throw new ParseException("Can not parse message '" + msg + "'", 0);
                }
            } catch (ParseException e) {
                Log.i(SMS_PARSER_TAG, "Can not parse message '" + msg + "'");
                //and just skip this message
            }

            matchers.clear();
        }

        return result;
    }
}
