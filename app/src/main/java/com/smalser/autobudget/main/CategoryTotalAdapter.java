package com.smalser.autobudget.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.smalser.autobudget.CategoriesRepository;
import com.smalser.autobudget.R;
import com.smalser.autobudget.Utils;

import java.util.List;

public class CategoryTotalAdapter extends ArrayAdapter<CategoryTotal> {
    public static final int PLAIN_CATEGORY_TYPE = 0;
    public static final int SELECTED_CATEGORY_TYPE = 1;
    public static final int NUMBER_CATEGORY_TYPES = 2;

    public CategoryTotalAdapter(Context context, int resource, List<CategoryTotal> objects) {
        super(context, resource, objects);
    }

    @Override
    public int getViewTypeCount() {
        return NUMBER_CATEGORY_TYPES;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).selected ? SELECTED_CATEGORY_TYPE : PLAIN_CATEGORY_TYPE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = convertView;
        final CategoryTotal ct = this.getItem(position);

        int viewType = getItemViewType(position);
        if (convertView == null) {
            switch (viewType) {
                case PLAIN_CATEGORY_TYPE:
                    rowView = layoutInflater.inflate(R.layout.category_total_row, parent, false);
                    break;
                case SELECTED_CATEGORY_TYPE:
                    rowView = layoutInflater.inflate(R.layout.category_total_row_selected, parent, false);
                    break;
                default:
                    rowView = layoutInflater.inflate(R.layout.category_total_row, parent, false);
            }

            TextView category = (TextView) rowView.findViewById(R.id.lblCategory);
            TextView total = (TextView) rowView.findViewById(R.id.lblCategoryTotal);
            TextView count = (TextView) rowView.findViewById(R.id.lblCount);
            ImageButton deleteBtn = (ImageButton) rowView.findViewById(R.id.deleteCategoryBtn);
            if (deleteBtn != null) {
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remove(ct);
                        Toast.makeText(getContext(), "Category " + ct.category.name + " deleted", Toast.LENGTH_SHORT).show();
                        CategoriesRepository.delete(ct.category);
                    }
                });
            }

            rowView.setTag(new ViewHolder(category, total, count));
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.category.setText(ct.category.name);
        holder.total.setText(Utils.getFormattedCash(ct.result));
        holder.count.setText(String.format("(%d)", ct.messages.size()));

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
