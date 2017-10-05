package entity;

/**
 * Created by ZPT_USER on 05.10.2017.
 */
public class AlcoholBeverage extends IBeverage {

    double strength;

    public AlcoholBeverage(String beverageName, double price, String beverageType, double volume, double strength, double availability) {
        this.beverageName = beverageName;
        this.price = price;
        this.beverageType = beverageType;
        this.volume = volume;
        this.strength = strength;
        this.availability = availability;
    }

    public String getBeverageName() {
        return beverageName;
    }

    public void setBeverageName(String beverageName) {
        this.beverageName = beverageName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBeverageType() {
        return beverageType;
    }

    public void setBeverageType(String beverageType) {
        this.beverageType = beverageType;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    public double getAvailability() {
        return availability;
    }

    public void setAvailability(double availability) {
        this.availability = availability;
    }
}

