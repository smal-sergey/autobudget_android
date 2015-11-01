package com.smalser.autobudget;

public class Category {
    public final String name;

    Category(String name) {
        this.name = name;
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
