package utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import mainapp.MainApp;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageUtils {
    private static final String BASE_NAME = "Internationalization.traduccion_en";
    private static Locale locale = Locale.ENGLISH;
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BASE_NAME, locale);


    public static void setLocale(Locale newLocale) {
        locale = newLocale;
        resourceBundle = ResourceBundle.getBundle(BASE_NAME, locale);
        reloadViews();
    }

    public static void reloadViews() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/uifxml/pantallaLogCreateUser.fxml"), resourceBundle);
                Parent root = loader.load();
                MainApp.getPrimaryStage().getScene().setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
}

