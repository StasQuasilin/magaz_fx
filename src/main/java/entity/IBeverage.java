package entity;

/**
 * Created by ZPT_USER on 05.10.2017.
 */
public abstract class IBeverage {
    String beverageName;
    double price;
    String beverageType;
    double volume;
    double availability;

    @Override
    public String toString() {
        return "[ " + beverageName + " ]";
    }
}
