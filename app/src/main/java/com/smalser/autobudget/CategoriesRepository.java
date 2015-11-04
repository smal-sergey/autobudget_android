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
    private static final Map<String, Category> nameToCategory = new HashMap<>();

    private static SharedPreferences categoryNamesPrefs;
    private static SharedPreferences categoryPrefs;
    private static SharedPreferences messagePrefs;

    public static Category OTHER;

    public static void initialize(Context context) {
        categoryNamesPrefs = context.getSharedPreferences(MainActivity.CATEGORY_NAMES_PREFS, Context.MODE_PRIVATE);
        categoryPrefs = context.getSharedPreferences(MainActivity.CATEGORY_PREFS, Context.MODE_PRIVATE);
        messagePrefs = context.getSharedPreferences(MainActivity.MESSAGE_PREFS, Context.MODE_PRIVATE);

        loadCategories();

        OTHER = create("Other", 0L);
    }

    private static void loadCategories() {
        for (String catId : categoryNamesPrefs.getAll().keySet()) {
            Long id = Long.valueOf(catId);
            String name = categoryNamesPrefs.getString(catId, "");
            create(name, id);
        }
    }

    public static Category create(String name) {
        if (nameToCategory.containsKey(name)) {
            Log.w(CATEGORIES_REPOSITORY_TAG, "Trying to create already existing category '" + name + "'");
            return nameToCategory.get(name);
        }
        return create(name, System.currentTimeMillis());
    }

    private static Category create(String name, long id) {
        Category category = new Category(id, name);
        nameToCategory.put(name, category);
        addCategoryToPrefs(category);

        return category;
    }

    public static void delete(Category category) {
        removeCategoryFromPrefs(category);
        nameToCategory.remove(category.getName());
    }

    public static boolean exists(String name) {
        return nameToCategory.containsKey(name);
    }

    public static Category get(long id) {
        for (Category cat : allCategories()) {
            if (cat.id == id) {
                return cat;
            }
        }
        Log.e(CATEGORIES_REPOSITORY_TAG, "Trying to get not existing category with id=" + id + ".");
        return null;
    }

    public static void rename(Category category, String name) {
        nameToCategory.remove(category.getName());
        nameToCategory.put(name, category);
        categoryNamesPrefs.edit().putString(category.getIdAsString(), name).apply();
        category.setName(name);
    }

    public static Collection<Category> allCategories() {
        return nameToCategory.values();
    }

    private static void removeCategoryFromPrefs(Category category) {
        Long catId = category.id;
        String strCatId = category.getIdAsString();
        categoryNamesPrefs.edit().remove(strCatId).apply();
        categoryPrefs.edit().remove(strCatId).apply();

        for (String msgId : messagePrefs.getAll().keySet()) {
            Long categoryId = messagePrefs.getLong(msgId, -1L);
            if (catId.equals(categoryId)) {
                messagePrefs.edit().remove(msgId).apply();
            }
        }
    }

    private static void addCategoryToPrefs(Category category) {
        String catId = category.getIdAsString();
        if (!categoryPrefs.contains(catId)) {
            categoryPrefs.edit().putStringSet(catId, new HashSet<String>()).apply();
        }
        if (!categoryNamesPrefs.contains(catId)) {
            categoryNamesPrefs.edit().putString(catId, category.getName()).apply();
        }
    }
}
