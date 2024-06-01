package controllers;

import entity.Producto;
import entity.ShippingStatus;
import funcionalidadDAO.ProductoDAOImpl;
import funcionalidadDAO.TransaccionDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import utils.SessionManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.function.Supplier;

import static entity.ShippingStatus.COMPLETED;

public class ControllerDashboardInv {

    @FXML
    private Label labelGestionDeInventario;
    @FXML
    private Label lblCompleted, lblInprogress, lblReturns, lblOverdue;
    @FXML
    private ComboBox<String> comboBoxPeriodo;
    @FXML
    private Label lblStockin, lblStockout, lblStocklow, lblstockDead;
    @FXML
    private PieChart pieChartSalesOrderStage, pieChartStockStage;
    @FXML
    private Label lblInventoryTurnoverRatio,lblDaysToSellInventory,lblLeadTime,lblServiceLevel,lblAverageInventory,lblCostOfGoodsSold,lblPerfectOrderRate,lblReturnSales;

    @FXML
    public void initialize() {
        // Inicializar datos del ComboBox, PieCharts, Labels, etc.
        setupComboBox();
        assert pieChartSalesOrderStage != null : "fx:id=\"pieChartSalesOrderStage\" no fue inyectado: verifica tu archivo 'dashboardPanel.fxml'.";
        setupPiechartSalesOrderStage();
        setupPiechartInventoryStage();
        updateStatistics();
    }

    private void setupComboBox() {
        // Suponiendo que el ComboBox maneje periodos de tiempo
        comboBoxPeriodo.getItems().addAll("Mensual", "Trimestral", "Anual");
        comboBoxPeriodo.getSelectionModel().selectFirst(); // Selecciona el primer periodo por defecto
    }



    private void setupPiechartSalesOrderStage() {
        TransaccionDAOImpl transaccionDAO = new TransaccionDAOImpl();
        Long empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
        List<Object[]> datos = transaccionDAO.obtenerDatosParaPieChart(empresaId);
        pieChartSalesOrderStage.getData().clear();
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

        Map<ShippingStatus, Integer> statusCounts = new HashMap<>();

        for (Object[] resultado : datos) {
            ShippingStatus estado = (ShippingStatus) resultado[0];
            Number count = (Number) resultado[1];
            statusCounts.put(estado, count.intValue());
            chartData.add(new PieChart.Data(estado.toString(), count.doubleValue()));
        }

        pieChartSalesOrderStage.setData(chartData);

        // Actualizar las etiquetas según los datos
        lblCompleted.setText(String.valueOf(statusCounts.getOrDefault(ShippingStatus.COMPLETED, 0)));
        lblInprogress.setText(String.valueOf(statusCounts.getOrDefault(ShippingStatus.IN_PROGRESS, 0)));
        lblReturns.setText(String.valueOf(statusCounts.getOrDefault(ShippingStatus.RETURNS, 0)));
        lblOverdue.setText(String.valueOf(statusCounts.getOrDefault(ShippingStatus.OVERDUE, 0)));

        chartData.forEach(data -> {
            switch (ShippingStatus.valueOf(data.getName())) {
                case COMPLETED:
                    data.getNode().setStyle("-fx-pie-color: #4caf50;");
                    break;
                case IN_PROGRESS:
                    data.getNode().setStyle("-fx-pie-color: #9c27b0;");
                    break;
                case RETURNS:
                    data.getNode().setStyle("-fx-pie-color: Orange;");
                    break;
                case OVERDUE:
                    data.getNode().setStyle("-fx-pie-color: #f44336;");
                    break;
            }
        });
    }


    public void setupPiechartInventoryStage() {
        ProductoDAOImpl productoDAO = new ProductoDAOImpl();
        Long empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();

        List<Producto> productos = productoDAO.obtenerProductosPorEstadoStock(empresaId, 10, 30);

        int inStock = 0, lowStock = 0, outStock = 0, deadStock = 0, nearExpiration = 0;
        LocalDate today = LocalDate.now();

        for (Producto producto : productos) {
            if (producto.getCantidadEnStock() > 10) {
                inStock++;
            } else if (producto.getCantidadEnStock() > 0) {
                lowStock++;
            } else {
                outStock++;
            }

            if (producto.getEsPerecedero() && producto.getFechaCaducidad() != null && producto.getFechaCaducidad().isBefore(today.plusDays(30))) {
                nearExpiration++;
                deadStock++;
            }
        }

        pieChartStockStage.getData().setAll(
                new PieChart.Data("In Stock Items", inStock),
                new PieChart.Data("Low Stock Items", lowStock),
                new PieChart.Data("Out of Stock Items", outStock),
                new PieChart.Data("Dead Stock Items", deadStock)
        );

        lblStockin.setText(String.valueOf(inStock));
        lblStockout.setText(String.valueOf(outStock));
        lblStocklow.setText(String.valueOf(lowStock));
        lblstockDead.setText(String.valueOf(deadStock));
    }


    private void applyCustomColors() {
        pieChartStockStage.getData().get(0).getNode().setStyle("-fx-pie-color: #9c27b0;");
        pieChartStockStage.getData().get(1).getNode().setStyle("-fx-pie-color: #f44336;");
        pieChartStockStage.getData().get(2).getNode().setStyle("-fx-pie-color: Orange;");
        pieChartStockStage.getData().get(3).getNode().setStyle("-fx-pie-color: #4caf50;");
    }

    @FXML
    private void updateStatistics() {
        Long empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();

        try {
            BigDecimal inventoryTurnoverRatio = ProductoDAOImpl.calculateInventoryTurnoverRatio(empresaId);
            setLabel(lblInventoryTurnoverRatio, inventoryTurnoverRatio);

            BigDecimal daysToSellInventory = ProductoDAOImpl.calculateDaysToSellInventory(empresaId);
            setLabel(lblDaysToSellInventory, daysToSellInventory);

            BigDecimal leadTime = TransaccionDAOImpl.calculateLeadTime(empresaId);
            setLabel(lblLeadTime, leadTime);

            BigDecimal perfectOrder = TransaccionDAOImpl.calculatePerfectOrderRate(empresaId);
            setLabel(lblPerfectOrderRate, perfectOrder);

            BigDecimal rateOfReturn = TransaccionDAOImpl.calculateRateOfReturn(empresaId);
            setLabel(lblReturnSales, rateOfReturn);

            BigDecimal serviceLevel = TransaccionDAOImpl.calculateServiceLevel(empresaId).multiply(BigDecimal.valueOf(100));
            setLabel(lblServiceLevel, serviceLevel, "%");

            BigDecimal averageInventory = ProductoDAOImpl.calculateAverageInventory(empresaId);
            setLabel(lblAverageInventory, averageInventory);

            BigDecimal costOfGoodsSold = ProductoDAOImpl.calculateCostOfGoodsSold(empresaId);
            setLabel(lblCostOfGoodsSold, costOfGoodsSold);
        } catch (Exception e) {
            System.out.println("Error updating statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void setLabel(Label label, BigDecimal value, String suffix) {
        DecimalFormat df = new DecimalFormat("0.00"); // Formato con dos decimales
        label.setText(df.format(value) + (suffix != null ? suffix : ""));
    }

    private void setLabel(Label label, BigDecimal value) {
        setLabel(label, value, null);
    }








    // Método para manejar acciones, como cambios en el ComboBox
    @FXML
    private void onPeriodChanged() {

        System.out.println("Periodo seleccionado: " + comboBoxPeriodo.getSelectionModel().getSelectedItem());

    }




}
