package com.smalser.autobudget;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private final SmsParser smsParser = new SmsParser();
    Button mBtnInbox;
    TextView mQiwiTxt;
    TextView mProductsTxt;
    TextView mSportTxt;
    TextView mCashTxt;
    TextView mOtherTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQiwiTxt = (TextView) findViewById(R.id.qiwiStat);
        mProductsTxt = (TextView) findViewById(R.id.productsStat);
        mSportTxt = (TextView) findViewById(R.id.sportStat);
        mCashTxt = (TextView) findViewById(R.id.cashStat);
        mOtherTxt = (TextView) findViewById(R.id.otherStat);

        mBtnInbox = (Button) findViewById(R.id.btnInbox);
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

                mQiwiTxt.setText(Category.QIWI + ": " + statCollector.getCategory(Category.QIWI) + " rub.");
                mProductsTxt.setText(Category.PRODUCTS + ": " + statCollector.getCategory(Category.PRODUCTS) + " rub.");
                mSportTxt.setText(Category.SPORT + ": " + statCollector.getCategory(Category.SPORT) + " rub.");
                mCashTxt.setText(Category.CASH_WITHDRAW + ": " + statCollector.getCategory(Category.CASH_WITHDRAW) + " rub.");
                mOtherTxt.setText(Category.OTHER + ": " + statCollector.getCategory(Category.OTHER) + " rub.");
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
