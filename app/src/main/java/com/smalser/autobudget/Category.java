package com.smalser.autobudget;

public class Category {
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
}
