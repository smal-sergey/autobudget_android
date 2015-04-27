package com.smalser.autobudget.collector;

import android.content.Context;
import android.util.Log;

import com.smalser.autobudget.Category;
import com.smalser.autobudget.Message;
import com.smalser.autobudget.main.CategoryTotal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StatisticCollector {
    private static final String STATISTIC_COLLECTOR_TAG = "Statistic_collector_log";

    private List<? extends Message> messages;
    private final Context context;

    public StatisticCollector(List<? extends Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    private Map<Category, List<Message>> categorize() {
        Map<Category, List<Message>> categorized = new HashMap<>();
        List<Message> uncategorized = new ArrayList<>(messages);

        //values returns OTHER at the end, when everything already categorized!
        for (Category category : Category.values()) {
            List<Message> matched = new ArrayList<>();
            categorized.put(category, matched);

            Pattern p;
            try {
                p = Pattern.compile(category.loadTemplate(context));
            } catch (PatternSyntaxException e) {
                continue;
            }

            for (Message msg : uncategorized) {
                Matcher m = p.matcher(msg.source);
                if (m.matches()) {
                    matched.add(msg);
                    msg.setCategory(category);
                }
            }

            uncategorized.removeAll(matched);
        }
        return categorized;
    }

    public Double getCategory(Category category, Calendar fromDate) {
        double result = 0;

        Map<Category, List<Message>> categorized = categorize();
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

    public List<CategoryTotal> getAllCategories(Calendar fromDate) {
        List<CategoryTotal> stat = new ArrayList<>();
        Map<Category, List<Message>> categorized = categorize();

        for (Category category : Category.values()) {
            List<Message> messages = filterMessages(categorized.get(category), fromDate);

            double result = 0.0;
            for (Message msg : messages) {
                result += msg.purchase;
            }
            stat.add(new CategoryTotal(messages, (Math.round(result * 100) + 0.0) / 100, category));
        }
        return stat;
    }

    public List<Message> filterMessages(Category category, Calendar fromDate) {
        Map<Category, List<Message>> categorized = categorize();
        return filterMessages(categorized.get(category), fromDate);
    }

    private List<Message> filterMessages(List<Message> messages, Calendar fromDate) {
        List<Message> filtered = new ArrayList<>();
        for (Message msg : messages) {
            if (fromDate.before(msg.date)) {
                filtered.add(msg);
            }
        }
        return filtered;
    }

    public Calendar getMinDate() {
        Calendar minDate = Calendar.getInstance();
        for (Message msg : messages) {
            if (minDate.after(msg.date)) {
                minDate.setTimeInMillis(msg.date.getTimeInMillis());
            }
        }
        return minDate;
    }
}
