package control;

import entity.BeverageStorage;
import entity.BuyInfo;
import entity.Client;
import org.apache.log4j.Logger;
import sample.Parameters;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by ZPT_USER on 05.10.2017.
 */
public class Shop implements Runnable{

    private static final Logger log = Logger.getLogger(Shop.class);
    private BeverageStorage storage;

    LocalDateTime time;
    LocalDateTime nextEventTime;
    LocalDateTime startTime;
    LocalDateTime nextStatisticTime;
    LocalDateTime stopTime;

    private Parameters parameters;
    private boolean doWork = false;

    long lastNanos = System.nanoTime();
    long timeSteep = 0;
    boolean isWorkTime = false;
    Random r = new Random();

    public Shop(Parameters parameters, BeverageStorage storage) {
        this.parameters = parameters;
        this.storage = storage;

        startTime = time = LocalDateTime.now();
        nextStatisticTime = startTime.plusDays(parameters.getReportEveryDays());
        stopTime = startTime.plusDays(parameters.getEmuDays());

        doWork = true;
        updateNextEventTime();
    }

    public void run() {

        updateTimer();

        if (time.isAfter(stopTime)) {
            stop();
        } else {

            if (time.isAfter(nextStatisticTime)) {
                saveLog();
            }

            if (!isWorkTime && parameters.isWorkTime(time)) {
                addNews("--- Магаз открыт");
                updateNextEventTime();
            } else if (isWorkTime && !parameters.isWorkTime(time)) {
                addNews("--- Магаз закрыт");
                storage.purchase();
            }

            isWorkTime = parameters.isWorkTime(time);

            if (isWorkTime) {

                if (isEventTime()) {
                    incomeClient();
                    updateNextEventTime();
                }
            }
        }
    }

    public void stop() {
        addNews("Магаз прекращает работу");

        saveLog();
        storage.save();

        doWork = false;
    }

    private void saveLog() {

        infos.addAll(storage.getInfos());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                            LocalDate.from(nextStatisticTime) +
                            " log.txt"));

            for (String s : infos) {
                writer.write(s);
                writer.newLine();
            }

            infos.clear();

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        nextStatisticTime = time.plusDays(parameters.getReportEveryDays());
    }

    void addNews(String value) {
        log.info(value);
        news.add(value);
    }

    List<String> infos = new LinkedList<>();
    private void incomeClient() {

        for (int i = 0; i < r.nextInt(10); i++) {

            Client client = new Client();
            addNews(time + ": " + client + " зашел в магаз");

            int markUp = parameters.getMarkUp(time);
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

    void updateTimer() {
        timeSteep = (System.nanoTime() - lastNanos) * parameters.getScale();
        lastNanos = System.nanoTime();
        time = time.plusNanos(timeSteep);
    }

    void updateNextEventTime() {

        nextEventTime = time.plusSeconds(r.nextInt(30));
        nextEventTime = nextEventTime.plusMinutes(r.nextInt(30));
    }

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
