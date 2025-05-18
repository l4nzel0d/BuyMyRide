package com.example.buymyride.data.models;

public final class CarCardInfo {
    private final String name;
    private final String imageUrl;
    private final String price;
    private final String details;
    private final boolean isFavorite;

    public CarCardInfo(String name, String imageUrl, String price, String details, boolean isFavorite) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.details = details;
        this.isFavorite = isFavorite;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public String getDetails() {
        return details;
    }

    public boolean isFavorite() {
        return isFavorite;
    }
}
