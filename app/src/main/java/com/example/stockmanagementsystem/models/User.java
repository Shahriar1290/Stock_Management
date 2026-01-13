package com.example.stockmanagementsystem.models;
public class User {
    public String uid;
    public String name;
    public String email;
    public String role;

    public User() {}

    public User(String uid, String name, String email, String role) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
