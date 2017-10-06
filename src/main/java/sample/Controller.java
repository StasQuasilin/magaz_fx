package sample;

import control.Shop;
import entity.BeverageStorage;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.File;

public class Controller {

    public Label info;
    public Button startButton;
    public Button stopButton;
    public Label scaleLabel;
    public TextField filePath;
    public Label emuDayLabel;
    public Label redLabel;
    public VBox statistic;
    public ScrollPane scrollPane;

    Parameters parameters = new Parameters();
    Shop shop;
    BeverageStorage storage = new BeverageStorage();

    Timer controllerTimer;
    Timer magazTimer;

    File file;

    //Запуск контроллера окна
    public Controller() {
        ActionListener al = e -> updateInfo();
        controllerTimer = new Timer(100, al);
        controllerTimer.start();
    }
    //Начало работы магазина
    public void start() {
        //Если файл указан вверно...
        if (checkFile()) {
            //Очищаем статистику
            statistic.getChildren().clear();
            //Инициализируем магазин
            shop = new Shop(parameters, storage);

            ActionListener al = e -> checkMagazStatus();
            magazTimer = new Timer(100, al);
            //Стартуем
            magazTimer.start();
        }
    }

    //Остановка магазина
    public void stop() {
        if(shop != null) {
            if (shop.isDoWork()) {
                shop.stop();
            }
        }
    }

    //Проверка статуса работы магазина
    private void checkMagazStatus() {
        if (shop.isDoWork()) {
            shop.run();
        } else {
            magazTimer.stop();
        }
    }

    //Проверка файла
    private boolean checkFile() {
        if (file == null) {
            file = new File(filePath.getText());
        }

        if (file.exists()) {
            storage.readFile(file);
            return true;
        }

        return false;
    }

    //Новая строка в консоли приложения
    void addNewsLine(String line) {
        statistic.getChildren().add(
                new Label(line)
        );

        scrollPane.setVvalue(100);
    }

    //Обновление выводимой информации
    void updateInfo() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                emuDayLabel.setText(String.valueOf(parameters.getEmuDays()));
                redLabel.setText(String.valueOf(parameters.getReportEveryDays()));
                scaleLabel.setText("x" + parameters.getScale());

                if (shop != null) {
                    info.setText(shop.getInfo());
                    if (shop.newsSize() > 0) {
                        for (String s : shop.getLatestsNews()) {
                            addNewsLine(s);
                        }

                    }

                    if (storage.newsSize() > 0) {
                        for (String s : storage.getNews()) {
                            addNewsLine(s);
                        }
                    }
                }
            }
        });
    }

    public void scaleMinus() {
        parameters.setScale(-1);
    }
    public void scalePlus() {
        parameters.setScale(1);
    }

    public void selectFile() {
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(extFilter);

        file = chooser.showOpenDialog(StageHolder.stage);
        if (file != null) {
            filePath.setText(file.getPath());
        }
    }

    public void emuDayLeft() {
        parameters.setEmuDays(-1);
    }
    public void emuDayRight() {
        parameters.setEmuDays(1);
    }

    public void redLeft() {
        parameters.setReportEveryDays(-1);
    }
    public void redRight() {
        parameters.setReportEveryDays(1);
    }
}
