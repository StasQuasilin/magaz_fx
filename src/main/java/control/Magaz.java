package control;

import entity.BeverageStorage;
import entity.Client;
import entity.IBeverage;
import org.apache.log4j.Logger;
import sample.Parameters;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by ZPT_USER on 05.10.2017.
 */
public class Magaz implements Runnable{

    private static final Logger log = Logger.getLogger(Magaz.class);
    private BeverageStorage storage;

    LocalDateTime time;
    LocalDateTime startTime;
    LocalDateTime nextStatisticTime;
    LocalDateTime stopTime;

    private Parameters parameters;
    private boolean doWork = false;

    public Magaz(Parameters parameters, BeverageStorage storage) {
        this.parameters = parameters;
        this.storage = storage;
    }

    long lastNanos = System.nanoTime();
    long timeSteep = 0;
    boolean isWorkTime = false;
    @Override

    public void run() {
        startTime = time = LocalDateTime.now();
        nextStatisticTime = startTime.plusDays(parameters.getReportEveryDays());
        stopTime = startTime.plusDays(parameters.getEmuDays());

        doWork = true;
        updateNextEventTime();

        while (doWork) {
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
    }

    public void stop() {
        addNews("Магаз прекращает работу");
        doWork = false;
        if (!nextStatisticTime.equals(stopTime)) {
            saveLog();
        }
        storage.save();
    }

    private void saveLog() {
        nextStatisticTime = time.plusDays(parameters.getReportEveryDays());

        infos.addAll(storage.getInfos());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                    startTime + "-" +
                            time +
                            " statistic.txt"));

            for (String s : infos) {
                writer.write(s);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            for (IBeverage b :  storage.tryBuy(r.nextInt(10), markUp)) {
                addNews("\t" + client + " купил " + b + ", наценка - " + markUp + "%");
            }
        }

    }

    private boolean isEventTime() {
        return time.getHour() >= neHour && time.getMinute() >= neMinutes && time.getSecond() >= neSeconds;
    }

    void updateTimer() {
        timeSteep = (System.nanoTime() - lastNanos) * parameters.getScale();
        lastNanos = System.nanoTime();
        time = time.plusNanos(timeSteep);
    }
    int neHour;
    int neMinutes;
    int neSeconds;
    Random r = new Random();

    void updateNextEventTime() {
        neHour = time.getHour();
        neMinutes = time.getMinute() + r.nextInt(30);
        neSeconds = time.getSecond() + r.nextInt(30);

        if (neSeconds > 59) {
            neMinutes ++;
            neSeconds -= 59;
        }

        if (neMinutes > 59) {
            neHour++;
            neMinutes -= 59;
        }
    }

    public String getInfo() {
        String info = "Время в магазе: " + time + "\n";

        if (parameters.isWorkTime(time)) {
            info += "Следующее событие в: " + neHour + ":" + neMinutes + ":" + neSeconds + "\n";
        } else {
            info += "Магазин закрыт";
        }

        return info;
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
