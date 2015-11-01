package com.smalser.autobudget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.smalser.autobudget.main.MainActivity;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CategoriesRepository {
    private static final String CATEGORIES_REPOSITORY_TAG = "Cat_repository_log";
    private static final Map<String, Category> categories = new HashMap<>();

    public static Category OTHER;
    private static Context context;

    public static void initialize(Context context) {
        CategoriesRepository.context = context;
        OTHER = create("Other");
    }

    public static Category create(String name) {
        if (categories.containsKey(name)) {
            Log.w(CATEGORIES_REPOSITORY_TAG, "Trying to create already existing category '" + name + "'");
            return categories.get(name);
        }
        Category category = new Category(name);
        categories.put(name, category);
        addCategoryToPrefs(category);

        return category;
    }

    public static boolean delete(Category category) {
        removeCategoryFromPrefs(category);
        return allCategories().remove(category);
    }

    public static Category valueOf(String name) {
        Category category = categories.get(name);
        if (category == null) {
            Log.e(CATEGORIES_REPOSITORY_TAG, "Trying to use not existing category '" + name + "'");
        }
        return category;
    }

    public static Collection<Category> allCategories() {
        return categories.values();
    }

    private static void removeCategoryFromPrefs(Category category) {
        SharedPreferences categoryPrefs = context.getSharedPreferences(MainActivity.CATEGORY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences messagePrefs = context.getSharedPreferences(MainActivity.MESSAGE_PREFS, Context.MODE_PRIVATE);

        categoryPrefs.edit().remove(category.name).apply();

        for (String id : messagePrefs.getAll().keySet()) {
            String categoryName = messagePrefs.getString(id, CategoriesRepository.OTHER.name);
            if (categoryName.equals(category.name)) {
                messagePrefs.edit().remove(id).apply();
            }
        }
    }

    private static void addCategoryToPrefs(Category category) {
        SharedPreferences categoryPrefs = context.getSharedPreferences(MainActivity.CATEGORY_PREFS, Context.MODE_PRIVATE);

        if (!categoryPrefs.contains(category.name)) {
            categoryPrefs.edit().putStringSet(category.name, new HashSet<String>()).apply();
        }
    }
}
