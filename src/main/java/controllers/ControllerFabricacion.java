package controllers;

import entity.Fabricacion;
import entity.Producto;
import entity.DetallesFabricacion;
import entity.DetallesFabricacionId;
import funcionalidadDAO.FabricacionDAOImpl;
import funcionalidadDAO.DetallesFabricacionDAOImpl;
import funcionalidadDAO.ProductoDAOImpl;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;
import utils.SessionManager;

import java.time.LocalDate;
import java.util.List;

public class ControllerFabricacion {


    @FXML
    private Spinner<Integer> fabricationQuantitySpinner;
    @FXML
    private DatePicker dateofFabrication;
    @FXML
    private TableView<Fabricacion> fabricationTable;
    @FXML
    private TableColumn<Fabricacion, Long> fabricationIdColumn;
    @FXML
    private TableColumn<Fabricacion, String> fabricationProductColumn;
    @FXML
    private TableColumn<Fabricacion, Integer> fabricationQuantityColumn;
    @FXML
    private TableColumn<Fabricacion, LocalDate> fabricationDateColumn;

    @FXML
    private TableView<DetallesFabricacion> componentsTable;
    @FXML
    private TableColumn<DetallesFabricacion, Long> componentIdColumn;
    @FXML
    private TableColumn<DetallesFabricacion, String> componentNameColumn;
    @FXML
    private TableColumn<DetallesFabricacion, Integer> usedQuantityColumn;
    @FXML
    private ComboBox<Producto> componentNameComboBox, ProductoFabricationCombobox;
    @FXML
    private Spinner<Integer> usedQuantitySpinner;

    private FabricacionDAOImpl fabricacionDAO = new FabricacionDAOImpl();
    private DetallesFabricacionDAOImpl detallesFabricacionDAO = new DetallesFabricacionDAOImpl();
    private ProductoDAOImpl productoDAO = new ProductoDAOImpl();

    @FXML
    public void initialize() {
        setupFabricationTable();
        setupComponentsTable();
        setupSpinners();
        setupComboBox();
        loadFabricationData();
        loadComponentData();
        setupComboBoxProduction();
    }

    private void setupFabricationTable() {
        fabricationIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fabricationProductColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getProducto().getNombre()));
        fabricationQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("cantidadProducida"));
        fabricationDateColumn.setCellValueFactory(new PropertyValueFactory<>("fechaFabricacion"));

        fabricationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setFabricationFields(newSelection);
                loadComponentData(newSelection.getId());
            } else {
                componentsTable.getItems().clear();
            }
        });
    }

    private void setupComponentsTable() {
        componentIdColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId().getComponenteId()));
        componentNameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getComponente().getNombre()));
        usedQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("cantidadUtilizada"));

        componentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setComponentFields(newSelection);
            }
        });
    }

    private void setupSpinners() {
        fabricationQuantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
        usedQuantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
    }

    private void setupComboBox() {
        componentNameComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Producto producto) {
                return producto != null ? producto.getNombre() : "";
            }

            @Override
            public Producto fromString(String string) {
                return null;
            }
        });
    }

    private void setupComboBoxProduction() {
        ProductoFabricationCombobox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Producto producto) {
                return producto != null ? producto.getNombre() : "";
            }

            @Override
            public Producto fromString(String string) {
                return null;
            }
        });
    }

    private void loadFabricationData() {
        Long empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
        List<Fabricacion> fabricaciones = fabricacionDAO.obtenerTodos(empresaId);
        ObservableList<Fabricacion> fabricacionObservableList = FXCollections.observableArrayList(fabricaciones);
        fabricationTable.setItems(fabricacionObservableList);
    }

    private void loadComponentData() {
        Long empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
        List<Producto> productos = productoDAO.obtenerTodos(empresaId);
        ObservableList<Producto> productosObservableList = FXCollections.observableArrayList(productos);
        componentNameComboBox.setItems(productosObservableList);
        ProductoFabricationCombobox.setItems(productosObservableList);
    }

    private void loadComponentData(Long fabricacionId) {
        Long empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
        List<DetallesFabricacion> detalles = detallesFabricacionDAO.buscarPorFabricacion(fabricacionId, empresaId);
        ObservableList<DetallesFabricacion> detallesObservableList = FXCollections.observableArrayList(detalles);
        componentsTable.setItems(detallesObservableList);
    }

    private void setFabricationFields(Fabricacion fabricacion) {
        ProductoFabricationCombobox.setValue(fabricacion.getProducto());
        fabricationQuantitySpinner.getValueFactory().setValue(fabricacion.getCantidadProducida());
        dateofFabrication.setValue(fabricacion.getFechaFabricacion());
        componentNameComboBox.setValue(fabricacion.getProducto());
    }

    private void setComponentFields(DetallesFabricacion detallesFabricacion) {
        componentNameComboBox.setValue(detallesFabricacion.getComponente());
        usedQuantitySpinner.getValueFactory().setValue(detallesFabricacion.getCantidadUtilizada());
    }

    @FXML
    private void handleSaveFabrication() {
        try {
            Long empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
            Producto productoSeleccionado = ProductoFabricationCombobox.getValue();

            if (productoSeleccionado == null) {
                showAlert("Validation Error", "No product selected. Please select a product before saving.", Alert.AlertType.WARNING);
                return;
            }

            Integer cantidadProducida = fabricationQuantitySpinner.getValue();
            if (cantidadProducida == null || cantidadProducida <= 0) {
                showAlert("Validation Error", "Invalid quantity. Please enter a valid quantity greater than zero.", Alert.AlertType.WARNING);
                return;
            }

            LocalDate fechaFabricacion = dateofFabrication.getValue();
            if (fechaFabricacion == null) {
                showAlert("Validation Error", "No fabrication date selected. Please select a date.", Alert.AlertType.WARNING);
                return;
            }

            // Actualizar el stock del producto
            int nuevoStock = productoSeleccionado.getCantidadEnStock() + cantidadProducida;
            productoSeleccionado.setCantidadEnStock(nuevoStock);
            productoDAO.actualizar(productoSeleccionado, empresaId);

            // Guardar la nueva fabricación
            Fabricacion fabricacion = new Fabricacion();
            fabricacion.setProducto(productoSeleccionado);
            fabricacion.setCantidadProducida(cantidadProducida);
            fabricacion.setFechaFabricacion(fechaFabricacion);
            fabricacionDAO.guardar(fabricacion, empresaId);

            showAlert("Save Successful", "The fabrication has been saved successfully, and product stock updated.", Alert.AlertType.INFORMATION);
            loadFabricationData();
        } catch (Exception e) {
            showAlert("Error", "An error occurred while saving the fabrication: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }



    @FXML
    private void handleUpdateFabrication() {
        Fabricacion selectedFabrication = fabricationTable.getSelectionModel().getSelectedItem();
        if (selectedFabrication != null) {
            try {
                Long empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
                selectedFabrication.setProducto(componentNameComboBox.getValue());
                selectedFabrication.setCantidadProducida(fabricationQuantitySpinner.getValue());
                selectedFabrication.setFechaFabricacion(dateofFabrication.getValue());
                fabricacionDAO.actualizar(selectedFabrication, empresaId);
                showAlert("Update Successful", "The fabrication has been updated successfully.", Alert.AlertType.INFORMATION);
                loadFabricationData();
            } catch (Exception e) {
                showAlert("Error", "An error occurred while updating the fabrication: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Selection", "Please select a fabrication to update.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleDeleteFabrication() {
        Fabricacion selectedFabrication = fabricationTable.getSelectionModel().getSelectedItem();
        if (selectedFabrication != null) {
            try {
                Long empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
                fabricacionDAO.eliminar(selectedFabrication.getId(), empresaId);
                showAlert("Delete Successful", "The fabrication has been deleted successfully.", Alert.AlertType.INFORMATION);
                loadFabricationData();
            } catch (Exception e) {
                showAlert("Error", "An error occurred while deleting the fabrication: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Selection", "Please select a fabrication to delete.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleAddComponent() {
        Fabricacion selectedFabrication = fabricationTable.getSelectionModel().getSelectedItem();
        if (selectedFabrication != null) {
            try {
                Long empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
                DetallesFabricacion detalles = new DetallesFabricacion();
                detalles.setComponente(componentNameComboBox.getValue());
                detalles.setId(new DetallesFabricacionId(selectedFabrication.getId(), componentNameComboBox.getValue().getId()));
                detalles.setFabricacion(selectedFabrication);
                detalles.setCantidadUtilizada(usedQuantitySpinner.getValue());
                detallesFabricacionDAO.guardar(detalles, empresaId);
                showAlert("Add Successful", "The component has been added successfully.", Alert.AlertType.INFORMATION);
                loadComponentData(selectedFabrication.getId());
            } catch (Exception e) {
                showAlert("Error", "An error occurred while adding the component: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Fabrication Selected", "Please select a fabrication to add components.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleUpdateComponent() {
        DetallesFabricacion selectedComponent = componentsTable.getSelectionModel().getSelectedItem();
        if (selectedComponent != null) {
            try {
                Long empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
                selectedComponent.setComponente(componentNameComboBox.getValue());
                selectedComponent.setCantidadUtilizada(usedQuantitySpinner.getValue());
                detallesFabricacionDAO.actualizar(selectedComponent, empresaId);
                showAlert("Update Successful", "The component has been updated successfully.", Alert.AlertType.INFORMATION);
                loadComponentData(selectedComponent.getFabricacion().getId());
            } catch (Exception e) {
                showAlert("Error", "An error occurred while updating the component: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Selection", "Please select a component to update.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleRemoveComponent() {
        DetallesFabricacion selectedComponent = componentsTable.getSelectionModel().getSelectedItem();
        if (selectedComponent != null) {
            try {
                Long empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
                detallesFabricacionDAO.eliminar(selectedComponent, empresaId);
                showAlert("Remove Successful", "The component has been removed successfully.", Alert.AlertType.INFORMATION);
                loadComponentData(selectedComponent.getFabricacion().getId());
            } catch (Exception e) {
                showAlert("Error", "An error occurred while removing the component: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Selection", "Please select a component to remove.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleClearSelection() {
        // Limpiar la selección de las tablas
        if (fabricationTable != null) {
            fabricationTable.getSelectionModel().clearSelection();
        }
        if (componentsTable != null) {
            componentsTable.getSelectionModel().clearSelection();
        }

        // Reiniciar los ComboBox
        if (ProductoFabricationCombobox != null) {
            ProductoFabricationCombobox.setValue(null);  // Cambiar clearSelection por setValue(null)
        }
        if (componentNameComboBox != null) {
            componentNameComboBox.setValue(null);  // Cambiar clearSelection por setValue(null)
        }

        // Reiniciar los Spinners a su valor inicial
        if (fabricationQuantitySpinner != null) {
            fabricationQuantitySpinner.getValueFactory().setValue(1);
        }
        if (usedQuantitySpinner != null) {
            usedQuantitySpinner.getValueFactory().setValue(1);
        }

        // Reiniciar el DatePicker
        if (dateofFabrication != null) {
            dateofFabrication.setValue(null);
        }

        // Opcionalmente, puedes agregar un mensaje de alerta para confirmar la acción
        showAlert("Reset", "All selections and inputs have been reset.", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    }