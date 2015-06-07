package com.smalser.autobudget.report;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smalser.autobudget.Message;
import com.smalser.autobudget.R;
import com.smalser.autobudget.Utils;

import java.util.List;

public class CategoryReportAdapter extends ArrayAdapter<Message> {
    public CategoryReportAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = layoutInflater.inflate(R.layout.categories_report_row, parent, false);

            TextView source = (TextView) rowView.findViewById(R.id.lblReportSource);
            TextView date = (TextView) rowView.findViewById(R.id.lblReportDate);
            TextView purchase = (TextView) rowView.findViewById(R.id.lblReportPurchase);

            rowView.setTag(new ViewHolder(source, date, purchase));
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        Message msg = this.getItem(position);

        holder.source.setText(msg.source);
        holder.date.setText(msg.getDateString());
        holder.purchase.setText(Utils.getFormattedCash(msg.purchase));

        return rowView;
    }

    class ViewHolder {
        private final TextView source;
        private final TextView date;
        private final TextView purchase;

        ViewHolder(TextView source, TextView date, TextView purchase) {
            this.source = source;
            this.date = date;
            this.purchase = purchase;
        }
    }
}
