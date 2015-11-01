package com.smalser.autobudget.edit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.smalser.autobudget.CategoriesRepository;
import com.smalser.autobudget.Category;
import com.smalser.autobudget.Message;
import com.smalser.autobudget.MyApplication;
import com.smalser.autobudget.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class EditMessageActivity extends Activity {

    TextView mFullMessageText;
    Button mBtnOk;
    EditText mEditPattern;
    EditText mEditCategoryPattern;
    Category curCategory;
    private Message editedMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);

        final MyApplication app = (MyApplication) getApplication();
        editedMessage = app.getEditedMessage();
        curCategory = editedMessage.getCategory();

        mFullMessageText = (TextView) findViewById(R.id.lblFullMessage);
        mFullMessageText.setText(app.getEditedMessage().fullMessage);
        mFullMessageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditPattern.setText(editedMessage.source);
            }
        });

        mBtnOk = (Button) findViewById(R.id.btnEditOk);
        mEditPattern = (EditText) findViewById(R.id.editPattern);
        mEditPattern.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CharSequence newPattern = s.toString();
                mEditCategoryPattern.setText(mEditCategoryPattern.getText() + "|(" + newPattern + ")");
            }
        });

        mEditCategoryPattern = (EditText) findViewById(R.id.editCategoryPattern);
        mEditCategoryPattern.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String template = mEditCategoryPattern.getText().toString();
                String color = "red";

                try {
                    color = editedMessage.source.matches(template) ? "green" : "red";
                    mBtnOk.setEnabled(true);
                    mEditCategoryPattern.setBackgroundColor(getResources().getColor(R.color.green));
                } catch (PatternSyntaxException exception) {
                    mBtnOk.setEnabled(false);
                    mEditCategoryPattern.setBackgroundColor(getResources().getColor(R.color.red));
                }

                String highlighted = editedMessage.fullMessage.replaceAll("(" + editedMessage.source + ")",
                        "<font color='" + color + "'>$1</font>");

                mFullMessageText.setText(Html.fromHtml(highlighted));
            }
        });

        //todo spinner adapter works wrong (string resources are not used)
        Spinner spinner = (Spinner) findViewById(R.id.spinnerCategories);
        List<Category> categories = new ArrayList<>(CategoriesRepository.allCategories());
        SpinnerAdapter adapter = new CategoriesAdapter(this, categories);
        spinner.setAdapter(adapter);
        spinner.setSelection(categories.indexOf(curCategory));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curCategory = (Category) parent.getItemAtPosition(position);
                app.getEditedMessage().setCategory(curCategory);
                updateCurCategory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String template = mEditCategoryPattern.getText().toString();
                saveTemplate(EditMessageActivity.this, template, curCategory);
                finish();
            }
        });

        updateCurCategory();
    }

    private void updateCurCategory() {
        String template = loadTemplate(this, curCategory);
        mEditCategoryPattern.setText(template);
    }

    private static String getPrefsName() {
        return "category_template_prefs";
    }

    public String loadTemplate(Context context, Category category) {
        SharedPreferences prefs = context.getSharedPreferences(getPrefsName(), 0);
        return prefs.getString(category.name, defaultTemplates(category.name));
    }

    public void saveTemplate(Context context, String template, Category category) {
        SharedPreferences prefs = context.getSharedPreferences(getPrefsName(), 0);
        prefs.edit().putString(category.name, template).apply();
    }

    private static String defaultTemplates(String name) {
        switch (name) {
            case "Qiwi":
                return "(W.QIWI.RU)|(Oplata scheta)"; //todo Oplata scheta ?
            case "Products":
                return "(O KEY)|(OKEY)|(.*AUCHAN.*)|(MIRATORG.*)|(.*PEREKRESTOK.*)|(KOFEYNAYA KANTA.*)|(LENTA)";
            case "Cash":
                return "(Snyatiye nalichnykh)|(Snjatie nalichnyh)";
            case "Sport":
                return "(.*SPORTMASTER.*)|(.* KANT .*)";
            case "Cafe":
                return "(MUMU)|(ZELENAYA GORCHIORENBURG)|(.*KFC.*)|(.*MCDONALDS.*)|(.*Burger Club.*)|" +
                        "(TASHIR)|(THE PASHA)|(DUNKIN DONUTS.*)|(GEISHA.*)|(SBARRO)|(SHOKO.*)|(CAFETERA WHITE)|(KOFETUN)";
            case "Transport":
                return "(WWW.RZD.RU)|(.*ORENBURG AIRLI.*)|(AEROFLOT.*)|(UZ.GOV.UA)|(RAILWAYTICKETS KYIV)";
            case "Clothes":
                return "(DZHULIANNA)|(.*CALZEDONIA.*)|(COLINS.*)|(MANGO)|(RESERVED.*)|(YNG)|(ZOLLA)|(RALFRINGER)|(OSTIN)|(MOHITO TTS RIO)";
            case "Relax":
                return "(.*CINEMA.*)|(WWW.KINOHOD.RU MOSCOW)|(KINOBAR)";
            case "Pharmacy":
                return "(.*Pharmacy.*)";
            case "Other":
                return ".*";
            default:
                return ".*";
        }
    }
}
