package com.smalser.autobudget.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smalser.autobudget.R;
import com.smalser.autobudget.Utils;

import java.util.List;

public class CategoryTotalAdapter extends ArrayAdapter<CategoryTotal> {
    public CategoryTotalAdapter(Context context, int resource, List<CategoryTotal> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = layoutInflater.inflate(R.layout.category_total_row, parent, false);

            TextView category = (TextView) rowView.findViewById(R.id.lblCategory);
            TextView total = (TextView) rowView.findViewById(R.id.lblCategoryTotal);
            TextView count = (TextView) rowView.findViewById(R.id.lblCount);

            rowView.setTag(new ViewHolder(category, total, count));
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        CategoryTotal ct = this.getItem(position);

        holder.category.setText(ct.category.lblId());
        holder.total.setText(Utils.getFormattedCash(ct.result));
        holder.count.setText("(" + ct.messages.size() + ")");

        return rowView;
    }

    class ViewHolder {
        private final TextView category;
        private final TextView total;
        private final TextView count;

        ViewHolder(TextView category, TextView total, TextView count) {
            this.category = category;
            this.total = total;
            this.count = count;
        }
    }
}
