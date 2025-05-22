package com.example.buymyride.data.models;

public record CarCardModel(
        String id,
        String make,
        String model,
        int year,
        int price,
        String imageUrl,
        boolean isFavorite
) {}
