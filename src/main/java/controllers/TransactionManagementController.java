package controllers;

import entity.Transaccion;
import entity.DetalleTransaccion;
import funcionalidadDAO.DetalleTransaccionDAOImpl;
import funcionalidadDAO.TransaccionDAOImpl;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import utils.EmailSender;
import utils.SessionManager;
import utils.TransactionEmailService;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static utils.EmailSender.sendInvoiceEmail;
import static utils.TransactionEmailService.generateInvoiceHtml;

public class TransactionManagementController {
    @FXML private TableView<Transaccion> transactionTable;
    @FXML private TableColumn<Transaccion, Long> columnId;
    @FXML private TableColumn<Transaccion, String> clienteNombreColumn;
    @FXML private TableColumn<Transaccion, String> columnType;
    @FXML private TableColumn<Transaccion, Boolean> columnEsPerfect;
    @FXML private TableColumn<Transaccion, String> columnOrderDate;
    @FXML private TableColumn<Transaccion, String> columnDeadline;
    @FXML private TableColumn<Transaccion, String> columnShipping;
    @FXML private TextField searchField;

    @FXML private TableView<DetalleTransaccion> transactionDetailTable;
    @FXML private TableColumn<DetalleTransaccion, Long> columnDetailId;
    @FXML private TableColumn<DetalleTransaccion, Long> columnTransId;
    @FXML private TableColumn<DetalleTransaccion, String> columnProdName;
    @FXML private TableColumn<DetalleTransaccion, Integer> columnQuantity;
    @FXML private TableColumn<DetalleTransaccion, Double> columnPricePerItem;
    @FXML private TableColumn<DetalleTransaccion, Double> columnDiscount;

    private TransaccionDAOImpl transaccionDAO = new TransaccionDAOImpl();
    private DetalleTransaccionDAOImpl detalleTransaccionDAO = new DetalleTransaccionDAOImpl();
    private Long empresaId;

    @FXML
    public void initialize() {
        empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
        setupTransactionTableColumns();
        setupTransactionDetailTableColumns();
        loadTransactionData();
        loadAllTransactionDetails();
    }


    private void setupTransactionTableColumns() {
        // Configuración de las columnas de la tabla de transacciones
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        clienteNombreColumn.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        columnType.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        columnEsPerfect.setCellValueFactory(new PropertyValueFactory<>("esPerfecto"));
        columnOrderDate.setCellValueFactory(new PropertyValueFactory<>("fechaPedido"));
        columnDeadline.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
        columnShipping.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getEstadoEnvio().getLabel()));

        // Listener para cambios de selección en la tabla de transacciones
        transactionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                handleTransactionSelectionChanged();
            }
        });

        // Formatear columnas específicas
        columnEsPerfect.setCellFactory(column -> new TableCell<Transaccion, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Yes" : "No");
                }
            }
        });

        columnOrderDate.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getFechaPedido();
            return new ReadOnlyStringWrapper(date != null ? date.format(DateTimeFormatter.ISO_LOCAL_DATE) : "");
        });
    }

    private void setupTransactionDetailTableColumns() {
        // Configuración de las columnas de la tabla de detalles de transacciones
        columnDetailId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnTransId.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTransaccion().getId()));
        columnProdName.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getProducto().getNombre()));
        columnQuantity.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnPricePerItem.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        columnDiscount.setCellValueFactory(new PropertyValueFactory<>("descuento"));
    }
    private void loadAllTransactionDetails() {
        List<DetalleTransaccion> allDetails = detalleTransaccionDAO.buscarTodosPorEmpresa(empresaId);
        System.out.println("All transaction details found: " + allDetails.size()); // Debugging
        transactionDetailTable.setItems(FXCollections.observableArrayList(allDetails));
    }
    private void loadTransactionData() {
        List<Transaccion> transactions = transaccionDAO.obtenerTodos(empresaId);
        System.out.println("Transactions found: " + transactions.size()); // Debugging
        transactionTable.setItems(FXCollections.observableArrayList(transactions));
    }

    private void loadTransactionDetailData(Long transactionId) {
        List<DetalleTransaccion> details = detalleTransaccionDAO.buscarPorTransaccion(transactionId, empresaId);
        System.out.println("Transaction details found: " + details.size()); // Debugging
        transactionDetailTable.setItems(FXCollections.observableArrayList(details));
    }


    @FXML
    private void handleAddTransaction(ActionEvent event) {
        openTransactionForm("/uifxml/AddTransactionForm.fxml", "Add New Transaction");
        loadTransactionData();
    }

    @FXML
    private void handleEditTransaction(ActionEvent event) {
        Transaccion selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction != null) {
            openTransactionForm("/uifxml/EditTransactionForm.fxml", "Edit Transaction", selectedTransaction);
            loadTransactionData();
            loadTransactionDetailData(selectedTransaction.getId());
        } else {
            showAlert("Error", "No transaction selected.");
        }
    }

    @FXML
    private void handleDeleteTransaction(ActionEvent event) {
        Transaccion selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "No transaction selected.");
            return;
        }

        // Verificar si la transacción tiene detalles asociados
        boolean hasDetails = !detalleTransaccionDAO.buscarPorTransaccion(selected.getId(), empresaId).isEmpty();

        if (hasDetails) {
            // Mostrar un diálogo de confirmación si hay detalles asociados
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("This transaction has associated details.");
            alert.setContentText("Deleting this transaction will also delete all associated details. Are you sure you want to proceed?");

            ButtonType buttonTypeDelete = new ButtonType("Delete All");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonTypeDelete, buttonTypeCancel);

            alert.showAndWait().ifPresent(response -> {
                if (response == buttonTypeDelete) {
                    // Eliminar transacción y detalles
                    try {
                        detalleTransaccionDAO.eliminar(selected.getId(), empresaId);
                        transaccionDAO.eliminar(selected.getId(), empresaId);
                        loadTransactionData();
                        showAlert("Deleted", "Transaction and all associated details were deleted successfully.");
                    } catch (Exception e) {
                        showAlert("Error", "Failed to delete transaction and details: " + e.getMessage());
                    }
                }
            });
        } else {
            // Si no hay detalles, simplemente eliminar la transacción
            try {
                transaccionDAO.eliminar(selected.getId(), empresaId);
                loadTransactionData();
                showAlert("Deleted", "Transaction was deleted successfully.");
            } catch (Exception e) {
                showAlert("Error", "Failed to delete transaction: " + e.getMessage());
            }
        }
    }


    private void openTransactionForm(String fxmlPath, String title) {
        openTransactionForm(fxmlPath, title, null);
    }

    private void openTransactionForm(String fxmlPath, String title, Transaccion transaction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (transaction != null) {
                EditTransactionController controller = loader.getController();
                controller.setTransaction(transaction);
                controller.setTransactionManagementController(this);
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the form.");
        }
    }

    @FXML
    private void handleSearchAction(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase();
        ObservableList<Transaccion> filteredList = transactionTable.getItems().stream()
                .filter(t -> t.toString().toLowerCase().contains(searchText))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        transactionTable.setItems(filteredList);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public void refreshTable() {
        loadTransactionData();
    }

    @FXML
    public void handleTransactionSelectionChanged() {
        Transaccion selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction != null) {
            loadTransactionDetailData(selectedTransaction.getId());
        } else {
            transactionDetailTable.getItems().clear();
        }
    }


    public void handleAddDetail(ActionEvent actionEvent) {
        Transaccion selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/uifxml/AddDetailForm.fxml"));
                Parent root = loader.load();

                AddDetailController controller = loader.getController();
                // Initialize with the selected transaction
                controller.setCurrentTransaction(transaccionDAO.encontrar(selectedTransaction.getId(), empresaId));

                Stage stage = new Stage();
                stage.setTitle("Add Detail");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                loadTransactionDetailData(selectedTransaction.getId());
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load the add detail form.");
            }
        } else {
            showAlert("No Transaction Selected", "Please select a transaction first.");
        }
    }

    public void handleEditDetail(ActionEvent actionEvent) {
        DetalleTransaccion selectedDetail = transactionDetailTable.getSelectionModel().getSelectedItem();
        if (selectedDetail != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/uifxml/EditDetailForm.fxml"));
                Parent root = loader.load();

                EditDetailController controller = loader.getController();
                // Initialize with the selected detail, ensuring all necessary entities are fully loaded
                controller.setDetail(detalleTransaccionDAO.encontrar(selectedDetail.getId(), empresaId));

                Stage stage = new Stage();
                stage.setTitle("Edit Detail");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                loadTransactionDetailData(selectedDetail.getTransaccion().getId());
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load the edit detail form.");
            }
        } else {
            showAlert("No Detail Selected", "Please select a detail first.");
        }
    }



    @FXML
    public void handleDeleteDetail(ActionEvent event) {
        DetalleTransaccion selectedDetail = transactionDetailTable.getSelectionModel().getSelectedItem();
        if (selectedDetail != null) {
            detalleTransaccionDAO.eliminar(selectedDetail.getId(), empresaId);
            Transaccion selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
            loadAllTransactionDetails();
            if (selectedTransaction != null) {
                loadTransactionDetailData(selectedTransaction.getId());
            }
        }
    }


    @FXML
    public void handleSendInvoice(ActionEvent actionEvent) {
        // Get the selected transaction from the table
        Transaccion selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction == null) {
            showAlert("Error", "No transaction selected.");
            return;
        }

        try {
            // Fetch the details associated with the transaction
            List<DetalleTransaccion> detalles = detalleTransaccionDAO.buscarPorTransaccion(selectedTransaction.getId(), empresaId);

            // Generate the HTML content for the invoice
            String htmlContent = TransactionEmailService.generateInvoiceHtml(selectedTransaction, detalles);

            // Email parameters
            String toAddress = selectedTransaction.getCliente().getEmail();
            String subject = "Invoice for Transaction ID: " + selectedTransaction.getId();

            // Send the email
            EmailSender.sendInvoiceEmail(toAddress, subject, htmlContent);

            System.out.println("Invoice sent successfully!");
        } catch (MessagingException e) {
            System.err.println("Error sending invoice email: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error processing invoice email: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

