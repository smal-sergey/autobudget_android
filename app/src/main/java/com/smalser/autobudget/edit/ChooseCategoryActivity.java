package com.smalser.autobudget.edit;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.smalser.autobudget.Category;
import com.smalser.autobudget.Message;
import com.smalser.autobudget.MyApplication;
import com.smalser.autobudget.R;
import com.smalser.autobudget.main.MainActivity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChooseCategoryActivity extends Activity {

    TextView mFullMessageText;
    TextView mCurCategory;

    RadioButton mOnlyThisMessage;
    RadioButton mAllFromSource;

    Button mBtnOk;
    Category curCategory;
    Category newCategory;
    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        final MyApplication app = (MyApplication) getApplication();
        message = app.getEditedMessage();
        curCategory = message.getCategory();
        newCategory = curCategory;

        String highlighted = message.fullMessage.replaceAll("(" + message.source + ")",
                "<font color='green'>$1</font>");
        mFullMessageText = (TextView) findViewById(R.id.lblFullMessage);
        mFullMessageText.setText(Html.fromHtml(highlighted));

        mCurCategory = (TextView) findViewById(R.id.lblCurCategory);
        mCurCategory.setText(curCategory.lblId());

        mOnlyThisMessage = (RadioButton) findViewById(R.id.radio_only_this_msg);
        mAllFromSource = (RadioButton) findViewById(R.id.radio_all_from_source);

        mBtnOk = (Button) findViewById(R.id.btnEditOk);

        //todo spinner adapter works wrong (string resources are not used)
        Spinner spinner = (Spinner) findViewById(R.id.spinnerCategories);
        List<Category> categories = Arrays.asList(Category.values());
        ArrayAdapter<Category> adapter = new CategoriesAdapter(this, R.layout.spinner_layout, categories);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(adapter);
        spinner.setSelection(categories.indexOf(curCategory));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newCategory = (Category) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //todo add warning on select 'move all from source'

        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences msgPrefs = getSharedPreferences(MainActivity.MESSAGE_PREFS, MODE_PRIVATE);
                SharedPreferences categoryPrefs = getSharedPreferences(MainActivity.CATEGORY_PREFS, MODE_PRIVATE);

                if (mOnlyThisMessage.isChecked()) {
                    msgPrefs.edit().putString(message.id, newCategory.toString()).apply();
                } else if (mAllFromSource.isChecked()) {
                    Set<String> oldSources = categoryPrefs.getStringSet(newCategory.toString(), new HashSet<String>());

                    Map<String, Message> idToMessage = app.getStatisticCollector().getIdToMessage();
                    for (String id : msgPrefs.getAll().keySet()) {
                        if (idToMessage.get(id).source.equals(message.source)) {
                            msgPrefs.edit().remove(id);
                        }
                    }

                    if (!oldSources.contains(message.source)) {
                        Set<String> sources = new HashSet<>(oldSources);
                        sources.add(message.source);
                        categoryPrefs.edit().putStringSet(newCategory.toString(), sources).apply();
                    }

                }

                finish();
            }
        });
    }
}
