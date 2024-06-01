package controllers;

import entity.Cliente;
import funcionalidadDAO.ClienteDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.JPAUtil;
import utils.SessionManager;

import java.io.IOException;

public class ControllerCustomerManagement {
    @FXML private TableView<Cliente> customerTable;
    @FXML private TableColumn<Cliente, Long> columnId;
    @FXML private TableColumn<Cliente, String> columnName;
    @FXML private TableColumn<Cliente, String> columnAddress;
    @FXML private TableColumn<Cliente, String> columnPhone;
    @FXML private TableColumn<Cliente, String> columnEmail;
    @FXML private TableColumn<Cliente, String> columnNIF;
    @FXML private TextField searchField, nameField, addressField, phoneField, emailField,nifFIeld;
    @FXML private Button updateButton, deleteButton, clearButton,btnNewCustumer;

    private ClienteDAOImpl clienteDAO = new ClienteDAOImpl();
    private ObservableList<Cliente> clienteList = FXCollections.observableArrayList();
    private Long empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadClientes();
        setupSelectionModel();
    }

    private void setupTableColumns() {
        // Configurar las columnas utilizando PropertyValueFactory para vincular los datos
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnAddress.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnNIF.setCellValueFactory(new PropertyValueFactory<>("nif"));
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    protected void loadClientes() {
        clienteList.setAll(clienteDAO.buscarTodosClientes(empresaId));
        customerTable.setItems(clienteList);
    }

    private void setupSelectionModel() {
        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillInputFields(newSelection);
            }
        });
    }

    private void fillInputFields(Cliente cliente) {
        nameField.setText(cliente.getNombre());
        addressField.setText(cliente.getDireccion());
        phoneField.setText(cliente.getTelefono());
        emailField.setText(cliente.getEmail());
        nifFIeld.setText(cliente.getNif());
    }

    @FXML
    private void handleUpdateAction(ActionEvent event) {
        Cliente selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            selected = new Cliente(); // Crear nuevo cliente si no hay selección
        }

        selected.setNombre(nameField.getText());
        selected.setDireccion(addressField.getText());
        selected.setTelefono(phoneField.getText());
        selected.setEmail(emailField.getText());
        selected.setNif(nifFIeld.getText());
        clienteDAO.guardarActualizarCliente(selected, empresaId);
        loadClientes();
    }

    @FXML
    private void handleDeleteAction(ActionEvent event) {
        Cliente selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            clienteDAO.eliminarCliente(selected.getId(), empresaId);
            loadClientes();
        }
    }

    @FXML
    private void handleClearAction(ActionEvent event) {
        customerTable.getSelectionModel().clearSelection();
        nameField.clear();
        addressField.clear();
        phoneField.clear();
        emailField.clear();
        nifFIeld.clear();
    }

    @FXML
    private void handleSearchAction(ActionEvent event) {
        String searchText = searchField.getText();
        if (searchText != null && !searchText.isEmpty()) {
            ObservableList<Cliente> filteredList = FXCollections.observableArrayList(
                    clienteDAO.buscarClientesPorNombre(searchText, empresaId)
            );
            customerTable.setItems(filteredList);
        } else {
            loadClientes();
        }
    }

    @FXML
    private void handleNewCustomerAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/uifxml/NewCustomer.fxml"));
            Parent root = fxmlLoader.load();

            NewCustomerController newCustomerController = fxmlLoader.getController();
            newCustomerController.setMainController(this);  // Establece la referencia

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("New Customer");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el archivo FXML: " + e.getMessage());
        }
    }



}

