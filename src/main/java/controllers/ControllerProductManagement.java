package controllers;

import entity.Categoria;
import entity.Proveedor;
import funcionalidadDAO.CategoriaDAOImpl;
import funcionalidadDAO.ProveedorDAOImpl;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import utils.SessionManager;
import funcionalidadDAO.ProductoDAOImpl;
import entity.Producto;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class ControllerProductManagement {
    @FXML private TableView<Producto> tableViewProducts;
    @FXML private TableColumn<Producto, Long> columnId;
    @FXML private TableColumn<Producto, String> columnName;
    @FXML private TableColumn<Producto, String> columnDescription;
    @FXML private TableColumn<Producto, BigDecimal> columnPrice;
    @FXML private TableColumn<Producto, Integer> columnQuantity;
    @FXML private TableColumn<Producto, String> columnSKU;
    @FXML private TableColumn<Producto, Categoria> columnCategory;
    @FXML private TableColumn<Producto, Proveedor> columnSupplier;
    @FXML private TableColumn<Producto, Boolean> columnPerishable;
    @FXML private TableColumn<Producto, LocalDate> columnExpiration;
    @FXML private TextField inputName;
    @FXML private TextField inputDescription;
    @FXML private TextField inputPrice;
    @FXML private TextField inputQuantity;
    @FXML private TextField inputSKU;
    @FXML private ComboBox<Categoria> inputCategory;
    @FXML private ComboBox<Proveedor> inputSupplier;
    @FXML private CheckBox inputPerishable;
    @FXML private DatePicker inputExpiration;
    @FXML private Button btnAddUpdate;
    @FXML private Button btnDelete;

    private ProductoDAOImpl productoDAO = new ProductoDAOImpl();
    private Long empresaId;

    @FXML
    public void initialize() {
        empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
        setupTableColumns();
        loadProductData();
        setupSelectionModel();
        loadSupplierData();
        loadCategoryData();
        setupCategoryComboBox();
        setupSupplierComboBox();
    }

    private void setupTableColumns() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnQuantity.setCellValueFactory(new PropertyValueFactory<>("cantidadEnStock"));
        columnSKU.setCellValueFactory(new PropertyValueFactory<>("sku"));

        // Configura correctamente las columnas para Categoría y Proveedor
        columnCategory.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        columnSupplier.setCellValueFactory(new PropertyValueFactory<>("proveedor"));

        // Ajusta la forma en que se muestra la información en la tabla
        columnCategory.setCellFactory(column -> new TableCell<Producto, Categoria>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre());
                }
            }
        });

        columnSupplier.setCellFactory(column -> new TableCell<Producto, Proveedor>() {
            @Override
            protected void updateItem(Proveedor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre());
                }
            }
        });

        columnPerishable.setCellValueFactory(new PropertyValueFactory<>("esPerecedero"));
        columnExpiration.setCellValueFactory(new PropertyValueFactory<>("fechaCaducidad"));
    }


    private void loadProductData() {
        try {
            List<Producto> productos = productoDAO.obtenerTodos(empresaId);
            tableViewProducts.getItems().setAll(productos);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error al cargar productos", e.getMessage());
        }
    }


    private void setupSelectionModel() {
        tableViewProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Producto producto = tableViewProducts.getSelectionModel().getSelectedItem();
                inputName.setText(producto.getNombre());
                inputDescription.setText(producto.getDescripcion());
                inputPrice.setText(producto.getPrecio().toString());
                inputQuantity.setText(String.valueOf(producto.getCantidadEnStock()));
                inputSKU.setText(producto.getSku());
                inputPerishable.setSelected(producto.getEsPerecedero());
                inputExpiration.setValue(producto.getFechaCaducidad());
                inputCategory.getSelectionModel().select(producto.getCategoria());
                inputSupplier.getSelectionModel().select(producto.getProveedor());
            } else {
                clearInputs();
            }
        });
    }

    @FXML
    private void handleAddUpdate(ActionEvent event) {
        Producto producto = tableViewProducts.getSelectionModel().getSelectedItem();
        if (producto == null) {
            producto = new Producto();
        }

        try {
            producto.setEmpresa(SessionManager.getInstance().getCurrentUser().getEmpresa());
            producto.setNombre(inputName.getText());
            producto.setDescripcion(inputDescription.getText());
            producto.setPrecio(new BigDecimal(inputPrice.getText()));
            producto.setCantidadEnStock(Integer.parseInt(inputQuantity.getText()));
            producto.setSku(inputSKU.getText());
            producto.setEsPerecedero(inputPerishable.isSelected());
            producto.setFechaCaducidad(inputExpiration.getValue());
            producto.setCategoria(inputCategory.getValue());
            producto.setProveedor(inputSupplier.getValue());

            if (producto.getId() == null) {
                productoDAO.guardar(producto, empresaId);
            } else {
                productoDAO.actualizar(producto, empresaId);
            }
            refreshTable();
        } catch (Exception e) {
            showAlert("Error", "Cannot add/update product: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Producto selectedProduct = tableViewProducts.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            productoDAO.eliminar(selectedProduct.getId(), empresaId);
            refreshTable();
        } else {
            showAlert("Error", "No product selected!");
        }
    }

    private void refreshTable() {
        loadProductData();  // Recargar los datos después de una adición/eliminación
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        clearInputs(); // Limpia todos los campos de entrada
        tableViewProducts.getSelectionModel().clearSelection(); // Deselecciona cualquier fila seleccionada
    }

    private void clearInputs() {
        inputName.clear();
        inputDescription.clear();
        inputPrice.clear();
        inputQuantity.clear();
        inputSKU.clear();
        inputPerishable.setSelected(false);
        inputExpiration.setValue(null);
        inputCategory.getSelectionModel().clearSelection();
        inputSupplier.getSelectionModel().clearSelection();
    }
    private void loadCategoryData() {
        List<Categoria> categorias = new CategoriaDAOImpl().buscarTodasCategorias(empresaId);
        if (categorias != null) {
            inputCategory.getItems().clear();
            inputCategory.getItems().addAll(categorias);
        } else {
            System.out.println("No se encontraron categorías.");
        }
    }

    private void loadSupplierData() {
        List<Proveedor> proveedores = new ProveedorDAOImpl().obtenerTodosLosProveedores(empresaId);
        if (proveedores != null) {
            inputSupplier.getItems().clear();
            inputSupplier.getItems().addAll(proveedores);
        } else {
            System.out.println("No se encontraron proveedores.");
        }
    }


    private void setupCategoryComboBox() {
        inputCategory.setCellFactory(lv -> new ListCell<Categoria>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });

        inputCategory.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria categoria) {
                return categoria == null ? null : categoria.getNombre();
            }

            @Override
            public Categoria fromString(String string) {
                // Esto es solo necesario si el ComboBox es editable, lo que permite al usuario introducir texto
                return inputCategory.getItems().stream()
                        .filter(item -> item.getNombre().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    private void setupSupplierComboBox() {
        inputSupplier.setCellFactory(lv -> new ListCell<Proveedor>() {
            @Override
            protected void updateItem(Proveedor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre());
            }
        });

        inputSupplier.setConverter(new StringConverter<Proveedor>() {
            @Override
            public String toString(Proveedor object) {
                return object == null ? null : object.getNombre();
            }

            @Override
            public Proveedor fromString(String string) {
                return inputSupplier.getItems().stream()
                        .filter(item -> item.getNombre().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }
    @FXML
    private void handleOpenCategoryManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uifxml/ManageCategory.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open the category management view.");
        }
    }
    @FXML
    private void handleGenerateBarcode(ActionEvent event) {
        Producto selectedProduct = tableViewProducts.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                Code128Bean barcode128 = new Code128Bean();
                final int dpi = 160;
                barcode128.setModuleWidth(UnitConv.in2mm(2.8f / dpi));

                // Especifica el directorio donde se guardará el archivo
                String barcodeDirectory = "barcodes";
                // Crea el directorio si no existe
                File directory = new File(barcodeDirectory);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Define la ruta completa del archivo
                File barcodeFile = Paths.get(barcodeDirectory, selectedProduct.getSku() + ".png").toFile();
                FileOutputStream fos = new FileOutputStream(barcodeFile);
                BitmapCanvasProvider canvas = new BitmapCanvasProvider(fos, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
                barcode128.generateBarcode(canvas, selectedProduct.getSku());
                canvas.finish();

                showAlert("Success", "Barcode generated successfully. Saved as " + barcodeFile.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Error", "Failed to generate barcode: " + e.getMessage());
            }
        } else {
            showAlert("Error", "No product selected!");
        }

}
}
