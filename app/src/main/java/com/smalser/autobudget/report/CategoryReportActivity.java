package com.smalser.autobudget.report;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.smalser.autobudget.Message;
import com.smalser.autobudget.MyApplication;
import com.smalser.autobudget.R;

import java.util.List;

public class CategoryReportActivity extends Activity{

    ListView mCategoryReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_report);

        mCategoryReport = (ListView) findViewById(R.id.listCategoryReport);
        MyApplication context = (MyApplication) getApplication();

        List<Message> report = context.getData();

        mCategoryReport.setAdapter(new CategoryReportAdapter(CategoryReportActivity.this, R.layout.categories_report_row, report));
    }
}
