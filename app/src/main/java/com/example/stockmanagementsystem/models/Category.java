package com.example.stockmanagementsystem.models;

public class Category {

    private String id;
    private String name;

    public Category() {
        // Required for Firebase
    }

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
