package control;

import entity.BeverageStorage;
import entity.BuyInfo;
import entity.Client;
import org.apache.log4j.Logger;
import sample.Parameters;
import utils.FileSaver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by ZPT_USER on 05.10.2017.
 */
//Основной класс, эмулирует работу магазина
public class Shop implements Runnable{

    private static final Logger log = Logger.getLogger(Shop.class);

    private Parameters parameters;
    private BeverageStorage storage;

    //Текущее время в магазине
    LocalDateTime time;
    //Время следующего события
    LocalDateTime nextEventTime;
    //Время начала работы
    LocalDateTime startTime;
    //Время следующего вывода лога
    LocalDateTime nextLogTime;
    //Время остановки
    LocalDateTime stopTime;

    private boolean doWork = false;
    boolean isWorkTime = false;

    //Время последнего цыкла
    long lastNanos = System.nanoTime();
    //Время цыкла
    long timeSteep = 0;
    Random r = new Random();

    public Shop(Parameters parameters, BeverageStorage storage) {
        this.parameters = parameters;
        this.storage = storage;

        startTime = time = LocalDateTime.now();
        nextLogTime = startTime.plusDays(parameters.getReportEveryDays());
        stopTime = startTime.plusDays(parameters.getEmuDays());

        doWork = true;
        updateNextEventTime();
    }

    //Цыкл работы
    public void run() {

        updateTimer();

        //Не пора ли остановиться?
        if (time.isAfter(stopTime)) {
            stop();
        } else {

            //Не пора ли выводить лог
            if (time.isAfter(nextLogTime)) {
                saveLog();
            }

            //Если утренне открытие - одновить время следующего события
            if (!isWorkTime && parameters.isWorkTime(time)) {
                addNews(time + " --------- Магазин открыт");
                updateNextEventTime();
                //иначе проверить остатки товара после закрытия
            } else if (isWorkTime && !parameters.isWorkTime(time)) {
                addNews(time + " --------- Магазин закрыт");
                storage.purchase();
            }

            isWorkTime = parameters.isWorkTime(time);

            //Если текущее время с 8 до 22...
            if (isWorkTime) {
                //и если прошло время события...
                if (isEventTime()) {
                    //приходит новый клиент
                    incomeClient();
                    //обновляется время следующего события
                    updateNextEventTime();
                }
            }
        }
    }

    //Остановка магазина
    public void stop() {
        addNews(time + " --------- Магазин прекращает работу");
        //сохраняем логи
        saveLog();
        //сохраняем статистику покупок и остатки продукции
        storage.save();

        doWork = false;
    }

    //Сохранение логов работы
    private void saveLog() {

        infos.addAll(storage.getInfos());
        try {
            FileSaver saver = new FileSaver(new File(LocalDate.from(nextLogTime) + " log.txt"));

            for (String s : infos) {
                saver.write(s);
            }

            infos.clear();

            saver.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        nextLogTime = time.plusDays(parameters.getReportEveryDays());
    }

    //Вывод информации в програмную консоль и в логи программы
    void addNews(String value) {
        log.info(value);
        news.add(value);
    }

    List<String> infos = new LinkedList<>();

    //Новые клиенты...
    private void incomeClient() {

        //...от 1 до 10
        for (int i = 0; i < r.nextInt(10); i++) {

            Client client = new Client();
            addNews(time + ": " + client + " зашел в магазин");

            //Наценка в зависимости от времени
            int markUp = parameters.getMarkUp(time);

            //Покупка от 0 до 10 товаров
            for (BuyInfo bi : storage.tryBuy(r.nextInt(10), markUp)) {

                addNews("\t" + client +
                        " купил " + bi.getBeverage() +
                        ", " + bi.getCount() + " шт." +
                        ", наценка - " + bi.getMarkup() + "%");
            }
        }

    }

    private boolean isEventTime() {
        return time.isAfter(nextEventTime);
    }

    //Обновление местного времени
    void updateTimer() {
        timeSteep = (System.nanoTime() - lastNanos) * parameters.getScale();
        lastNanos = System.nanoTime();
        time = time.plusNanos(timeSteep);
    }

    //Обновление таймера следующего осбытия
    void updateNextEventTime() {
        //...текущее время + 0...30 секунд
        nextEventTime = time.plusSeconds(r.nextInt(30));
        //новое время + 0...30 минут
        nextEventTime = nextEventTime.plusMinutes(r.nextInt(30));
    }

    //Информация о времени работы
    public String getInfo() {
        if (doWork) {
            String info = String.format("Начало работи: %1$td-%1$tm-%1$ty, %1$tH:%1$tM:%1$tS", startTime) + "\n";
            info += String.format("Завершение работы: %1$td-%1$tm-%1$ty, %1$tH:%1$tM:%1$tS", stopTime) + "\n";
            info += String.format("Местное время: %1$td %1$tB %1$tY, %1$tH:%1$tM:%1$tS", time) + "\n";

            if (parameters.isWorkTime(time)) {
                info += String.format("Следующее событие в %1$tH:%1$tM:%1$tS", nextEventTime) + "\n";
            } else {
                info += "Магазин закрыт";
            }

            return info;
        } else {
            return "Магазин остановил работу";
        }
    }

    public boolean isDoWork() {
        return doWork;
    }

    public List<String> getLatestsNews() {
        List<String> temp = news;
        news = new LinkedList<>();
        infos.addAll(temp);
        return temp;
    }

    List<String> news = new LinkedList<>();
    public int newsSize() {
        return news.size();
    }
}
