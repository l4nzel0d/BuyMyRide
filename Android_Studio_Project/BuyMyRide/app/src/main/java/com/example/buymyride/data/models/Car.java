package com.example.buymyride.data.models;

public final class Car {
    private final String make;
    private final String model;
    private final String imageUrl;
    private final long price;
    private final long creditPrice;
    private final int year;
    private final String trim;
    private final String bodyStyle;
    private final String transmission;
    private final String power;
    private final String engineDisplacement;
    private final String engineType;
    private final String drive;
    private final String exteriorColor;
    private final String steeringWheel;

    public Car(String make, String model, String imageUrl, long price, long creditPrice, int year,
               String trim, String bodyStyle, String transmission, String power,
               String engineDisplacement, String engineType, String drive, String exteriorColor,
               String steeringWheel) {
        this.make = make;
        this.model = model;
        this.imageUrl = imageUrl;
        this.price = price;
        this.creditPrice = creditPrice;
        this.year = year;
        this.trim = trim;
        this.bodyStyle = bodyStyle;
        this.transmission = transmission;
        this.power = power;
        this.engineDisplacement = engineDisplacement;
        this.engineType = engineType;
        this.drive = drive;
        this.exteriorColor = exteriorColor;
        this.steeringWheel = steeringWheel;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getPrice() {
        return price;
    }

    public long getCreditPrice() {
        return creditPrice;
    }

    public int getYear() {
        return year;
    }

    public String getTrim() {
        return trim;
    }

    public String getBodyStyle() {
        return bodyStyle;
    }

    public String getTransmission() {
        return transmission;
    }

    public String getPower() {
        return power;
    }

    public String getEngineDisplacement() {
        return engineDisplacement;
    }

    public String getEngineType() {
        return engineType;
    }

    public String getDrive() {
        return drive;
    }

    public String getExteriorColor() {
        return exteriorColor;
    }

    public String getSteeringWheel() {
        return steeringWheel;
    }
}
