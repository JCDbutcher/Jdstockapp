package controllers;

import entity.Cliente;
import funcionalidadDAO.ClienteDAOImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.SessionManager;

public class NewCustomerController {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField nifFIeld;

    private ClienteDAOImpl clienteDAO = new ClienteDAOImpl();
    private ControllerCustomerManagement mainController;

    // Método para establecer el controlador principal
    public void setMainController(ControllerCustomerManagement mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleSaveAction(ActionEvent event) {
        if (validateInput()) {
            try {
                Cliente newClient = new Cliente();
                newClient.setNombre(nameField.getText().trim());
                newClient.setDireccion(addressField.getText().trim());
                newClient.setTelefono(phoneField.getText().trim());
                newClient.setEmail(emailField.getText().trim());
                newClient.setNif(nifFIeld.getText().trim());
                // Obteniendo el ID de empresa desde la sesión del usuario
                Long empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();

                clienteDAO.guardarActualizarCliente(newClient, empresaId);

                // Notifica al controlador principal para actualizar la tabla
                if (mainController != null) {
                    mainController.loadClientes();
                }

                closeStage();  // Cierra la ventana
            } catch (Exception e) {
                showAlert("Error", "Error saving new customer: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty() || addressField.getText().isEmpty() ||
                phoneField.getText().isEmpty() || emailField.getText().isEmpty()) {
            showAlert("Validation Error", "All fields must be filled", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}