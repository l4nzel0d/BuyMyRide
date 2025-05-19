package com.example.buymyride.data.models;

public class CarCardModel {
    private String id;
    private String make;
    private String model;
    private int year;
    private int price;
    private String imageUrl;
    private boolean isFavorite;

    public CarCardModel(String id, String make, String model, int year, int price, String imageUrl, boolean isFavorite) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isFavorite = isFavorite;
    }

    public String getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
