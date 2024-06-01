package controllers;

import entity.Usuario;
import funcionalidadDAO.UsuarioDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import utils.PasswordUtils;
import utils.SessionManager;

public class UserManagementController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtNombreUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private ComboBox<String> comboRol;
    @FXML private TableView<Usuario> tableViewUsers;
    @FXML private TableColumn<Usuario, String> columnNombre;
    @FXML private TableColumn<Usuario, String> columnNombreUsuario;
    @FXML private TableColumn<Usuario, String> columnRol;

    private UsuarioDAOImpl usuarioDao = new UsuarioDAOImpl();
    private ObservableList<Usuario> usuarioList = FXCollections.observableArrayList();
    Long empresaId;
    @FXML
    public void initialize() {
        empresaId = SessionManager.getInstance().getCurrentUser().getEmpresa().getId();
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnNombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        columnRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        tableViewUsers.setItems(usuarioList);
        loadUsers();

        tableViewUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNombre.setText(newSelection.getNombre());
                txtNombreUsuario.setText(newSelection.getNombreUsuario());
                txtPassword.setText(newSelection.getHashContrasena());
                txtConfirmPassword.setText(newSelection.getHashContrasena());
                comboRol.setValue(newSelection.getRol());
            }
        });
    }

    private void loadUsers() {
        usuarioList.setAll(usuarioDao.obtenerTodosUsuarios(empresaId));
    }

    @FXML
    private void handleSave() {
        if (!txtPassword.getText().equals(txtConfirmPassword.getText())) {
            showAlert("Error", "Las contraseñas no coinciden.");
            return;
        }

        Usuario usuario = tableViewUsers.getSelectionModel().getSelectedItem();
        if (usuario == null) {
            usuario = new Usuario();
        }
        usuario.setNombre(txtNombre.getText());
        usuario.setNombreUsuario(txtNombreUsuario.getText());
        usuario.setHashContrasena(PasswordUtils.hashPassword(txtPassword.getText())); // Asumiendo que usas encriptación aquí
        usuario.setRol(comboRol.getSelectionModel().getSelectedItem());


        if (usuario.getId() == null) {
            usuarioDao.guardarUsuario(usuario, empresaId);
        } else {
            usuarioDao.actualizarUsuario(usuario, empresaId);
        }
        loadUsers();
        handleClearSelection();
        showAlert("Éxito", "Usuario guardado con éxito.");
    }
    /**
     * Intenta iniciar sesión con el nombre de usuario y la contraseña proporcionados.
     */
    public boolean tryLogin(String username, String password) {
        Usuario usuario = usuarioDao.encontrarUsuarioPorNombreUsuarioLogin(username);
        if (usuario != null && PasswordUtils.verifyPassword(password, usuario.getHashContrasena())) {
            SessionManager.getInstance().setCurrentUser(usuario);
            return true;
        }
        return false;
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    /**
     * Limpia la selección actual y los campos del formulario.
     */
    @FXML
    private void handleClearSelection() {
        tableViewUsers.getSelectionModel().clearSelection();
        txtNombre.clear();
        txtNombreUsuario.clear();
        txtPassword.clear();
        txtConfirmPassword.clear();
        comboRol.getSelectionModel().clearSelection();
    }

    /**
     * Muestra un mensaje de alerta.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
