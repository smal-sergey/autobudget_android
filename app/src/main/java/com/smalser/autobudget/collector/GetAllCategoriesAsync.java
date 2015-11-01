package com.smalser.autobudget.collector;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smalser.autobudget.CategoriesRepository;
import com.smalser.autobudget.Category;
import com.smalser.autobudget.Message;
import com.smalser.autobudget.Utils;
import com.smalser.autobudget.main.CategoryTotal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GetAllCategoriesAsync extends AsyncTask<Calendar, Void, List<CategoryTotal>> {

    private final View loadingIndicator;
    private ArrayAdapter<CategoryTotal> listAdapter;
    private TextView mTotalTxt;
    private StatisticCollector statisticCollector;

    public GetAllCategoriesAsync(View loadingIndicator, ArrayAdapter<CategoryTotal> listAdapter,
                                 TextView mTotalTxt, StatisticCollector sc) {
        this.loadingIndicator = loadingIndicator;
        this.listAdapter = listAdapter;
        this.mTotalTxt = mTotalTxt;
        statisticCollector = sc;
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

        for (Category category : CategoriesRepository.allCategories()) {
            List<Message> messages = statisticCollector.filterMessages(category, fromDate[0]);

            double result = 0.0;
            for (Message msg : messages) {
                result += msg.purchase;
            }
            stat.add(new CategoryTotal(messages, Utils.roundDouble(result), category));
        }
        return stat;
    }
}
