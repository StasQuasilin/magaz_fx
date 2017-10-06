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
    Random r = new Random();

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

    //Проверка остатков товара
    public void purchase() {
        addNews("Остаток товара");
        rests.forEach(entity.BeverageValue::checkCount);
    }

    public List<BuyInfo> tryBuy(int count, int makeUp) {
        List<BuyInfo> buyInfos = new ArrayList<>();

        //Список товаров, остаток которых >0
        List<BeverageValue> temp = new LinkedList<>();
        for (BeverageValue bv : rests) {
            if (bv.beverage.availability > 0) {
                temp.add(bv);
            }
        }

        //Количество товара в корзине
        int buyCount = 0;

        //Если товар есть на складе...
        if (temp.size() > 0) {
            while (buyCount < count) {
                //Выбираем товар от 0 до количества товара
                BeverageValue value = temp.get(r.nextInt(temp.size()));

                if (value.beverage.availability > 0) {
                    int c = r.nextInt(10);
                    if (c > 0) {
                        //Пытаемся покупать
                        buyCount += buy(c, buyInfos, makeUp, value);
                    }
                }

            }
        }

        return buyInfos;
    }

    //Покупка
    private static double buy(int count, List<BuyInfo> productBox, int makeUp, BeverageValue bv) {
        //Если товара в корзине 2, станавливаем праздничную скидку
        if (productBox.size() > 1)
            makeUp = 7;

        //Количество купленной продукции
        double c = bv.sale(count, makeUp);

        //Добавляем информацию о покупке
        productBox.add(new BuyInfo(bv.beverage, c, makeUp));
        return c;
    }

    //Количество новых записей консоли
    public int newsSize() {
        return news.size();
    }

    //Возвращает записи лога
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

            //Сохранение информации про остатки продукции
            BufferedWriter writer1 = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file), StandardCharsets.UTF_8
                    )
            );

            //Сохранение статистики
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
    //Продукт
    IBeverage beverage;
    BeverageStorage storage;
    //Количество закупленого
    double purchaseCount = 0;
    //Сумма закупки
    double totalPurchaseSum = 0;

    //Количество проданого
    double saleCount = 0;
    //Сумма продаж
    double totalSaleSum = 0;

    //Стандартноя количество закупки
    int pCount = 150;

    public BeverageValue(IBeverage beverage, BeverageStorage beverageStorage) {
        this.beverage = beverage;
        this.storage = beverageStorage;
    }

    void checkCount() {
        //Если продукта на складе <=10...
        if (beverage.availability < 11) {
            //...Закупаем
            beverage.availability += pCount;

            totalPurchaseSum += pCount * beverage.price;
            purchaseCount += pCount;
            //...И выводим информацию
            storage.addNews("\tЗакуплено " + pCount + " продукта " + beverage);
        } else {
            //...Выводим остаток
            storage.addNews("\t" + beverage + " остаток: " + beverage.availability);
        }
    }

    //Продажа
    double sale(double count, double makeUp) {
        //Если остаток меньше требуемого количества...
        if (beverage.availability < count) {
            //Требуемое количество равно остатку
            count = beverage.availability;
        }
        //Отнимаем требуемое количество
        beverage.availability -= count;
        //Переводим наценку
        makeUp = 1 + makeUp * 0.01;

        //Добавляем количество проданого
        saleCount += count;
        //Добавляем сумму продажи
        totalSaleSum += count * beverage.price * makeUp;
        //Возвращаем количество закупленного товара
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

