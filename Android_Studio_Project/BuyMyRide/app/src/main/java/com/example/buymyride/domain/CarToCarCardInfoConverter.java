package com.example.buymyride.domain;

import com.example.buymyride.data.models.Car;
import com.example.buymyride.data.models.CarCardInfo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CarToCarCardInfoConverter {
    public static CarCardInfo convert(Car car) {
        String name = String.format("%s %s", car.getMake(), car.getModel());
        String imageUrl = car.getImageUrl();
        String price  = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.US) {{ setGroupingSeparator(' '); }}).format(car.getPrice()) + " ₽";
        String details = String.format("%s / %s / %s / %s / %s / %s", car.getBodyStyle(), car.getTransmission(), car.getEngineDisplacement(), car.getPower(), car.getEngineType(), car.getDrive());

        return new CarCardInfo(name, imageUrl, price, details, false);
    }
}
