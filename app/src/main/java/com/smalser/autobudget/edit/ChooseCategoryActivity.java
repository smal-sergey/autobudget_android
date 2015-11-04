package com.smalser.autobudget.edit;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.smalser.autobudget.CategoriesRepository;
import com.smalser.autobudget.Category;
import com.smalser.autobudget.Message;
import com.smalser.autobudget.MyApplication;
import com.smalser.autobudget.R;
import com.smalser.autobudget.main.MainActivity;

import java.util.ArrayList;
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
        mCurCategory.setText(curCategory.getName());

        mOnlyThisMessage = (RadioButton) findViewById(R.id.radio_only_this_msg);
        mAllFromSource = (RadioButton) findViewById(R.id.radio_all_from_source);

        mBtnOk = (Button) findViewById(R.id.btnEditOk);

        //todo spinner adapter works wrong (string resources are not used)
        Spinner spinner = (Spinner) findViewById(R.id.spinnerCategories);
        List<Category> categories = new ArrayList<>(CategoriesRepository.allCategories());
        SpinnerAdapter adapter = new CategoriesAdapter(this, categories);
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

                String newCatId = newCategory.getIdAsString();
                if (mOnlyThisMessage.isChecked()) {
                    msgPrefs.edit().putLong(message.id, newCategory.id).apply();
                } else if (mAllFromSource.isChecked()) {
                    String msgSource = message.source;

                    Map<String, Message> idToMessage = app.getStatisticCollector().getIdToMessage();
                    for (String id : msgPrefs.getAll().keySet()) {
                        if (idToMessage.get(id).source.equals(msgSource)) {
                            msgPrefs.edit().remove(id);
                        }
                    }

                    for (String catId : categoryPrefs.getAll().keySet()) {
                        Set<String> sources = categoryPrefs.getStringSet(catId, new HashSet<String>());
                        if (sources.remove(msgSource)) {
                            categoryPrefs.edit().putStringSet(catId, sources).apply();
                        }
                    }

                    Set<String> sources = categoryPrefs.getStringSet(newCatId, new HashSet<String>());
                    sources.add(msgSource);
                    categoryPrefs.edit().putStringSet(newCatId, sources).apply();
                }

                finish();
            }
        });
    }
}
