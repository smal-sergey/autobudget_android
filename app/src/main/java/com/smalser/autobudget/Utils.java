package com.smalser.autobudget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    private static DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
    private static DecimalFormat formatter;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');

        formatter = new DecimalFormat("0.00#", symbols);
        formatter.setMaximumFractionDigits(2);
        formatter.setGroupingUsed(true);
    }

    public static String stringifyDate(Calendar c) {
        return df.format(new Date(c.getTimeInMillis()));
    }

    public static String getFormattedCash(double value) {
        return formatter.format(value);
    }

    public static View.OnClickListener createCategoryListener(final Context context, final CategoryCreatedCallback categoryCreatedCallback) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder addCategoryDialogBuilder = new AlertDialog.Builder(context);
                addCategoryDialogBuilder.setCancelable(true)
                        .setMessage(R.string.enter_category_name)
                        .setTitle(R.string.create_category_title)
                        .setView(R.layout.add_category_dialog) //<-- layout containing EditText
                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
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
                        if (CategoriesRepository.exists(name)) {
                            Toast.makeText(context, R.string.category_exists, Toast.LENGTH_SHORT).show();
                        } else {
                            Category category = CategoriesRepository.create(name);
                            categoryCreatedCallback.onCategoryCreated(v, category);
                            addCategoryDialog.dismiss();
                        }
                    }
                });
            }
        };
    }
}
