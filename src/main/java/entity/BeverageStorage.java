package entity;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZPT_USER on 05.10.2017.
 */
public class BeverageStorage {
    private static final Logger log = Logger.getLogger(BeverageStorage.class);
    List<BeverageValue> rests;

    File file;
    public void readFile(File file) {
        this.file = file;

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), "utf-8"));

            rests = new LinkedList<>();
            String line;
            String separator = ",";
            while ((line = reader.readLine()) != null) {
                System.out.println(line);

                List<String> words = new ArrayList<>();
                char[] chars = line.toCharArray();

                String result = "";
                boolean block = false;

                int i = 0;
                for (char ch : chars) {

                    if (ch == '\"') {
                        block = !block;
                    }

                    if (!block) {
                        if (ch == ',') {
                            System.out.println(words.get(i));
                            i++;
                            words.add(i, "");
                        }
                    } else {
                        if (!block & ch != ' ') {
                            String s = words.get(i);
                            words.add(i,  s);
                        }

                    }

                }

                IBeverage beverage;

                try {
                    String title = words.get(0);
                    double price = Double.parseDouble(words.get(1));
                    String type = words.get(2);
                    double volume = Double.parseDouble(words.get(3));
                    double availability = Double.parseDouble(words.get(5));

                    try {
                        double strength = Double.parseDouble(words.get(4));
                        beverage = new AlcoholBeverage(title, price, type, volume, strength, availability);
                    } catch (NumberFormatException ignore) {
                        String consist = words.get(4);
                        beverage = new NonAlcoholBeverage(title, price, type, volume, consist, availability);
                    }

                    addNews("Добавлено " + beverage.availability + " " + beverage + " на склад");
                    rests.add(new BeverageValue(beverage, this));

                } catch (Exception e) {
                    e.printStackTrace();
                    addNews("Неправильная информация " + line);
                }
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void purchase() {
        addNews("����� ���");

        rests.forEach(entity.BeverageValue::checkCount);
    }

    public List<IBeverage> tryBuy(int count, int makeUp) {
        List<IBeverage> productList = new LinkedList<>();

        int i = 0;
        for (BeverageValue bv : rests) {
            if (i < count) {
                if (bv.beverage.availability > 0) {

                    if (productList.size() > 1) {
                        bv.sale(1, 7);
                    } else {
                        bv.sale(1, makeUp);
                    }

                    productList.add(bv.beverage);

                    i++;
                }
            } else {
                break;
            }
        }

        return productList;
    }

    List<String> news = new LinkedList<>();
    List<String> infos = new LinkedList<>();
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
            BufferedWriter writer1 = new BufferedWriter(
                    new FileWriter(
                            file
                    )
            );

            BufferedWriter writer2 = new BufferedWriter(
                    new FileWriter(
                            "statistic.txt"
                    )
            );

            for (BeverageValue bv : rests) {
                writer1.write(bv.getString());
                writer2.write(bv.getStatistic());
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

            storage.addNews("��������� " + pCount + " �������� " + beverage);
        } else {
            storage.addNews(beverage + " �������: " + beverage.availability);
        }
    }

    void sale(double count, double makeUp) {
        beverage.availability -= count;
        makeUp = 1 + makeUp * 0.01;

        saleCount += count;
        totalSaleSum += count * beverage.price * makeUp;
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
        String s = beverage.beverageName + ", " +
                beverage.price + ", " +
                beverage.beverageType + ", " +
                beverage.volume + ", " +
                "\"" + ut + "\", " +
                beverage.availability + "\n"
                ;
        return s;
    }

    public String getStatistic() {
        return beverage + ":\n" +
                "\t�������: " + purchaseCount + "\n" +
                "\t����� ������: " + totalPurchaseSum + "\n" +
                "\t���������: " + saleCount + "\n" +
                "\t����� �������: " + totalSaleSum + "\n" +
                "\t�������: " + (totalSaleSum - totalPurchaseSum) + "\n"
                ;
    }
}

