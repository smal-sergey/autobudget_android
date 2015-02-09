package com.smalser.autobudget;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends ActionBarActivity {

    Button mBtnInbox;
    ListView lvMsg;
    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvMsg = (ListView) findViewById(R.id.lvMessages);

        mBtnInbox = (Button) findViewById(R.id.btnInbox);
        mBtnInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri inboxURI = Uri.parse("content://sms/inbox");

                String[] reqCols = new String[] { "_id", "address", "body" };
                String reqSelection = "address = ?";
                String[] reqSelectionArgs = new String[] {"42"};

                // Get Content Resolver object, which will deal with Content Provider
                ContentResolver cr = getContentResolver();

                // Fetch Inbox SMS Message from Built-in Content Provider
                Cursor c = cr.query(inboxURI, reqCols, reqSelection, reqSelectionArgs, null);

                // Attached Cursor with adapter and display in listview
                adapter = new SimpleCursorAdapter(MainActivity.this, R.layout.row, c,
                        new String[] { "body", "address" }, new int[] {
                        R.id.lblText, R.id.lblPhone });
                lvMsg.setAdapter(adapter);

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
