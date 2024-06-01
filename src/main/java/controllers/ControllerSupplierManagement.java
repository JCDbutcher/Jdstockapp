package controllers;

import entity.Proveedor;
import funcionalidadDAO.ProveedorDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.SessionManager;

import java.util.List;

public class ControllerSupplierManagement {
    @FXML private TableView<Proveedor> suppliersTable;
    @FXML private TableColumn<Proveedor, Long> supplierIdColumn;
    @FXML private TableColumn<Proveedor, String> supplierNameColumn;
    @FXML private TableColumn<Proveedor, String> supplierAddressColumn;
    @FXML private TableColumn<Proveedor, String> supplierPhoneColumn;
    @FXML private TableColumn<Proveedor, String> supplierEmailColumn;
    @FXML private TableColumn<Proveedor, String> supplierContactColumn;
    @FXML private TableColumn<Proveedor, String> supplierDescriptionColumn;
    @FXML private TableColumn<Proveedor, String> supplierAccountColumn;
    @FXML private TableColumn<Proveedor, String> supplierNIFColumn;
    @FXML private TextField supplierNameField;
    @FXML private TextField supplierAddressField;
    @FXML private TextField supplierPhoneField;
    @FXML private TextField supplierEmailField;
    @FXML private TextField supplierContactField;
    @FXML private TextField supplierDescriptionField;
    @FXML private TextField supplierAccountField;
    @FXML private TextField nifField;
    @FXML private Button btnClear;

    private ProveedorDAOImpl proveedorDAO = new ProveedorDAOImpl();
    private Long empresaId;

    @FXML
    public void initialize() {
        empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
        setupTableColumns();
        loadSuppliers();
        setupSelectionModel();
    }

    private void setupTableColumns() {
        supplierIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        supplierNameColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        supplierAddressColumn.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        supplierPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        supplierEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        supplierContactColumn.setCellValueFactory(new PropertyValueFactory<>("contacto"));
        supplierDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        supplierAccountColumn.setCellValueFactory(new PropertyValueFactory<>("cuentaBancaria"));
        supplierNIFColumn.setCellValueFactory(new PropertyValueFactory<>("nif"));
    }

    private void loadSuppliers() {
        List<Proveedor> proveedores = proveedorDAO.obtenerTodosLosProveedores(empresaId);
        suppliersTable.setItems(FXCollections.observableArrayList(proveedores));
    }

    private void setupSelectionModel() {
        suppliersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillInputFields(newSelection);
            } else {
                clearInputFields();
            }
        });
    }

    private void fillInputFields(Proveedor proveedor) {
        supplierNameField.setText(proveedor.getNombre());
        supplierAddressField.setText(proveedor.getDireccion());
        supplierPhoneField.setText(proveedor.getTelefono());
        supplierEmailField.setText(proveedor.getEmail());
        supplierContactField.setText(proveedor.getContacto());
        supplierDescriptionField.setText(proveedor.getDescripcion());
        supplierAccountField.setText(proveedor.getCuentaBancaria());
        supplierNIFColumn.setText(proveedor.getNif());
    }

    @FXML
    private void handleAddUpdate(ActionEvent event) {
        Proveedor selected = suppliersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            selected = new Proveedor();
        }
        selected.setNombre(supplierNameField.getText());
        selected.setDireccion(supplierAddressField.getText());
        selected.setTelefono(supplierPhoneField.getText());
        selected.setEmail(supplierEmailField.getText());
        selected.setContacto(supplierContactField.getText());
        selected.setDescripcion(supplierDescriptionField.getText());
        selected.setCuentaBancaria(supplierAccountField.getText());
        selected.setNif(nifField.getText());

        if (selected.getId() == null) {
            proveedorDAO.guardar(selected, empresaId);
        } else {
            proveedorDAO.actualizar(selected, empresaId);
        }
        loadSuppliers();
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Proveedor selected = suppliersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            proveedorDAO.eliminar(selected.getId(), empresaId);
            loadSuppliers();
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        suppliersTable.getSelectionModel().clearSelection();
        clearInputFields();
    }

    private void clearInputFields() {
        supplierNameField.clear();
        supplierAddressField.clear();
        supplierPhoneField.clear();
        supplierEmailField.clear();
        supplierContactField.clear();
        supplierDescriptionField.clear();
        supplierAccountField.clear();
        nifField.clear();
    }







}

