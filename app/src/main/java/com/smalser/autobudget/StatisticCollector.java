package com.smalser.autobudget;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatisticCollector {
    private static final String STATISTIC_COLLECTOR_TAG = "Statistic_collector_log";

    private final Map<Category, List<Message>> categorized;

    public StatisticCollector(Collection<Message> messages) {
        categorized = new HashMap<>();
        categorize(messages);
    }

    private void categorize(Collection<Message> messages) {
        //values returns OTHER at the end, when everything already categorized!
        for (Category category : Category.values()) {
            Pattern p = Pattern.compile(category.resolve());

            List<Message> matched = new ArrayList<>();
            categorized.put(category, matched);

            for (Message msg : messages) {
                Matcher m = p.matcher(msg.source);
                if (m.matches()) {
                    matched.add(msg);
                }
            }

            messages.removeAll(matched);
        }
    }

    public Double getCategory(Category category, Calendar fromDate) {
        double result = 0;

        for (Message msg : categorized.get(category)) {
            if (fromDate.before(msg.date)) {
                result += msg.purchase;

                if (category == Category.OTHER) {
                    Log.i(STATISTIC_COLLECTOR_TAG, "\nSOURCE:'" + msg.source + "'; MSG: '" + msg.fullMessage + "'");
                }
            }
        }
        return (Math.round(result * 100) + 0.0) / 100;
    }
}
