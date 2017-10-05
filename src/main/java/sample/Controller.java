package sample;

import control.Magaz;
import entity.BeverageStorage;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    Parameters parameters = new Parameters();

    Magaz magaz;
    BeverageStorage storage = new BeverageStorage();

    public Controller() {
        ActionListener al = e -> update();
        new Timer(100, al).start();
    }

    public void start() {
        if (checkFile()) {
            magaz = new Magaz(parameters, storage);
            thread = new Thread(magaz);
            thread.start();
        }
    }

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

    Thread thread;
    public void stop() {
        if(magaz != null) {
            if (magaz.isDoWork()) {
                magaz.stop();
            }
        }
    }

    void update() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                emuDayLabel.setText(String.valueOf(parameters.getEmuDays()));
                redLabel.setText(String.valueOf(parameters.getReportEveryDays()));
                scaleLabel.setText("x" + parameters.getScale());

                if (magaz != null) {
                    if (magaz.isDoWork()) {
                        info.setText(magaz.getInfo());
                        if (magaz.newsSize() > 0) {
                            for (String s : magaz.getLatestsNews()) {
                                statistic.getChildren().add(
                                        new Label(s)
                                );
                            }

                        }

                        if (storage.newsSize() > 0) {
                            for (String s : storage.getNews()) {
                                statistic.getChildren().add(
                                        new Label(s)
                                );
                            }
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

    File file;
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
