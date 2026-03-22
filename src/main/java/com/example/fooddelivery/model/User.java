package com.example.fooddelivery.model;

import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    private String name;
    private String email;
    private String password;

    // ✅ GETTERS & SETTERS

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {   // 🔥 FIX
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) { // 🔥 FIX
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) { // 🔥 FIX
        this.password = password;
    }
}