package com.example.buymyride.data.models;

import java.util.List;
import java.util.Map;

public record Car(
        String id,
        String imageUrl,
        String make,
        String model,
        int year,
        long price,
        long creditPrice,
        List<Map<String, String>> features
) {
}
