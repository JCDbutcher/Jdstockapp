package controllers;

import entity.Cliente;
import entity.Transaccion;
import entity.ShippingStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import funcionalidadDAO.TransaccionDAOImpl;
import javafx.util.StringConverter;
import utils.SessionManager;

public class EditTransactionController {
    @FXML private TextField transactionIdField;
    @FXML private ComboBox<Cliente> customerComboBox;
    @FXML private ComboBox<String> typeField;
    @FXML private DatePicker orderDatePicker;
    @FXML private DatePicker deliveryDatePicker;
    @FXML private ComboBox<ShippingStatus> shippingStatusField;
    @FXML private ComboBox<Boolean> isPerfectField;

    private TransaccionDAOImpl transaccionDAO = new TransaccionDAOImpl();
    private TransactionManagementController transactionManagementController;

    @FXML
    private void initialize() {
        setupComboBoxConverters();
        populateShippingStatusField();
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        try {
            Long transactionId = Long.parseLong(transactionIdField.getText());
            Transaccion transaccion = transaccionDAO.encontrar(transactionId, SessionManager.getInstance().getCurrentUser().getEmpresa().getId());
            if (transaccion != null) {
                transaccion.setCliente(customerComboBox.getValue());
                transaccion.setTipo(typeField.getValue());
                transaccion.setFechaPedido(orderDatePicker.getValue());
                transaccion.setFechaEntrega(deliveryDatePicker.getValue());
                transaccion.setEstadoEnvio(shippingStatusField.getValue()); // Directly using ShippingStatus
                transaccion.setEsPerfecto(isPerfectField.getValue());

                transaccionDAO.actualizar(transaccion, transaccion.getEmpresa().getId());
                showAlert("Update Successful", "The transaction has been updated successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Update Failed", "No transaction found with the provided ID.", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number for transaction ID.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "An error occurred while updating the transaction: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        closeStage();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) transactionIdField.getScene().getWindow();
        stage.close();
        transactionManagementController.refreshTable();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setTransaction(Transaccion selectedTransaction) {
        if (selectedTransaction != null) {
            transactionIdField.setText(selectedTransaction.getId().toString());
            customerComboBox.setValue(selectedTransaction.getCliente());
            typeField.setValue(selectedTransaction.getTipo());
            orderDatePicker.setValue(selectedTransaction.getFechaPedido());
            deliveryDatePicker.setValue(selectedTransaction.getFechaEntrega());
            shippingStatusField.setValue(selectedTransaction.getEstadoEnvio()); // Directly using ShippingStatus
            isPerfectField.setValue(selectedTransaction.getEsPerfecto());
        }
    }

    public void setTransactionManagementController(TransactionManagementController transactionManagementController) {
        this.transactionManagementController = transactionManagementController;
    }

    private void setupComboBoxConverters() {
        customerComboBox.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente cliente) {
                return cliente != null ? cliente.getNombre() : "";
            }

            @Override
            public Cliente fromString(String string) {
                return customerComboBox.getItems().stream()
                        .filter(cliente -> cliente.getNombre().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        shippingStatusField.setConverter(new StringConverter<ShippingStatus>() {
            @Override
            public String toString(ShippingStatus status) {
                return status != null ? status.getLabel() : "";
            }

            @Override
            public ShippingStatus fromString(String string) {
                return ShippingStatus.fromString(string);
            }
        });
    }

    private void populateShippingStatusField() {
        shippingStatusField.getItems().setAll(ShippingStatus.values());
    }
}

