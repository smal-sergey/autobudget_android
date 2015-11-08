package com.smalser.autobudget.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.smalser.autobudget.CategoriesRepository;
import com.smalser.autobudget.R;
import com.smalser.autobudget.Utils;

import java.util.List;

public class CategoryTotalAdapter extends ArrayAdapter<CategoryTotal> {
    private static final String CAT_TOTAL_ADAPTER_TAG = "CatTotalAdapter_log";

    public static final int PLAIN_CATEGORY_TYPE = 0;
    public static final int SELECTED_CATEGORY_TYPE = 1;
    public static final int NUMBER_CATEGORY_TYPES = 2;
    private final MainActivity mainActivity;

    public CategoryTotalAdapter(MainActivity mainActivity, int resource, List<CategoryTotal> objects) {
        super(mainActivity, resource, objects);
        this.mainActivity = mainActivity;
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        final LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = convertView;

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
                        CategoryTotal ct = getSelected();

                        String titleTemplate = getContext().getString(R.string.delete_category_dialog_title_template);
                        String title = String.format(titleTemplate, ct.category.getName());

                        final AlertDialog.Builder deleteCategoryDialogBuilder = new AlertDialog.Builder(getContext());

                        deleteCategoryDialogBuilder.setCancelable(true)
                                .setMessage(R.string.delete_category_dialog_msg)
                                .setTitle(title)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        CategoryTotal ct = getSelected();
                                        remove(ct);
                                        Toast.makeText(getContext(), "Category " + ct.category.getName() + " deleted", Toast.LENGTH_SHORT).show();
                                        CategoriesRepository.delete(ct.category);
                                        mainActivity.updateCategories();
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        deleteCategoryDialogBuilder.create().show();
                    }
                });
            }

            //todo move listener creation to Utils
            ImageButton editBtn = (ImageButton) rowView.findViewById(R.id.editCategoryBtn);
            if (editBtn != null) {
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CategoryTotal ct = getSelected();
                        Log.i(CAT_TOTAL_ADAPTER_TAG, "Start editing " + ct);

                        final View catTextView = layoutInflater.inflate(R.layout.add_category_dialog, null);
                        ((EditText) catTextView.findViewById(R.id.newCategoryName)).setText(ct.category.getName());


                        final AlertDialog.Builder editCategoryDialogBuilder = new AlertDialog.Builder(getContext());

                        editCategoryDialogBuilder.setCancelable(true)
                                .setMessage(R.string.enter_category_name)
                                .setTitle(R.string.edit_category_title)
                                .setView(catTextView)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        final AlertDialog editCategoryDialog = editCategoryDialogBuilder.create();
                        editCategoryDialog.show();
                        Button addButton = editCategoryDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        addButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText mCategoryName = (EditText) editCategoryDialog.findViewById(R.id.newCategoryName);
                                String name = mCategoryName.getText().toString();
                                CategoryTotal ct = getSelected();

                                if (name.equals(ct.category.getName())) {
                                    editCategoryDialog.dismiss();
                                } else if (CategoriesRepository.exists(name)) {
                                    Toast.makeText(getContext(), R.string.category_exists, Toast.LENGTH_SHORT).show();
                                } else if (name.isEmpty()) {
                                    Toast.makeText(getContext(), R.string.category_name_empty_alarm, Toast.LENGTH_SHORT).show();
                                } else {
                                    CategoriesRepository.rename(ct.category, name);
                                    editCategoryDialog.dismiss();
                                }
                            }
                        });
                    }
                });
            }

            rowView.setTag(new ViewHolder(category, total, count));
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        CategoryTotal ct = getItem(position);
        holder.category.setText(ct.category.getName());
        holder.total.setText(Utils.getFormattedCash(ct.result));
        holder.count.setText(String.format("(%d)", ct.messages.size()));

        return rowView;
    }

    private CategoryTotal getSelected() {
        for (int i = 0; i < getCount(); i++) {
            CategoryTotal category = getItem(i);
            if (category.selected) {
                return category;
            }
        }
        return null;
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
