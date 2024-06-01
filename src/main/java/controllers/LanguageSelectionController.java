package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import utils.LanguageUtils;

import java.util.Locale;

public class LanguageSelectionController {

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    public void initialize() {
        // Inicializa el ComboBox con el idioma actual
        languageComboBox.setValue(Locale.getDefault().getDisplayLanguage());
        setupComboBoxListener();
    }

    private void setupComboBoxListener() {
        // Escucha cambios en la selección del ComboBox
        languageComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.equals("English")) {
                LanguageUtils.setLocale(Locale.ENGLISH);
            } else if (newVal.equals("Español")) {
                LanguageUtils.setLocale(new Locale("es", "ES"));
            }
        });
    }

    @FXML
    private void handleLanguageChange() {
        System.out.println("apretaste el boton de idioma");
        // Aplica el cambio de idioma sin necesidad de reiniciar la aplicación
        LanguageUtils.reloadViews();
    }
}
