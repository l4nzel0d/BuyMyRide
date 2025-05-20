package com.example.buymyride.util;

import com.example.buymyride.data.models.Car;

import java.util.Comparator;
import java.util.List;

public class CarSortUtils {

    public static void sortByPriceAscending(List<Car> cars) {
        cars.sort(Comparator.comparingLong(Car::price));
    }

    public static void sortByPriceDescending(List<Car> cars) {
        cars.sort((car1, car2) -> Long.compare(car2.price(), car1.price()));
    }

    public static void sortByYearNewest(List<Car> cars) {
        cars.sort((car1, car2) -> Integer.compare(car2.year(), car1.year()));
    }

    public static void sortByYearOldest(List<Car> cars) {
        cars.sort(Comparator.comparingInt(Car::year));
    }

}
