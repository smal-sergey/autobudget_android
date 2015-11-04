package com.smalser.autobudget.edit;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.smalser.autobudget.Category;
import com.smalser.autobudget.CategoryCreatedCallback;
import com.smalser.autobudget.R;
import com.smalser.autobudget.Utils;

import java.util.Collections;
import java.util.List;

public class CategoriesAdapter extends BaseAdapter implements SpinnerAdapter {
    private final Activity activity;
    private final List<Category> items;

    public static final int PLAIN_CATEGORY_TYPE = 0;
    public static final int ADD_BUTTON_TYPE = 1;

    public CategoriesAdapter(Activity activity, List<Category> items) {
        this.activity = activity;
        Collections.sort(items);
        this.items = items;
    }

    @Override
    public int getViewTypeCount() {
        //ugly hack to make button in spinner
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position < items.size() ? PLAIN_CATEGORY_TYPE : ADD_BUTTON_TYPE;
    }

    @Override
    public int getCount() {
        //last one is button
        return items.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = convertView;
        int viewType = getItemViewType(position);

        Category item = position < items.size() ? (Category) this.getItem(position) : null;

        switch (viewType) {
            case PLAIN_CATEGORY_TYPE:
                rowView = layoutInflater.inflate(R.layout.spinner_text_row, parent, false);
                TextView textView = (TextView) rowView;
                textView.setText(item.getName());
                break;
            case ADD_BUTTON_TYPE:
                rowView = layoutInflater.inflate(R.layout.spinner_btn_row, parent, false);
                ImageButton btn = (ImageButton) rowView.findViewById(R.id.addCategoryBtn);
                btn.setOnClickListener(Utils.createCategoryListener(activity, new CategoryCreatedCallback() {
                    @Override
                    public void onCategoryCreated(View v, Category category) {
                        CategoriesAdapter adapter = CategoriesAdapter.this;
                        adapter.items.add(category);
                        Collections.sort(adapter.items);
                        adapter.notifyDataSetChanged();

                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }));
                break;
        }

        return rowView;
    }
}
