package com.example.buymyride.data.models;

import java.util.List;

public class MyUser {
    private final UserId userId;
    private final String email;
    private final String name;
    private final String phoneNumber;
    private final List<String> favoriteCarIds;

    public MyUser(UserId userId, String email, String name, String phoneNumber, List<String> favoriteCarIds) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.favoriteCarIds = favoriteCarIds;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<String> getFavoriteCarIds() {
        return favoriteCarIds;
    }
}