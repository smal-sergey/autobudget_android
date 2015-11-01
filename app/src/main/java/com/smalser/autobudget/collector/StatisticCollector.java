package com.smalser.autobudget.collector;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.smalser.autobudget.Category;
import com.smalser.autobudget.Message;
import com.smalser.autobudget.main.CategoryTotal;
import com.smalser.autobudget.main.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.smalser.autobudget.CategoriesRepository.OTHER;
import static com.smalser.autobudget.CategoriesRepository.allCategories;
import static com.smalser.autobudget.CategoriesRepository.valueOf;

public class StatisticCollector {
    private static final String STATISTIC_COLLECTOR_TAG = "Statistic_collector_log";

    private Map<String, Message> id2message;
    private List<? extends Message> messages;
    private final Context context;

    public StatisticCollector(List<? extends Message> messages, Context context) {
        this.messages = messages;
        this.context = context;

        this.id2message = new HashMap<>();
        for (Message msg : messages) {
            id2message.put(msg.id, msg);
        }
    }

    private Map<Category, List<Message>> categorize() {
//        applyPatterns();

//        cleanUp();

        //todo make this primary source of categorization (eliminate patterns)
        applyUserCategories();

        return getCategorizedMessages();
    }

    private void cleanUp() {
        SharedPreferences categoryPrefs = context.getSharedPreferences(MainActivity.CATEGORY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences messagePrefs = context.getSharedPreferences(MainActivity.MESSAGE_PREFS, Context.MODE_PRIVATE);

        for (String id : categoryPrefs.getAll().keySet()) {
            Category category = valueOf(id);
            if (category == null) {
                categoryPrefs.edit().remove(id).apply();
            }
        }

        for (String id : messagePrefs.getAll().keySet()) {
            String categoryName = messagePrefs.getString(id, OTHER.name);
            Category category = valueOf(categoryName);
            if (category == null) {
                messagePrefs.edit().remove(id).apply();
            }
        }
    }

//    private void applyPatterns() {
//        List<Message> uncategorized = new ArrayList<>(messages);
//
//        //values returns OTHER at the end, when everything already categorized!
//        for (Category category : allCategories()) {
//            List<Message> matched = new ArrayList<>();
//            Pattern p;
//            try {
//                p = Pattern.compile(loadTemplate(context, category));
//            } catch (PatternSyntaxException e) {
//                continue;
//            }
//
//            for (Message msg : uncategorized) {
//                Matcher m = p.matcher(msg.source);
//                if (m.matches()) {
//                    matched.add(msg);
//                    msg.setCategory(category);
//                }
//            }
//
//            uncategorized.removeAll(matched);
//        }
//
//        for (Message msg : uncategorized) {
//            msg.setCategory(OTHER);
//        }
//    }

    private void applyUserCategories() {
        SharedPreferences categoryPrefs = context.getSharedPreferences(MainActivity.CATEGORY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences messagePrefs = context.getSharedPreferences(MainActivity.MESSAGE_PREFS, Context.MODE_PRIVATE);

        for (Category category : allCategories()) {
            Set<String> sources = categoryPrefs.getStringSet(category.name, new HashSet<String>());

            for (Message msg : this.messages) {
                if (sources.contains(msg.source)) {
                    msg.setCategory(category);
                }
            }
        }

        for (String id : messagePrefs.getAll().keySet()) {
            Category category = valueOf(messagePrefs.getString(id, OTHER.name));
            Message msg = id2message.get(id);
            msg.setCategory(category);
        }
    }

    private Map<Category, List<Message>> getCategorizedMessages() {
        Map<Category, List<Message>> categories = new HashMap<>();

        for (Category category : allCategories()) {
            categories.put(category, new ArrayList<Message>());
        }

        for (Message msg : messages) {
            categories.get(msg.getCategory()).add(msg);
        }
        return categories;
    }

    public Double getCategory(Category category, Calendar fromDate) {
        double result = 0;

        Map<Category, List<Message>> categorized = categorize();
        for (Message msg : categorized.get(category)) {
            if (fromDate.before(msg.date)) {
                result += msg.purchase;

                if (category.equals(OTHER)) {
                    Log.i(STATISTIC_COLLECTOR_TAG, "\nSOURCE:'" + msg.source + "'; MSG: '" + msg.fullMessage + "'");
                }
            }
        }
        return (Math.round(result * 100) + 0.0) / 100;
    }

    public List<CategoryTotal> getAllCategories(Calendar fromDate) {
        List<CategoryTotal> stat = new ArrayList<>();
        Map<Category, List<Message>> categorized = categorize();

        for (Category category : allCategories()) {
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
        //can be cached
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

    public Map<String, Message> getIdToMessage() {
        return id2message;
    }
}
