package entity;

public class BuyInfo{
    IBeverage beverage;
    double count;
    double markup;

    public BuyInfo(IBeverage beverage, double cou, int makeUp) {
        this.beverage = beverage;
        this.count=cou;
        this.markup = makeUp;
    }

    public IBeverage getBeverage() {
        return beverage;
    }

    public void setBeverage(IBeverage beverage) {
        this.beverage = beverage;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public double getMarkup() {
        return markup;
    }

    public void setMarkup(double markup) {
        this.markup = markup;
    }
}
