package controllers;

import entity.Producto;
import funcionalidadDAO.ProductoDAOImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import utils.SessionManager;

import java.time.LocalDate;
import java.util.List;

public class NotificationController {

    @FXML
    private ListView<String> notificationList;

    public void initialize() {
        refreshNotifications();
    }

    public void refreshNotifications() {
        List<Producto> productos = new ProductoDAOImpl().obtenerProductosPorEstadoStock(SessionManager.getInstance().getCurrentUser().getEmpresa().getId(), 10); // Asumiendo umbral bajo de 10
        notificationList.getItems().clear();
        for (Producto producto : productos) {
            if (producto.getCantidadEnStock() == 0) {
                notificationList.getItems().add(producto.getNombre() + " - Out of Stock");
            } else if (producto.getCantidadEnStock() <= 10) {
                notificationList.getItems().add(producto.getNombre() + " - Low Stock");
            } else if (producto.getFechaCaducidad() != null && producto.getFechaCaducidad().isBefore(LocalDate.now().plusDays(30))) {
                notificationList.getItems().add(producto.getNombre() + " - Dead Stock");
            }
        }
    }

    @FXML
    private void handleDismiss(ActionEvent actionEvent) {
        // Cerrar ventana o limpiar notificaciones
        notificationList.getItems().clear();
    }

    @FXML
    private void handleRefresh(ActionEvent actionEvent) {
        refreshNotifications();
    }
}
