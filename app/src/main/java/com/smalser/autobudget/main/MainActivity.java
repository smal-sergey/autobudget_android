package com.smalser.autobudget.main;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.smalser.autobudget.collector.GetAllCategoriesAsync;
import com.smalser.autobudget.Message;
import com.smalser.autobudget.MyApplication;
import com.smalser.autobudget.R;
import com.smalser.autobudget.collector.SmsParser;
import com.smalser.autobudget.collector.StatisticCollector;
import com.smalser.autobudget.report.CategoryReportActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private final SmsParser smsParser = new SmsParser();

    TextView mDateFilterTxt;
    ListView mCategories;
    View mLoadingPanel;

    private static final int DATE_DIALOG_ID = 999;
    private MyApplication app;
    private List<Message> allMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (MyApplication) getApplication();

        mLoadingPanel = findViewById(R.id.loadingPanel);

        readAllMessages();

        mCategories = (ListView) findViewById(R.id.listCategories);
        mCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryTotal categoryTotal = (CategoryTotal) parent.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, CategoryReportActivity.class);
                intent.putExtra(CategoryReportActivity.CATEGORY_EXTRA, categoryTotal.category.toString());
                startActivity(intent);
            }
        });

        mDateFilterTxt = (TextView) findViewById(R.id.lblDateFilter);
        mDateFilterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinnerDateFilter);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.date_filter_array, R.layout.spinner_layout);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Calendar c = Calendar.getInstance();

                switch (position) {
                    case 0:
                        c.add(Calendar.DAY_OF_MONTH, -7);
                        break;
                    case 1:
                        c.add(Calendar.MONTH, -1);
                        break;
                    case 2:
                        c.add(Calendar.MONTH, -6);
                        break;
                    case 3:
                        c.add(Calendar.YEAR, -1);
                        break;
                    case 4:
                        c = app.getMinDate();
                        c.add(Calendar.DAY_OF_MONTH, -1); //to show all dates
                        break;
                    case 5:
                        showDialog(DATE_DIALOG_ID);
                        return;
                }

                app.setCurDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                updateDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        readAllMessages();
        updateCategories();
    }

    private void readAllMessages() {
        Uri inboxURI = Uri.parse("content://sms/inbox");

        String[] reqCols = new String[]{"_id", "address", "body"};
        String reqSelection = "address = ?";
        String[] reqSelectionArgs = new String[]{"Citialert"};

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver cr = getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        Cursor c = cr.query(inboxURI, reqCols, reqSelection, reqSelectionArgs, null);

        List<String> messages = read(c);
        allMessages = smsParser.parse(messages);
        app.setStatisticCollector(new StatisticCollector(allMessages, this));
    }

    private List<String> read(Cursor c) {
        List<String> messages = new ArrayList<>();
        while (c.moveToNext()) {
            messages.add(c.getString(2));
        }
        return messages;
    }

    private void updateCategories() {
        //todo categories can be grouped: use ExpandableListView
        ArrayAdapter<CategoryTotal> msgAdapter = new CategoryTotalAdapter(MainActivity.this, R.layout.category_total_row,
                new ArrayList<CategoryTotal>());
        mCategories.setAdapter(msgAdapter);

        GetAllCategoriesAsync getAllTask = new GetAllCategoriesAsync(allMessages, mLoadingPanel, msgAdapter);
        getAllTask.execute(app.getCurDate());
    }

    private void updateDate() {
        String label = getResources().getString(R.string.txt_from_date);

        mDateFilterTxt.setText(new StringBuilder()
                .append(label).append(" ")
                .append(app.getCurDateString()));

        updateCategories();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                Calendar curDate = app.getCurDate();

                // set date picker as current date
                return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                    // when dialog box is closed, below method will be called.
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        app.setCurDate(selectedYear, selectedMonth, selectedDay);

                        updateDate();
                    }
                }, curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), curDate.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
