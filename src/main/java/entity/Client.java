package entity;

import java.util.Random;

/**
 * Created by ZPT_USER on 05.10.2017.
 */
public class Client {
    String firstName;
    String secondName;
    private static final Random r = new Random();

    public Client() {
        firstName = fNames[r.nextInt(fNames.length)];
        secondName = lNames[r.nextInt(lNames.length)];
    }

    @Override
    public String toString() {
        return firstName + " " + secondName;
    }

    String[] fNames = {
            "Сергей",
            "Андрей",
            "Василий",
            "Пётер",
            "Виталий",
            "Инокентий",
            "Хороший",
            "Железный",
            "Человек -",
            "Иосиф",
            "Стас"
    };
    String[] lNames = {
            "Григоровський",
            "Кашпировский",
            "Бампер",
            "Великий",
            "Калиниченко",
            "Человек",
            "Паук",
            "Бетмен",
            "Иларионович",
            "Кобзон",
            "Михайлов",
            "Кобзарь"
    };
}
