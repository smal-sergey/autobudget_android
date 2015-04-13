package com.smalser.autobudget.edit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smalser.autobudget.MyApplication;
import com.smalser.autobudget.R;

public class EditMessageActivity extends Activity {

    TextView mFullMessageText;
    Button mBtnOk;
    EditText mEditPattern;
    EditText mEditCategoryPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);

        final MyApplication app = (MyApplication) getApplication();

        mFullMessageText = (TextView) findViewById(R.id.lblFullMessage);
        mFullMessageText.setText(app.getEditedMessage().fullMessage);
        mBtnOk = (Button) findViewById(R.id.btnEditOk);
        mEditPattern = (EditText) findViewById(R.id.editPattern);
        mEditCategoryPattern = (EditText) findViewById(R.id.editCategoryPattern);

        //todo text view with full message should highlight (not)matched source
        //todo fill in spinner with categories (init with current category)

        mEditCategoryPattern.setText(app.getEditedMessage().getCategory().loadTemplate(this));
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String template = mEditCategoryPattern.getText().toString();
                app.getEditedMessage().getCategory().saveTemplate(EditMessageActivity.this, template);
            }
        });
    }
}
