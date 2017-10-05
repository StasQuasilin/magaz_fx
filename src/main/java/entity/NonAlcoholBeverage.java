package entity;

/**
 * Created by ZPT_USER on 05.10.2017.
 */
public class NonAlcoholBeverage extends IBeverage {

    String consist;

    public NonAlcoholBeverage(String beverageName, double price, String beverageType, double volume, String consist, double availability) {
        this.beverageName = beverageName;
        this.price = price;
        this.beverageType = beverageType;
        this.volume = volume;
        this.consist = consist;
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

    public String getConsist() {
        return consist;
    }

    public void setConsist(String consist) {
        this.consist = consist;
    }

    public double getAvailability() {
        return availability;
    }

    public void setAvailability(double availability) {
        this.availability = availability;
    }

}
