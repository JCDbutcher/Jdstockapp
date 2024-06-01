package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ReportesYFacturasController {

    @FXML
    private TextField clienteTextField;
    @FXML
    private DatePicker fechaDatePicker;
    @FXML
    private TextField cantidadProductosTextField;
    @FXML
    private TextArea detalleTextArea;
    @FXML
    private TextField totalTextField;
    @FXML
    private TextArea previewTextArea;

    @FXML
    private void handleGenerarReporte() {
        // Implementa la lógica para generar el reporte
        String reporte = generarReporte();
        previewTextArea.setText(reporte);
    }

    @FXML
    private void handleGenerarFactura() {
        // Implementa la lógica para generar la factura
        String factura = generarFactura();
        previewTextArea.setText(factura);
    }

    private String generarReporte() {
        return "Reporte:\n" +
                "Cliente: " + clienteTextField.getText() + "\n" +
                "Fecha: " + fechaDatePicker.getValue() + "\n" +
                "Cantidad de Productos: " + cantidadProductosTextField.getText() + "\n" +
                "Detalle: " + detalleTextArea.getText() + "\n" +
                "Total: " + totalTextField.getText();
    }

    private String generarFactura() {
        return "Factura:\n" +
                "Cliente: " + clienteTextField.getText() + "\n" +
                "Fecha: " + fechaDatePicker.getValue() + "\n" +
                "Cantidad de Productos: " + cantidadProductosTextField.getText() + "\n" +
                "Detalle: " + detalleTextArea.getText() + "\n" +
                "Total: " + totalTextField.getText();
    }
}
