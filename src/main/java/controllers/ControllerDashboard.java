package controllers;

import entity.Empresa;
import entity.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.event.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hibernate.Hibernate;
import utils.SessionManager;


import java.io.IOException;
import java.util.ResourceBundle;

public class ControllerDashboard {

    @FXML
    private AnchorPane Pane_mainScreen;
    @FXML
    Label lblCompanyName,lblCompanyDirection,lblUsername,lblUserrol;
    @FXML
    private ImageView notificationIcon;

    public void loadInitialView() {
        // Carga la vista de Dashboard.
        loadView("/uifxml/dashboardPanel.fxml");

        // Obtiene el usuario actual desde el SessionManager
        Usuario currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Se asume que la empresa del usuario ya está completamente cargada.
            lblUsername.setText(currentUser.getNombreUsuario());
            lblUserrol.setText(currentUser.getRol());

            // Si el usuario tiene una empresa asociada, también actualiza esa información
            if (currentUser.getEmpresa() != null) {
                lblCompanyName.setText(currentUser.getEmpresa().getNombre());
                lblCompanyDirection.setText(currentUser.getEmpresa().getDireccion());
            }
        } else {
            // Manejo de error o estado cuando no hay usuario en sesión
            lblUsername.setText("No hay usuario logueado");
            lblUserrol.setText("N/A");
            lblCompanyName.setText("N/A");
            lblCompanyDirection.setText("N/A");
        }
        updateNotificationIcon(false); // Supongamos que inicialmente no hay notificaciones
        notificationIcon.setOnMouseClicked(event -> handleNotificationClick());

    }


    // Métodos para cargar vistas dentro del AnchorPane
    public void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), ResourceBundle.getBundle("Internationalization.traduccion_es"));
            Node view = loader.load();
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            Pane_mainScreen.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error cargando la vista: " + fxmlPath);
        }
    }

    // Eventos de menú
    @FXML
    private void handleMenuUserProfile(ActionEvent event) {
        try{
            String fxmlPath = "/uifxml/UserManagment.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            if (loader.getLocation() == null) {
                throw new IOException("Cannot load resource: " + fxmlPath);
            }

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("User Profile");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the User Profile view: " + e.getMessage());
        }
    }

    @FXML
    private void handleMenuLanguageEnglish() {
        loadView("/uifxml/Dashboard.fxml");
    }

    @FXML
    private void handleMenuLanguageSpanish() {
        loadView("/uifxml/Dashboard.fxml");
    }

    @FXML
    private void handleMenuHelp() {
        loadView("/uifxml/Help.fxml");

    }

    @FXML
    private void handleMenuAbout() {
        try {
            String fxmlPath = "/uifxml/aboutFXML.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            if (loader.getLocation() == null) {
                throw new IOException("Cannot load resource: " + fxmlPath);
            }

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("User Profile");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Optional: Makes the window modal
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the User Profile view: " + e.getMessage());
        }
    }



    @FXML
    private void handleDashboardAction(ActionEvent event) {
        loadView("/uifxml/dashboardPanel.fxml");
    }

    @FXML
    private void handleItemsAction(ActionEvent event) {
        loadView("/uifxml/ItemsManagement.fxml");
    }

    @FXML
    private void handleCustomersAction(ActionEvent event) {
        loadView("/uifxml/CustomerManagment.fxml");
    }

    @FXML
    private void handleSupplierAction(ActionEvent event) {
        loadView("/uifxml/SupplierManagment.fxml");
    }

    @FXML
    private void handleTransactionAction(ActionEvent event) {
        loadView("/uifxml/TransactionManagment.fxml");
    }

    @FXML
    private void handleProductionAction(ActionEvent event) {
        loadView("/uifxml/ProductionManagment.fxml");
    }
    @FXML
    private void handleReportAction(ActionEvent event) {
        loadView("/uifxml/ReportesYFacturas.fxml");
    }
    private void updateUIWithUserDetails(Usuario usuario) {
        lblCompanyName.setText(usuario.getEmpresa().getNombre());
        lblCompanyDirection.setText(usuario.getEmpresa().getDireccion());
        lblUsername.setText(usuario.getNombreUsuario());
        lblUserrol.setText(usuario.getRol());
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleNotificationClick() {
        loadNotificationView();
        updateNotificationIcon(true);
    }

    private void loadNotificationView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uifxml/NotificationView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Notifications");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load notifications view.");
        }
    }

    private void updateNotificationIcon(boolean hasNotifications) {
        Image image = new Image(getClass().getResourceAsStream(hasNotifications ? "/images/icon_notificacionoff.png" : "/images/icon_notificacionON.png"));
        notificationIcon.setImage(image);
    }
}