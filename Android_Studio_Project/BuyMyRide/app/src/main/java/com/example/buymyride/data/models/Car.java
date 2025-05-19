package com.example.buymyride.data.models;

public record Car(
        String id,
        String imageUrl,
        String make,
        String model,
        int year,
        long price,
        long creditPrice,
        String bodyStyle,
        String transmission,
        String power,
        String engineDisplacement,
        String engineType,
        String drive,
        String exteriorColor,
        String steeringWheel
) {}
