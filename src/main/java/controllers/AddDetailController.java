package controllers;

import entity.DetalleTransaccion;
import entity.Producto;
import entity.Transaccion;
import funcionalidadDAO.DetalleTransaccionDAOImpl;
import funcionalidadDAO.ProductoDAOImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import utils.SessionManager;

import java.math.BigDecimal;

public class AddDetailController {

    @FXML private ComboBox<Producto> productComboBox;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private TextField pricePerItemField;
    @FXML private TextField discountField;

    private DetalleTransaccionDAOImpl detalleTransaccionDAO = new DetalleTransaccionDAOImpl();
    private ProductoDAOImpl productoDAO = new ProductoDAOImpl();
    private Transaccion currentTransaction;
    private Long empresaId;
    public void setCurrentTransaction(Transaccion transaccion) {

        this.currentTransaction = transaccion;
        productComboBox.setItems(FXCollections.observableArrayList(productoDAO.obtenerTodos(transaccion.getEmpresa().getId())));
    }

    @FXML
    public void initialize() {
        empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
        setupProductComboBox();
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
    }

    private void setupProductComboBox() {
        productComboBox.setCellFactory(lv -> new ListCell<Producto>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre());
            }
        });
        productComboBox.setConverter(new StringConverter<Producto>() {
            @Override
            public String toString(Producto producto) {
                return producto == null ? "" : producto.getNombre();
            }

            @Override
            public Producto fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void handleAddDetailAction() {
        Producto selectedProduct = productComboBox.getValue();
        int quantity = quantitySpinner.getValue();
        BigDecimal pricePerItem = new BigDecimal(pricePerItemField.getText());
        BigDecimal discount = new BigDecimal(discountField.getText());

        if (selectedProduct == null) {
            showAlert("Error", "No product selected.");
            return;
        }

        // Actualizar el stock del producto
        int updatedStock = (currentTransaction.getTipo().equals("Venta")) ? selectedProduct.getCantidadEnStock() - quantity : selectedProduct.getCantidadEnStock() + quantity;
        selectedProduct.setCantidadEnStock(updatedStock);
        productoDAO.actualizar(selectedProduct,empresaId);

        // Crear y guardar el detalle de la transacción
        DetalleTransaccion detail = new DetalleTransaccion();
        detail.setTransaccion(currentTransaction);
        detail.setProducto(selectedProduct);
        detail.setCantidad(quantity);
        detail.setPrecioUnitario(pricePerItem);
        detail.setDescuento(discount);
        detalleTransaccionDAO.guardar(detail, currentTransaction.getEmpresa().getId());

        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) productComboBox.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

