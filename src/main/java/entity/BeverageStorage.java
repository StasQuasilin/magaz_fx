package entity;

import org.apache.log4j.Logger;
import utils.Utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by ZPT_USER on 05.10.2017.
 */
public class BeverageStorage {
    private static final Logger log = Logger.getLogger(BeverageStorage.class);
    List<BeverageValue> rests;
    List<String> news = new LinkedList<>();
    List<String> infos = new LinkedList<>();

    File file;
    public void readFile(File file) {
        this.file = file;

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), "UTF8"));

            rests = new LinkedList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    List<String> items = Utils.parseLine(line);

                    IBeverage beverage;

                    try {
                        String title = items.get(0);
                        double price = Double.parseDouble(items.get(1));
                        String type = items.get(2);
                        double volume = Double.parseDouble(items.get(3));
                        double availability = Double.parseDouble(items.get(5));

                        try {
                            double strength = Double.parseDouble(items.get(4));
                            beverage = new AlcoholBeverage(title, price, type, volume, strength, availability);
                        } catch (NumberFormatException ignore) {
                            String consist = items.get(4);
                            beverage = new NonAlcoholBeverage(title, price, type, volume, consist, availability);
                        }

                        addNews("Добавлено " + beverage.availability + " " + beverage + " на склад");
                        rests.add(new BeverageValue(beverage, this));

                    } catch (Exception e) {
                        e.printStackTrace();
                        addNews("Неправильная информация " + line);
                    }
                }
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        purchase();
    }

    public void purchase() {
        addNews("Итоги дня");

        rests.forEach(entity.BeverageValue::checkCount);
    }
    Random r = new Random();

    public List<BuyInfo> tryBuy(int count, int makeUp) {
        List<BuyInfo> buyInfos = new ArrayList<>();

        List<BeverageValue> temp = new LinkedList<>();

        for (BeverageValue bv : rests) {
            if (bv.beverage.availability > 0) {
                temp.add(bv);
            }
        }

        int buyCount = 0;

        if (temp.size() > 0) {
            while (buyCount < count) {

                BeverageValue value = temp.get(r.nextInt(temp.size()));

                if (value.beverage.availability > 0) {
                    int c = r.nextInt(10);
                    if (c > 0) {
                        buyCount += buy(c, buyInfos, makeUp, value);
                    }
                }

            }
        }

        return buyInfos;
    }

    private static double buy(int count, List<BuyInfo> productBox, int makeUp, BeverageValue bv) {
        if (productBox.size() > 1)
            makeUp = 7;

        double c = bv.sale(count, makeUp);
        productBox.add(new BuyInfo(bv.beverage, c, makeUp));
        return c;
    }

    public int newsSize() {
        return news.size();
    }

    public List<String> getNews() {
        List<String> temp = news;
        news = new LinkedList<>();
        infos.addAll(temp);
        return temp;
    }

    void addNews(String value) {
        log.info(value);
        news.add(value);
    }

    public List<String> getInfos() {
        return infos;
    }

    public void save() {
        try {

//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(
//                            new FileInputStream(file), "UTF8"));

            BufferedWriter writer1 = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file), StandardCharsets.UTF_8
                    )
            );

            BufferedWriter writer2 = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream("statistic.txt"),  StandardCharsets.UTF_8
                    )
            );

            for (BeverageValue bv : rests) {
                writer1.write(bv.getString());
                writer1.newLine();

                writer2.write(bv.getStatistic());
                writer2.newLine();
            }
            writer1.flush();
            writer1.close();

            writer2.flush();
            writer2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class BeverageValue {
    IBeverage beverage;
    BeverageStorage storage;

    double purchaseCount = 0;
    double totalPurchaseSum = 0;

    double saleCount = 0;
    double totalSaleSum = 0;

    int pCount = 150;

    public BeverageValue(IBeverage beverage, BeverageStorage beverageStorage) {
        this.beverage = beverage;
        this.storage = beverageStorage;
    }

    void checkCount() {
        if (beverage.availability < 11) {
            beverage.availability += pCount;

            totalPurchaseSum += pCount * beverage.price;
            purchaseCount += pCount;

            storage.addNews("\tЗакуплено " + pCount + " продукта " + beverage);
        } else {
            storage.addNews("\t" + beverage + " остаток: " + beverage.availability);
        }
    }

    double sale(double count, double makeUp) {
        if (beverage.availability < count) {
            count = beverage.availability;
        }

        beverage.availability -= count;
        makeUp = 1 + makeUp * 0.01;

        saleCount += count;
        totalSaleSum += count * beverage.price * makeUp;
        return count;
    }

    public String getString() {
        String ut;

        if (beverage instanceof AlcoholBeverage) {
            AlcoholBeverage b = (AlcoholBeverage) (beverage);
            ut = String.valueOf(b.getStrength());
        } else {
            NonAlcoholBeverage b = (NonAlcoholBeverage) beverage;
            ut = b.getConsist();
        }
        String s = beverage.beverageName + "," +
                beverage.price + "," +
                beverage.beverageType + "," +
                beverage.volume + "," +
                ut + "," +
                beverage.availability + "\n"
                ;
        return s;
    }

    public String getStatistic() {
        return beverage + ":\n" +
                "\tЗакуплено: " + purchaseCount + "\n" +
                "\tСумма закупок: " + totalPurchaseSum + "\n" +
                "\tПродано: " + saleCount + "\n" +
                "\tСумма продаж: " + totalSaleSum + "\n" +
                "\tПрибыль: " + (totalSaleSum - totalPurchaseSum) + "\n" +
                "\tОстаток: " + beverage.availability + "\n"
                ;
    }
}

