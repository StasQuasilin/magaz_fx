import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Controller;
import sample.StageHolder;

public class MainApp extends Application {


    Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
//        Platform.setImplicitExit(false);

        controller = new Controller();
        Scene scene = new Scene(controller, 924, 305);

        primaryStage.setTitle("АлкоМагаз 3000");

        primaryStage.setScene(scene);

        primaryStage.show();

        StageHolder.stage = primaryStage;
    }

    @Override
    public void stop() throws Exception {
        controller.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
