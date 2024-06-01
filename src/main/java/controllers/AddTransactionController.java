package controllers;

import entity.Cliente;
import entity.Proveedor;
import entity.Transaccion;
import entity.ShippingStatus;
import entity.Empresa;
import funcionalidadDAO.ClienteDAOImpl;
import funcionalidadDAO.ProveedorDAOImpl;
import funcionalidadDAO.TransaccionDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import utils.SessionManager;

public class AddTransactionController {

    @FXML private ComboBox<Object> inputCustomer; // Now uses Object to accommodate both Cliente and Proveedor
    @FXML private ComboBox<String> typeField;
    @FXML private DatePicker datePicker;
    @FXML private DatePicker orderDatePicker;
    @FXML private DatePicker deliveryDatePicker;
    @FXML private ComboBox<ShippingStatus> shippingStatusField;
    @FXML private ComboBox<Boolean> isPerfectField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private TransaccionDAOImpl transaccionDAO = new TransaccionDAOImpl();
    private ClienteDAOImpl clienteDAO = new ClienteDAOImpl();
    private ProveedorDAOImpl proveedorDAO = new ProveedorDAOImpl();
    private Empresa currentEmpresa;

    @FXML
    public void initialize() {
        currentEmpresa = SessionManager.getInstance().getCurrentUser().getEmpresa();
        typeField.setItems(FXCollections.observableArrayList("Venta", "Compra"));
        typeField.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateCustomerList(newVal));
        shippingStatusField.setItems(FXCollections.observableArrayList(ShippingStatus.values()));
        isPerfectField.setItems(FXCollections.observableArrayList(Boolean.TRUE, Boolean.FALSE));
        setupCustomerComboBox();
        updateCustomerList(typeField.getValue()); // Load initial customer or supplier list based on default type
    }

    private void updateCustomerList(String type) {
        ObservableList<Object> items = FXCollections.observableArrayList();
        if ("Compra".equals(type)) {
            items.addAll(proveedorDAO.obtenerTodosLosProveedores(currentEmpresa.getId()));
        } else if ("Venta".equals(type)) {
            items.addAll(clienteDAO.buscarTodosClientes(currentEmpresa.getId()));
        } else {
            items.addAll(clienteDAO.buscarTodosClientes(currentEmpresa.getId()));
            items.addAll(proveedorDAO.obtenerTodosLosProveedores(currentEmpresa.getId()));
        }
        inputCustomer.setItems(items);
    }

    private void setupCustomerComboBox() {
        inputCustomer.setCellFactory(lv -> new ListCell<Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (item instanceof Cliente) {
                    setText(empty ? null : ((Cliente) item).getNombre());
                } else if (item instanceof Proveedor) {
                    setText(empty ? null : ((Proveedor) item).getNombre());
                } else {
                    setText(null);
                }
            }
        });

        inputCustomer.setConverter(new StringConverter<Object>() {
            @Override
            public String toString(Object object) {
                if (object instanceof Cliente) {
                    return ((Cliente) object).getNombre();
                } else if (object instanceof Proveedor) {
                    return ((Proveedor) object).getNombre();
                }
                return null;
            }

            @Override
            public Object fromString(String string) {
                //no se usa esta parte del lambda
                return null;
            }
        });
    }

    @FXML
    private void handleSave() {
        try {
            Long empresaId = currentEmpresa.getId();
            Transaccion transaccion = new Transaccion();
            Object selectedCustomer = inputCustomer.getValue();

            if (selectedCustomer instanceof Cliente) {
                transaccion.setCliente((Cliente) selectedCustomer);
            } else {
                showAlert("Error", "Invalid customer or supplier selected.");
                return;
            }

            transaccion.setTipo(typeField.getValue());
            transaccion.setFechaPedido(orderDatePicker.getValue());
            transaccion.setFechaEntrega(deliveryDatePicker.getValue());
            transaccion.setEstadoEnvio(shippingStatusField.getValue());
            transaccion.setEsPerfecto(isPerfectField.getValue());
            transaccion.setEmpresa(currentEmpresa);

            if (transaccion.getId() == null) {
                transaccionDAO.guardar(transaccion, empresaId);
            } else {
                transaccionDAO.actualizar(transaccion, empresaId);
            }
            closeStage();
        } catch (Exception e) {
            showAlert("Error", "Cannot save transaction: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
