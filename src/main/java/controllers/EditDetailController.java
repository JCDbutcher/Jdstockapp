package controllers;

import entity.DetalleTransaccion;
import entity.Producto;
import funcionalidadDAO.DetalleTransaccionDAOImpl;
import funcionalidadDAO.ProductoDAOImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.util.List;

public class EditDetailController {

    @FXML private ComboBox<Producto> productComboBox;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private TextField pricePerItemField;
    @FXML private TextField discountField;

    private DetalleTransaccionDAOImpl detalleTransaccionDAO = new DetalleTransaccionDAOImpl();
    private ProductoDAOImpl productoDAO = new ProductoDAOImpl();
    private DetalleTransaccion currentDetail;

    public void setDetail(DetalleTransaccion detail) {
        this.currentDetail = detail;
        setupProductComboBox();
        List<Producto> productos = productoDAO.obtenerTodos(detail.getTransaccion().getEmpresa().getId());
        productComboBox.setItems(FXCollections.observableArrayList(productos));
        productComboBox.setValue(detail.getProducto());
        quantitySpinner.getValueFactory().setValue(detail.getCantidad());
        pricePerItemField.setText(detail.getPrecioUnitario().toString());
        discountField.setText(detail.getDescuento().toString());
    }

    @FXML
    public void initialize() {
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
    }

    @FXML
    private void handleUpdateDetailAction() {
        currentDetail.setProducto(productComboBox.getValue());
        currentDetail.setCantidad(quantitySpinner.getValue());
        currentDetail.setPrecioUnitario(new BigDecimal(pricePerItemField.getText()));
        currentDetail.setDescuento(new BigDecimal(discountField.getText()));

        detalleTransaccionDAO.actualizar(currentDetail, currentDetail.getTransaccion().getEmpresa().getId());
        closeStage();
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
                // This is not needed typically when editing existing items
                return null;
            }
        });
    }

    private void closeStage() {
        Stage stage = (Stage) productComboBox.getScene().getWindow();
        stage.close();
    }
}
