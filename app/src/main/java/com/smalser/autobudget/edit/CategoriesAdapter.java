package com.smalser.autobudget.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smalser.autobudget.Category;
import com.smalser.autobudget.R;

import java.util.List;

public class CategoriesAdapter extends ArrayAdapter<Category> {
    public CategoriesAdapter(Context context, int resource, List<Category> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView rowView = (TextView) layoutInflater.inflate(R.layout.spinner_layout, parent, false);
        TextView spinnerTarget = (TextView) rowView.findViewById(R.id.spinnerTarget);

        Category item = this.getItem(position);
        spinnerTarget.setText(getContext().getString(item.lblId()));

        return rowView;
    }
}
