package com.smalser.autobudget.report;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.smalser.autobudget.Category;
import com.smalser.autobudget.Message;
import com.smalser.autobudget.MyApplication;
import com.smalser.autobudget.R;
import com.smalser.autobudget.edit.EditMessageActivity;

import java.util.List;

public class CategoryReportActivity extends Activity {
    public static final String CATEGORY_EXTRA = "category_extra";
    ListView mCategoryReport;
    TextView mCategory;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_report);

        category = Category.valueOf(getIntent().getStringExtra(CATEGORY_EXTRA));

        mCategory = (TextView) findViewById(R.id.cr_lblCategory);
        mCategory.setText(getResources().getString(category.lblId()));
        mCategoryReport = (ListView) findViewById(R.id.listCategoryReport);

        final MyApplication app = (MyApplication) getApplication();

        mCategoryReport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message msg = (Message) parent.getItemAtPosition(position);
                app.setEditedMessage(msg);
                startActivity(new Intent(CategoryReportActivity.this, EditMessageActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyApplication app = (MyApplication) getApplication();
        List<Message> report = app.getFilteredMessages(category);
        mCategoryReport.setAdapter(new CategoryReportAdapter(CategoryReportActivity.this, R.layout.categories_report_row, report));
    }
}
