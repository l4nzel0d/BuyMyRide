package com.example.buymyride.util;

import com.example.buymyride.data.models.Car;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import com.example.buymyride.R;

public enum SortOption {
    YEAR_ASC(R.id.sort_by_year_asc, "Year ↑", Comparator.comparingInt(Car::year)),
    YEAR_DESC(R.id.sort_by_year_desc, "Year ↓", Comparator.comparingInt(Car::year).reversed()),
    PRICE_ASC(R.id.sort_by_price_asc, "Price ↑", Comparator.comparingDouble(Car::price)),
    PRICE_DESC(R.id.sort_by_price_desc, "Price ↓", Comparator.comparingDouble(Car::price).reversed());

    private final int menuItemId;
    private final String displayName;
    private final Comparator<Car> comparator;

    SortOption(int menuItemId, String displayName, Comparator<Car> comparator) {
        this.menuItemId = menuItemId;
        this.displayName = displayName;
        this.comparator = comparator;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Comparator<Car> getComparator() {
        return comparator;
    }

    private static final Map<Integer, SortOption> ID_MAP = new HashMap<>();

    static {
        for (SortOption option : values()) {
            ID_MAP.put(option.menuItemId, option);
        }
    }

    public static SortOption fromMenuItemId(int id) {
        return ID_MAP.get(id);
    }
}
