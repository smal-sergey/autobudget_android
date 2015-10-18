package com.smalser.autobudget.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.smalser.autobudget.Category;
import com.smalser.autobudget.R;

import java.util.List;

public class CategoriesAdapter extends BaseAdapter implements SpinnerAdapter {
    private final Context context;
    private final List<Category> items;

    public CategoriesAdapter(Context context, List<Category> objects) {
        this.context = context;
        this.items = objects;
    }

    @Override
    public int getCount() {
        return items.size();
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
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.spinner_layout, parent, false);
        }

        TextView rowView = (TextView) convertView;
        Category item = (Category) this.getItem(position);
        rowView.setText(item.name);

        return rowView;
    }
}
