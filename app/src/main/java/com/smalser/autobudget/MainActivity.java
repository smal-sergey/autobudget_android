package com.smalser.autobudget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private final SmsParser smsParser = new SmsParser();
    Button mBtnInbox;
    TextView mQiwiVal;
    TextView mProductsVal;
    TextView mSportVal;
    TextView mCashVal;
    TextView mOtherVal;
    TextView mDateFilterTxt;
    TextView mCafeVal;
    TextView mTransportVal;
    TextView mClothesVal;
    TextView mRelaxVal;

    private static final int DATE_DIALOG_ID = 999;
    private int curYear;
    private int curMonth;
    private int curDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQiwiVal = (TextView) findViewById(R.id.lblQiwiValue);
        mProductsVal = (TextView) findViewById(R.id.lblProductsValue);
        mSportVal = (TextView) findViewById(R.id.lblSportValue);
        mCashVal = (TextView) findViewById(R.id.lblCashWithdrawValue);
        mOtherVal = (TextView) findViewById(R.id.lblOtherValue);
        mDateFilterTxt = (TextView) findViewById(R.id.lblDateFilter);
        mCafeVal = (TextView) findViewById(R.id.lblCafeValue);
        mTransportVal = (TextView) findViewById(R.id.lblTransportValue);
        mClothesVal = (TextView) findViewById(R.id.lblClothesValue);
        mRelaxVal = (TextView) findViewById(R.id.lblRelaxValue);
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
                        showDialog(DATE_DIALOG_ID);
                        return;
                }

                curYear = c.get(Calendar.YEAR);
                curMonth = c.get(Calendar.MONTH);
                curDay = c.get(Calendar.DAY_OF_MONTH);

                updateDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mBtnInbox = (Button) findViewById(R.id.btnShow);
        mBtnInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri inboxURI = Uri.parse("content://sms/inbox");

                String[] reqCols = new String[]{"_id", "address", "body"};
                String reqSelection = "address = ?";
                String[] reqSelectionArgs = new String[]{"Citialert"};

                // Get Content Resolver object, which will deal with Content Provider
                ContentResolver cr = getContentResolver();

                // Fetch Inbox SMS Message from Built-in Content Provider
                Cursor c = cr.query(inboxURI, reqCols, reqSelection, reqSelectionArgs, null);

                List<String> messages = read(c);
                List<Message> results = smsParser.parse(messages);
                StatisticCollector statCollector = new StatisticCollector(results);

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, curYear);
                cal.set(Calendar.MONTH, curMonth);
                cal.set(Calendar.DAY_OF_MONTH, curDay);

                mQiwiVal.setText(statCollector.getCategory(Category.QIWI, cal) + " rub.");
                mProductsVal.setText(statCollector.getCategory(Category.PRODUCTS, cal) + " rub.");
                mSportVal.setText(statCollector.getCategory(Category.SPORT, cal) + " rub.");
                mCashVal.setText(statCollector.getCategory(Category.CASH_WITHDRAW, cal) + " rub.");

                mCafeVal.setText(statCollector.getCategory(Category.CAFE, cal) + " rub.");
                mTransportVal.setText(statCollector.getCategory(Category.TRANSPORT, cal) + " rub.");
                mClothesVal.setText(statCollector.getCategory(Category.CLOTHES, cal) + " rub.");
                mRelaxVal.setText(statCollector.getCategory(Category.RELAX, cal) + " rub.");

                mOtherVal.setText(statCollector.getCategory(Category.OTHER, cal) + " rub.");
            }

            private List<String> read(Cursor c) {
                List<String> messages = new ArrayList<>();
                while (c.moveToNext()) {
                    messages.add(c.getString(2));
                }
                return messages;
            }
        });
    }

    private String stringify(Integer value){
        return value < 10 ? "0" + value.toString() : value.toString();
    }

    private void updateDate() {
        String label = getResources().getString(R.string.txt_from_date);

        mDateFilterTxt.setText(new StringBuilder()
                .append(label).append(" ")
                .append(stringify(curDay)).append("-").append(stringify(curMonth + 1)).append("-")
                .append(curYear).append(" "));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                    // when dialog box is closed, below method will be called.
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        curYear = selectedYear;
                        curMonth = selectedMonth;
                        curDay = selectedDay;

                        updateDate();
                    }}, curYear, curMonth, curDay);
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
