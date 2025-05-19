package com.example.buymyride.data.models;

import java.util.List;

public record Car(
        String id,
        String imageUrl,
        String make,
        String model,
        int year,
        long price,
        long creditPrice,
        List<SpecItem> specs
) {
}
