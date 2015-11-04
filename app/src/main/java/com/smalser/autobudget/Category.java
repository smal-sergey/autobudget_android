package com.smalser.autobudget;

import android.support.annotation.NonNull;

public class Category implements Comparable<Category> {
    public final long id;
    private String name;

    Category(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category " + name;
    }

    public String getIdAsString() {
        return Long.valueOf(id).toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Category && Long.compare(id, ((Category) o).id) == 0;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public int compareTo(@NonNull Category another) {
        Category other = CategoriesRepository.OTHER;
        if (other.equals(this)) {
            return 1;
        } else if (other.equals(another)) {
            return -1;
        } else {
            return getName().compareTo(another.getName());
        }
    }
}
