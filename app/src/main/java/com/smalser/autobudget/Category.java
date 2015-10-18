package com.smalser.autobudget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Category {
    private static final String CATEGORY_TAG = "Category_log";
    private static final Map<String, Category> categories = new HashMap<>();

    public static final Category OTHER;

    static {
        OTHER = create("Other");
        create("Qiwi");
        create("Products");
        create("Cash");
        create("Sport");
        create("Cafe");
        create("Transport");
        create("Clothes");
        create("Relax");
        create("Pharmacy");
    }

    public final String name;

    private Category(String name) {
        this.name = name;
    }

    private String getPrefsName() {
        return "category_template_prefs";
    }

    public String loadTemplate(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(getPrefsName(), 0);
        return prefs.getString(name, defaultTemplates());
    }

    public void saveTemplate(Context context, String template) {
        SharedPreferences prefs = context.getSharedPreferences(getPrefsName(), 0);
        prefs.edit().putString(name, template).apply();
    }

    private String defaultTemplates() {
        switch (name) {
            case "Qiwi":
                return "(W.QIWI.RU)|(Oplata scheta)"; //todo Oplata scheta ?
            case "Products":
                return "(O KEY)|(OKEY)|(.*AUCHAN.*)|(MIRATORG.*)|(.*PEREKRESTOK.*)|(KOFEYNAYA KANTA.*)|(LENTA)";
            case "Cash":
                return "(Snyatiye nalichnykh)|(Snjatie nalichnyh)";
            case "Sport":
                return "(.*SPORTMASTER.*)|(.* KANT .*)";
            case "Cafe":
                return "(MUMU)|(ZELENAYA GORCHIORENBURG)|(.*KFC.*)|(.*MCDONALDS.*)|(.*Burger Club.*)|" +
                        "(TASHIR)|(THE PASHA)|(DUNKIN DONUTS.*)|(GEISHA.*)|(SBARRO)|(SHOKO.*)|(CAFETERA WHITE)|(KOFETUN)";
            case "Transport":
                return "(WWW.RZD.RU)|(.*ORENBURG AIRLI.*)|(AEROFLOT.*)|(UZ.GOV.UA)|(RAILWAYTICKETS KYIV)";
            case "Clothes":
                return "(DZHULIANNA)|(.*CALZEDONIA.*)|(COLINS.*)|(MANGO)|(RESERVED.*)|(YNG)|(ZOLLA)|(RALFRINGER)|(OSTIN)|(MOHITO TTS RIO)";
            case "Relax":
                return "(.*CINEMA.*)|(WWW.KINOHOD.RU MOSCOW)|(KINOBAR)";
            case "Pharmacy":
                return "(.*Pharmacy.*)";
            case "Other":
                return ".*";
            default:
                return ".*";
        }
    }

    public static Category create(String name) {
        if (categories.containsKey(name)) {
            Log.w(CATEGORY_TAG, "Trying to create already existing category '" + name + "'");
            return categories.get(name);
        }
        Category category = new Category(name);
        categories.put(name, category);
        return category;
    }

    public static Category valueOf(String name) {
        Category category = categories.get(name);
        if(category == null){
            Log.e(CATEGORY_TAG, "Trying to use not existing category '" + name + "'");
        }
        return category;
    }

    public static Collection<Category> allCategories() {
        return categories.values();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Category other = (Category) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.name.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "Category " + name;
    }
}
