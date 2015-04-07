package com.smalser.autobudget;

import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.smalser.autobudget.Message.fromMatcher;

public class SmsParser {
    private static final String SMS_PARSER_TAG = "Sms_parser_log";

    private static final String PATTERN_WITHDRAW = "Spisanie: (\\d+\\.\\d+).*Operaciya: (.+) Data: (\\d\\d/\\d\\d/\\d\\d) Balans: (\\d+\\.\\d+) RUB";
    private static final Pattern MSG_WITHDRAW_FORMAT = Pattern.compile(PATTERN_WITHDRAW);

    private static final String PATTERN_BUY = "Pokupka: (\\d+\\.\\d+).*Torgovaya tochka: (.+) Data: (\\d\\d/\\d\\d/\\d\\d) Balans: (\\d+\\.\\d+) RUB";
    private static final Pattern MSG_BUY_FORMAT = Pattern.compile(PATTERN_BUY);

    //todo make it in separate thread
    public List<Message> parse(List<String> messages) {
        List<Message> result = new ArrayList<>();
        Matcher matcher1;
        Matcher matcher2;
        for (String msg : messages) {
            matcher1 = MSG_BUY_FORMAT.matcher(msg);
            matcher2 = MSG_WITHDRAW_FORMAT.matcher(msg);
            try {
                if (matcher1.matches()) {
                    result.add(fromMatcher(matcher1));
                } else if (matcher2.matches()) {
                    result.add(fromMatcher(matcher2));
                } else {
                    throw new ParseException("Can not parse message '" + msg + "'", 0);
                }
            } catch (ParseException e) {
                Log.i(SMS_PARSER_TAG, "Can not parse message '" + msg + "'");
                //and just skip this message
            }
        }

        return result;
    }
}
