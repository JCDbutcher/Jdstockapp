package mainapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;

import java.util.ResourceBundle;

public class MainApp extends Application {

    private static Stage primaryStage; // Hold a reference to the primary stage

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("/uifxml/pantallaCarga.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        Image image = new Image(getClass().getResourceAsStream("/images/iconoLeon.png"));
        primaryStage.getIcons().add(image);
        primaryStage.setTitle("JDstock : inventario ");
        primaryStage.show();
        primaryStage.setResizable(false);
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> switchToMainScreen());
        pause.play();
    }

    private void switchToMainScreen() {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("Internationalization.traduccion_es");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/uifxml/pantallaLogCreateUser.fxml"), resourceBundle);
            Parent loginRoot = fxmlLoader.load();
            Scene loginScene = new Scene(loginRoot);
            primaryStage.setScene(loginScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
