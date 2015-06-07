package com.smalser.autobudget.collector;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smalser.autobudget.Category;
import com.smalser.autobudget.Message;
import com.smalser.autobudget.Utils;
import com.smalser.autobudget.main.CategoryTotal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class GetAllCategoriesAsync extends AsyncTask<Calendar, Void, List<CategoryTotal>> {

    private final List<? extends Message> messages;
    private final View loadingIndicator;
    private ArrayAdapter<CategoryTotal> listAdapter;
    private TextView mTotalTxt;

    public GetAllCategoriesAsync(List<? extends Message> messages, View loadingIndicator,
                                 ArrayAdapter<CategoryTotal> listAdapter, TextView mTotalTxt) {
        this.messages = messages;
        this.loadingIndicator = loadingIndicator;
        this.listAdapter = listAdapter;
        this.mTotalTxt = mTotalTxt;
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
                p = Pattern.compile(category.loadTemplate(loadingIndicator.getContext()));
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(List<CategoryTotal> categoryTotals) {
        super.onPostExecute(categoryTotals);
        loadingIndicator.setVisibility(View.GONE);
        listAdapter.clear();

        double sum = 0;
        for (CategoryTotal categoryTotal : categoryTotals) {
            listAdapter.add(categoryTotal);
            sum += categoryTotal.result;
        }
        mTotalTxt.setText(Utils.getFormattedCash(sum));
    }

    @Override
    protected List<CategoryTotal> doInBackground(Calendar... fromDate) {
        List<CategoryTotal> stat = new ArrayList<>();
        Map<Category, List<Message>> categorized = categorize();

        for (Category category : Category.values()) {
            List<Message> messages = filterMessages(categorized.get(category), fromDate[0]);

            double result = 0.0;
            for (Message msg : messages) {
                result += msg.purchase;
            }
            stat.add(new CategoryTotal(messages, Utils.roundDouble(result), category));
        }
        return stat;
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
}
