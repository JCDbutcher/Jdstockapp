package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import funcionalidadDAO.CategoriaDAOImpl;
import entity.Categoria;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.SessionManager;

import java.util.List;

public class ControllerCategoriaManagement {
    @FXML
    private TableView<Categoria> tableViewCategorias;
    @FXML
    private TableColumn<Categoria, Long> columnId;
    @FXML
    private TableColumn<Categoria, String> columnName;
    @FXML
    private TableColumn<Categoria, String> columnDescription;
    @FXML
    private TextField inputName;
    @FXML
    private TextField inputDescription;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnUpdate;

    private CategoriaDAOImpl categoriaDAO = new CategoriaDAOImpl();
    private Long empresaId;
    @FXML
    public void initialize() {
        empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
        setupTableColumns();
        loadCategoriaData();
        setupSelectionModel();
        tableViewCategorias.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                inputName.setText(newSelection.getNombre());
                inputDescription.setText(newSelection.getDescripcion());
            }
        });
    }

    private void setupTableColumns() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    }
    private void setupSelectionModel() {
        tableViewCategorias.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Categoria categoria = tableViewCategorias.getSelectionModel().getSelectedItem();
                inputName.setText(categoria.getNombre());
                inputDescription.setText(categoria.getDescripcion());
            }
        });
    }


    private void loadCategoriaData() {
        List<Categoria> categorias = categoriaDAO.buscarTodasCategorias(empresaId);
        tableViewCategorias.getItems().setAll(categorias);
    }

    @FXML
    private void handleSave() {
        try {
            Categoria selectedCategoria = tableViewCategorias.getSelectionModel().getSelectedItem();
            Categoria categoria = selectedCategoria != null ? selectedCategoria : new Categoria();
            categoria.setNombre(inputName.getText());
            categoria.setDescripcion(inputDescription.getText());
            categoria.setEmpresa(SessionManager.getInstance().getCurrentUser().getEmpresa());

            // Si la categoría seleccionada es null, significa que es una nueva entrada
            if (selectedCategoria == null) {
                categoriaDAO.guardarActualizarCategoria(categoria, empresaId);
            } else {
                // Si se seleccionó una categoría, actualízala
                categoria.setId(selectedCategoria.getId()); // Asegura que se establezca el ID para la actualización
                categoriaDAO.guardarActualizarCategoria(categoria, empresaId);
            }

            loadCategoriaData(); // Recargar datos después de guardar o actualizar
            clearInputs(); // Limpia los campos de entrada después de guardar
        } catch (Exception e) {
            showAlert("Error", "Cannot save category: " + e.getMessage());
        }
    }


    private void clearInputs() {
        inputName.clear();
        inputDescription.clear();
        tableViewCategorias.getSelectionModel().clearSelection();
    }


    @FXML
    private void handleDelete() {
        Categoria selectedCategoria = tableViewCategorias.getSelectionModel().getSelectedItem();
        if (selectedCategoria != null) {
            try {
                categoriaDAO.eliminarCategoria(selectedCategoria.getId(), empresaId);
                loadCategoriaData();
            } catch (Exception e) {
                showAlert("Error", "Cannot delete category: " + e.getMessage());
            }
        } else {
            showAlert("Error", "No category selected!");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void handleCancel() {
        clearInputs(); // Limpia todos los campos de entrada
        tableViewCategorias.getSelectionModel().clearSelection(); // Deselecciona cualquier fila seleccionada
    }

}

