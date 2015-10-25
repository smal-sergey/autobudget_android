package com.smalser.autobudget.main;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.smalser.autobudget.Category;
import com.smalser.autobudget.Message;
import com.smalser.autobudget.MyApplication;
import com.smalser.autobudget.R;
import com.smalser.autobudget.collector.GetAllCategoriesAsync;
import com.smalser.autobudget.collector.MessageCompiler;
import com.smalser.autobudget.collector.SmsParser;
import com.smalser.autobudget.collector.StatisticCollector;
import com.smalser.autobudget.report.CategoryReportActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity {
    private static final String MAIN_ACTIVITY_TAG = "Main_log";

    public static final String CATEGORY_PREFS = "category_prefs";
    public static final String MESSAGE_PREFS = "message_prefs";

    private final SmsParser smsParser = initSmsParser();

    ImageButton mAddCategoryBtn;
    TextView mTotalTxt;
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
        mAddCategoryBtn = (ImageButton) findViewById(R.id.addCategoryBtn);
        mAddCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder addCategoryDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                addCategoryDialogBuilder.setCancelable(true)
                        .setMessage("Please Enter data")
                        .setTitle(R.string.create_category_title)
                        .setView(R.layout.add_category_dialog) //<-- layout containing EditText
                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        });

                final AlertDialog addCategoryDialog = addCategoryDialogBuilder.create();
                addCategoryDialog.show();
                Button addButton = addCategoryDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText mCategoryName = (EditText) addCategoryDialog.findViewById(R.id.newCategoryName);
                        String name = mCategoryName.getText().toString();
                        if (Category.valueOf(name) != null) {
                            Toast.makeText(getBaseContext(), "Category already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            Category.create(name);
                            updateCategories();
                            addCategoryDialog.dismiss();
                        }
                    }
                });
            }
        });

        readAllMessages();

        mCategories = (ListView) findViewById(R.id.listCategories);
        mCategories.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int selectedPos = findSelectedPos();
                ArrayAdapter<CategoryTotal> adapter = (ArrayAdapter) mCategories.getAdapter();
                CategoryTotal category = adapter.getItem(position);

                if (position == selectedPos) {
                    category.selected = false;
                } else {
                    if (selectedPos != -1) {
                        CategoryTotal selectedCategory = adapter.getItem(selectedPos);
                        selectedCategory.selected = false;
                    }
                    category.selected = true;
                }

                adapter.notifyDataSetChanged();
                return true;
            }

            private int findSelectedPos() {
                ListAdapter adapter = mCategories.getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    CategoryTotal category = (CategoryTotal) adapter.getItem(i);
                    if (category.selected) {
                        return i;
                    }
                }
                return -1;
            }
        });

        mCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryTotal categoryTotal = (CategoryTotal) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, CategoryReportActivity.class);
                intent.putExtra(CategoryReportActivity.CATEGORY_EXTRA, categoryTotal.category.name);
                startActivity(intent);
            }
        });

        mTotalTxt = (TextView) findViewById(R.id.lblValueTotal);

        mDateFilterTxt = (TextView) findViewById(R.id.lblValueDateFilter);
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

    private SmsParser initSmsParser() {
        //let it be static and predefined list of sources for now
        Map<Pattern, MessageCompiler> patterns = new HashMap<>();

        final SimpleDateFormat citiDateFormat = new SimpleDateFormat("dd/MM/yy");
        MessageCompiler citiCompiler = new MessageCompiler() {
            @Override
            public Message getMessage(String id, Matcher m) throws ParseException {
                String fullMessage = m.group(0);
                Double purchase = Double.parseDouble(m.group(1));
                String source = m.group(2);
                Calendar date = Calendar.getInstance();
                date.setTime(citiDateFormat.parse(m.group(3)));

                Double balance = Double.parseDouble(m.group(4));
                return new Message(id, fullMessage, purchase, source, date, balance);
            }
        };

        String citiPatternWithdraw = "Spisanie:? (\\d+\\.\\d+).*\\s*Operaciya:? ([\\w\\d ]+[\\w\\d]).*\\s*Data:? (\\d\\d/\\d\\d/\\d\\d).*\\s*Balans:? (\\d+\\.\\d+) .*";
        patterns.put(Pattern.compile(citiPatternWithdraw), citiCompiler);

        String citiPatternBuy = "Pokupka:? (\\d+\\.\\d+).*\\s*Torgovaya tochka:? ([\\w\\d\\.\"& ]+[\\w\\d]).*\\s*Data:? (\\d\\d/\\d\\d/\\d\\d).*\\s*Balans:? (\\d+\\.\\d+) .*";
        patterns.put(Pattern.compile(citiPatternBuy), citiCompiler);

        final SimpleDateFormat raifDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        MessageCompiler raifCompiler = new MessageCompiler() {
            @Override
            public Message getMessage(String id, Matcher m) throws ParseException {
                String fullMessage = m.group(0);
                String source = m.group(1);
                Double purchase = Double.parseDouble(m.group(2));
                Calendar date = Calendar.getInstance();
                date.setTime(raifDateFormat.parse(m.group(3)));

                Double balance = Double.parseDouble(m.group(4));
                return new Message(id, fullMessage, purchase, source, date, balance);
            }
        };

        String raifPatternBuy = ".*Pokupka:? (.+?);\\s*(\\d+\\.\\d+) RUR;\\s*Data:? (\\d\\d/\\d\\d/\\d\\d\\d\\d);\\s*Dostupny Ostatok: (\\d+\\.\\d+) RUR.*\\s*.*";
        patterns.put(Pattern.compile(raifPatternBuy), raifCompiler);

        String raifPatternOperation = ".*Provedena operacija:? (.+?);\\s*(\\d+\\.\\d+) RUR;\\s*Data:? (\\d\\d/\\d\\d/\\d\\d\\d\\d);\\s*Dostupny Ostatok: (\\d+\\.\\d+) RUR.*\\s*.*";
        patterns.put(Pattern.compile(raifPatternOperation), raifCompiler);

        String raifPatternWithdraw = ".*Snyatie nalichnih:? (.+?);\\s*(\\d+\\.\\d+) RUR;\\s*Data:? (\\d\\d/\\d\\d/\\d\\d\\d\\d);\\s*Dostupny Ostatok: (\\d+\\.\\d+) RUR.*\\s*.*";
        patterns.put(Pattern.compile(raifPatternWithdraw), raifCompiler);

        return new SmsParser(patterns);
    }

    private void readAllMessages() {
        Uri inboxURI = Uri.parse("content://sms/inbox");

        String[] reqCols = new String[]{"_id", "address", "body"};
        String reqSelection = "address in (?, ?)";
        String[] reqSelectionArgs = new String[]{"Citialert", "Raiffeisen"};

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver cr = getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        Cursor c = cr.query(inboxURI, reqCols, reqSelection, reqSelectionArgs, null);

        Map<String, String> messages = read(c);
        allMessages = smsParser.parse(messages);
        app.setStatisticCollector(new StatisticCollector(allMessages, this));
    }

    private Map<String, String> read(Cursor c) {
        Map<String, String> messages = new HashMap<>();
        while (c.moveToNext()) {
            messages.put(c.getString(0), c.getString(2));
        }
        return messages;
    }

    private void updateCategories() {
        //todo categories can be grouped: use ExpandableListView
        ArrayAdapter<CategoryTotal> msgAdapter = new CategoryTotalAdapter(MainActivity.this, R.layout.category_total_row,
                new ArrayList<CategoryTotal>());
        mCategories.setAdapter(msgAdapter);

        GetAllCategoriesAsync getAllTask = new GetAllCategoriesAsync(mLoadingPanel, msgAdapter, mTotalTxt, app.getStatisticCollector());
        getAllTask.execute(app.getCurDate());
    }

    private void updateDate() {
        mDateFilterTxt.setText(app.getCurDateString());
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
