import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.StageHolder;

public class MainApp extends Application {

    public static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Platform.setImplicitExit(false);
        Parent root = FXMLLoader.load(getClass().getResource("/view/sample.fxml"));
        primaryStage.setTitle("AlcoMagaz 3000");
        primaryStage.setScene(new Scene(root));

        primaryStage.show();

        StageHolder.stage = primaryStage;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
